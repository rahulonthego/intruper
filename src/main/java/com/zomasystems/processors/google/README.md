


- [Getting Started with Google AI & ML](https://cloud.google.com/ml-engine/docs/tensorflow/getting-started-training-prediction?_ga=2.133596747.-452844365.1517727035&_gac=1.117566203.1535770353.CjwKCAjwzqPcBRAnEiwAzKRgS4AOLg5sf80lQ4-Ykc6ij1567UI-R-bC3j07FtpbqC-og2dPSyZJ3RoCtdwQAvD_BwE)

- [AutoML Vision Beginner's Guide](https://cloud.google.com/vision/automl/docs/beginners-guide#evaluate)

- [AutoML Natural Language API Tutorial](https://cloud.google.com/natural-language/automl/docs/tutorial)

- [AutoML Vision API Tutorial](https://cloud.google.com/vision/automl/docs/tutorial)

FAQs
----

- How to connect?

<code>
gcloud compute --project "" ssh --zone "us-central1-c" "app-instance"
</code>
<br>
<br>
- How to copy files to your instance?
<br>
<code>
gcloud compute scp build/libs/application-0.1.0.jar -app-instance: 
</code>
<br>
<br>
- How to run the app?
<br>
<code>
AWS_REGION="us-east-1" AWS_ACCESS_KEY=  AWS_SECRET_ACCESS_KEY= SPRING_PROFILES_ACTIVE=production java -jar application-0.1.0.jar &
</code>