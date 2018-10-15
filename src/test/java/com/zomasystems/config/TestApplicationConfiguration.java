/**
 * @author rahul
 * @created 29/09/2018
 */
package com.zomasystems.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@TestConfiguration
public class TestApplicationConfiguration {

    private static final String IMG_SOURCE = "195653.jpg";
    private static final String IMG_TARGET = "195653_Target.jpg";

    private static final String IMG_MAN_PEEKING_INSIDE = "ManPeekingInside.jpeg";
    private static final String IMG_MAN_STEPPING_IN_DOOR = "ManSteppingInDoor.jpeg";
    private static final String IMG_MAN_WALKING_OUT = "ManWalkingOut.jpeg";
    private static final String IMG_MAN_WALKING_TO_DOOR = "ManWalkingToDoor.jpg";
    private static final String IMG_MAN_WALKING_TO_DOOR_WITH_CANE = "ManWalkingToDoorWithCane.jpg";

    private ClassPathResource sourceResource = new ClassPathResource(IMG_SOURCE);
    private ClassPathResource targetResource = new ClassPathResource(IMG_TARGET);
    private ClassPathResource manPeekingInsideResource = new ClassPathResource(IMG_MAN_PEEKING_INSIDE);
    private ClassPathResource manSteppingInDoorResource = new ClassPathResource(IMG_MAN_STEPPING_IN_DOOR);
    private ClassPathResource manWalkingOutResource = new ClassPathResource(IMG_MAN_WALKING_OUT);
    private ClassPathResource manWalkingToDoorResource = new ClassPathResource(IMG_MAN_WALKING_TO_DOOR);
    private ClassPathResource manWalkingToDoorWithCaneResource = new ClassPathResource(IMG_MAN_WALKING_TO_DOOR_WITH_CANE);

    public ClassPathResource getSourceResource(){
        return sourceResource;
    }

    public ClassPathResource getTargetResource(){
        return targetResource;
    }


    public ClassPathResource getManPeekingInsideResource() {
        return manPeekingInsideResource;
    }

    public ClassPathResource getManSteppingInDoorResource() {
        return manSteppingInDoorResource;
    }

    public ClassPathResource getManWalkingOutResource() {
        return manWalkingOutResource;
    }

    public ClassPathResource getManWalkingToDoorResource() {
        return manWalkingToDoorResource;
    }

    public ClassPathResource getManWalkingToDoorWithCaneResource() {
        return manWalkingToDoorWithCaneResource;
    }
}
