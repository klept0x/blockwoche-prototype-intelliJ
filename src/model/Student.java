package model;

import java.util.ArrayList;

public class Student extends TheObject{


    /**
     * (private!) Constructor, creates a new object model and send it to the start station
     *
     * @param label        of the object
     * @param stationsToGo the stations to go
     * @param processtime  the processing time of the object, affects treatment by a station
     * @param speed        the moving speed of the object
     * @param xPos         x position of the object
     * @param yPos         y position of the object
     * @param image        image of the object
     */
    private Student(String label, ArrayList<String> stationsToGo, int processtime, int speed, int xPos, int yPos, String image) {
        super(label, stationsToGo, processtime, speed, xPos, yPos, image);
    }

    public static void create(String label, ArrayList<String> stationsToGo, int processtime, int speed ,int xPos, int yPos, String image){

        new Student(label, stationsToGo, processtime, speed, xPos, yPos, image);

    }
}
