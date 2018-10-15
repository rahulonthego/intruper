/**
 * @author rahul
 * @created 28/09/2018
 */
package com.zomasystems.processors;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.util.IOUtils;
import com.google.common.util.concurrent.*;
import com.google.protobuf.ByteString;
import com.zomasystems.config.AwsRekognitionProperties;
import com.zomasystems.config.GoogleAutoMLProperties;
import com.zomasystems.model.InputStreamProcessResult;
import com.zomasystems.processors.aws.RekognitionDetectFacesProcessor;
import com.zomasystems.processors.aws.RekognitionDetectLabelsProcessor;
import com.zomasystems.processors.google.PredictProcessor;
import com.zomasystems.processors.google.VisionProcessor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class InputStreamProcessor {
    private static final Logger log = LoggerFactory.getLogger(InputStreamProcessor.class);

    @Autowired
    private GoogleAutoMLProperties googleAutoMLProperties;

    @Autowired
    private AwsRekognitionProperties awsRekognitionProperties;

    @Autowired
    private ImageProcessor imageProcessor;


    private static final ExecutorService cachedExecutorService = Executors.newCachedThreadPool();

    private class DetectFacesFutureCallableImpl implements FutureCallback<HashMap<String, BoundingBox>> {

        private InputStreamProcessResult processResult;

        public DetectFacesFutureCallableImpl(InputStreamProcessResult processResult) {
            System.out.println("In the constructor for detectFacesFutureCallableImpl");
            this.processResult = processResult;
        }

        @Override
        public void onSuccess(@Nullable HashMap<String, BoundingBox> result) {
            System.out.println("In the onSuccess method for detectFacesFutureCallableImpl");
            processResult.setResultingMetaData(result);
        }

        @Override
        public void onFailure(Throwable t) {
            processResult.setResultingMetaData(null);
        }
    }

    private class DetectLabelsFutureCallableImpl implements FutureCallback<String> {

        private InputStreamProcessResult processResult;

        public DetectLabelsFutureCallableImpl(InputStreamProcessResult processResult) {
            this.processResult = processResult;
        }

        @Override
        public void onSuccess(@Nullable String result) {
            System.out.println("In the onSuccess method for detectLabelsFutureCallableImpl");
            System.out.println("Labels received " + result);
            processResult.setAwsImageLabels(result);
        }

        @Override
        public void onFailure(Throwable t) {
            processResult.setAwsImageLabels(null);
        }
    }


    private class VisionFutureCallableImpl implements FutureCallback<String> {

        private InputStreamProcessResult processResult;

        public VisionFutureCallableImpl(InputStreamProcessResult processResult) {
            this.processResult = processResult;
        }

        @Override
        public void onSuccess(@javax.annotation.Nullable String result) {
            processResult.setImgLabels(result);
        }

        @Override
        public void onFailure(Throwable t) {
            log.error(" An error occured while trying to get image labels from Google's Vision API : " + t.getMessage());
            processResult.setImgLabels(null);
        }
    }

    private class PredictFutureCallableImpl implements FutureCallback<String> {

        private InputStreamProcessResult processResult;

        public PredictFutureCallableImpl(InputStreamProcessResult processResult) {
            this.processResult = processResult;
        }

        @Override
        public void onSuccess(@Nullable String result) {
            processResult.setPredictions(result);
        }

        @Override
        public void onFailure(Throwable t) {
            log.error(" An error occured while trying to get predictions from Google : " + t.getMessage());
            processResult.setImgLabels(null);
        }
    }


    /**
     * Given an input stream of image does the following:
     * 1. Use Google's AutoML model to get Google's predictions
     * 2. Use Google's Vision Processor to get Image Labels
     * 3. Use AWS's Rekognition service to detect labels
     * 4. Use Rekognition service to detect faces
     * <p>5. When all the calls complete, send the SMS with the details
     * Runs all the four steps in parallel as Listenable futures and then combines the results in
     * InputStreamProcessResult
     *
     * @param stream
     * @return InputStreamProcessResult
     */
    public InputStreamProcessResult processInputStream(InputStream stream) throws IOException {

        InputStreamProcessResult processResult = new InputStreamProcessResult();
        Image image = null;

        if (stream == null) {
            log.error("Stream passed in is null");
            processResult.setError("Stream passed in is null");
        } else {

            try {
                //////////////////////////////////////
                // Get the stream into a byte array
                /////////////////////////////////////
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(stream, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                ByteString content = ByteString.copyFrom(byteArray);
                com.google.cloud.automl.v1beta1.Image imgForGoogle = com.google.cloud.automl.v1beta1.Image.newBuilder().setImageBytes(content).build();
                com.google.cloud.vision.v1.Image imgForLabels = com.google.cloud.vision.v1.Image.newBuilder().setContent(content).build();

                ListeningExecutorService service = MoreExecutors.listeningDecorator(cachedExecutorService);

                /////////////////////////////////////////////////////////////
                //1. Use Google's AutoML model to get Google's predictions
                /////////////////////////////////////////////////////////////
                ListenableFuture getGooglePredictions = service.submit(new PredictProcessor(googleAutoMLProperties, imgForGoogle));

                PredictFutureCallableImpl predictFutureCallable = new PredictFutureCallableImpl(processResult);
                Futures.addCallback(getGooglePredictions, predictFutureCallable, service);

                /////////////////////////////////////////////////////////////
                //2. Use Google's Vision Processor to get Image Labels
                /////////////////////////////////////////////////////////////
                ListenableFuture visionProcessorFuture = service.submit(new VisionProcessor(googleAutoMLProperties, imgForLabels));
                VisionFutureCallableImpl visionFutureCallable = new VisionFutureCallableImpl(processResult);
                Futures.addCallback(visionProcessorFuture, visionFutureCallable, service);

                ByteBuffer imageBytes;
                imageBytes = ByteBuffer.wrap(byteArray);
                image = new Image().withBytes(imageBytes);

                if (image != null) {

                    processResult.setImage(image);
                    /////////////////////////////////////////////////////////////
                    //3. Use AWS's Rekognition service to detect labels
                    /////////////////////////////////////////////////////////////

                    ListenableFuture detectLabelsFuture = service.submit(new RekognitionDetectLabelsProcessor(awsRekognitionProperties.getClient(), image));
                    //callback for detectlables finish
                    DetectLabelsFutureCallableImpl futureCallableImpl = new DetectLabelsFutureCallableImpl(processResult);
                    Futures.addCallback(detectLabelsFuture, futureCallableImpl, service);

                    /////////////////////////////////////////////////////////////
                    //4. Use Rekognition service to detect faces
                    /////////////////////////////////////////////////////////////
                    ListenableFuture detectFacesFuture
                            = service.submit(new RekognitionDetectFacesProcessor(awsRekognitionProperties.getClient(), image));
                    DetectFacesFutureCallableImpl futureCallableDetectFacesImpl = new DetectFacesFutureCallableImpl(processResult);
                    Futures.addCallback(detectFacesFuture, futureCallableDetectFacesImpl, service);


                    /////////////////////////////////////////////////////////////
                    //5. When all the calls complete, send the SMS with the details
                    /////////////////////////////////////////////////////////////
                    Futures.whenAllComplete(getGooglePredictions, visionProcessorFuture,
                            detectLabelsFuture,detectFacesFuture )
                            .call(new SMSProcessor(imageProcessor, processResult), service);


                } else {
                    log.error("Image is null after extracting bytes from the input stream");
                    processResult.setError("Image is null after extracting bytes from the input stream");
                }
            } catch (Exception err) {
                log.error("Error occured while trying to call detect labels", err);
                processResult.setError("Error occured while trying to call detect labels");
            }
        }
        return processResult;
    }


    public void setGoogleAutoMLProperties(GoogleAutoMLProperties googleAutoMLProperties) {
        this.googleAutoMLProperties = googleAutoMLProperties;
    }
}
