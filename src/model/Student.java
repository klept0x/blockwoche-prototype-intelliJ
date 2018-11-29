package model;

import io.Statistics;

import java.util.ArrayList;

public class Student extends TheObject {

//    private double guthaben;

    private static ArrayList<Student> allStudentAlts = new ArrayList<Student>();

    Measurement measurement = new Measurement();

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
        //     this.guthaben = guthaben;
        //    Student.allStudentAlts.add(this);
    }


    public static void create(String label, ArrayList<String> stationsToGo, int processtime, int speed, int xPos, int yPos, String image) {

        new Student(label, stationsToGo, processtime, speed, xPos, yPos, image);
        Statistics.show("Student create() methode wurde aufgerufen");
    }

    /**
     * A (static) inner class for measurement jobs. The class records specific values of the object during the simulation.
     * These values can be used for statistic evaluation.
     */
    static class Measurement {

        /**
         * the treated time by all processing stations, in seconds
         */
        int myTreatmentTime = 0;

        double guthaben = 0.0;

    }

    /**
     * Print some statistics
     */
    protected void printStatistics() {
        super.printStatistics();
        Statistics.show("Der Student musss " + measurement.guthaben + "€ zahlen.");

    }


}

