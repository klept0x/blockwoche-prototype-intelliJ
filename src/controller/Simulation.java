package controller;

import io.FactoryJSON;
import io.FactoryXML;
import view.SimulationView;
import io.Factory;
import io.Statistics;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

import model.Actor;

import javax.swing.*;

import static model.Actor.getAllActors;

/**
 * The main class, controls the flow of the simulation
 *
 * @author Jaeger, Schmidt modified by Gruppe 5
 * @version 2016-07-07
 */
public class Simulation {

    /**
     * is the simulation running
     */
    public static boolean isRunning = false;

    /**
     * a speed factor for the clock to vary the speed of the clock in a simple way
     */
    public static int SPEEDFACTOR = 1;

    /**
     * the beat or speed of the clock, e.g. 300 means one beat every 300 milli seconds
     */
    public static final int CLOCKBEAT = 300 * SPEEDFACTOR;

    /**
     * the global clock
     */
    //the clock must be thread safe -> AtomicLong. The primitive type long isn't, even if synchronized
    private static AtomicLong clock = new AtomicLong(0);


    /**
     * create a Simulation object and starts the "XML or JSON" query,
     * after the query the simulation starts	 *
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {

        //a new simulation
        Simulation theSimulation = new Simulation();
        theSimulation.xmlOderJson();
        theSimulation.init();

    }


    /**
     * Show's a pop up messages where you can decide if you want to choose XML Files or JSON Files
     */
    private void xmlOderJson() {

        String[] option = {"XML", "JSON"};
        try {
            int i = JOptionPane.showOptionDialog(null, "Aus welchen Dateityp soll ausgelesen werden", "Xml oder Json", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[0]);

            if (option[i].equals("XML")) {
                //method "welchesSzenarioXML" searches in the xml folder after "Szenarien" and returns them
                welchesSzenarioXML();

                //create all stations and objects for the starting scenario out of XML
                FactoryXML.createStartScenario();
            } else if (option[i].equals("JSON")) {
                //method "welchesSzenarioJSON" searches in the json folder after "Szenarien" and returns them
                welchesSzenarioJSON();

                //create all stations and objects for the starting scenario out of JSON
                FactoryJSON.createStartScenario();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Display's the different XML-"Szenarien" in a pop up message and let the user choose which he wants to execute
     *
     * @throws IOException
     */
    private void welchesSzenarioXML() throws IOException {
        //ließt subdirectories
        Path path = Paths.get("xml");
        Statistics.show(path.toString());
        path = path.toRealPath(LinkOption.NOFOLLOW_LINKS);
        Statistics.show(path.toString());

        File file = new File(String.valueOf(path));
        String[] directories = file.list(new FilenameFilter() {
            /**
             *
             * @param current
             * @param name
             * @return
             */
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        //zeigt sie im fenster an

        try {
            int i = JOptionPane.showOptionDialog(null, "Welches Szenario?", "Szenarioauswahl", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, directories, directories[0]);

            String xmlDateiEndung = ".xml";
            //Statistics.show("xml/" + directories[i] + "/object");
            Factory.setTheObjectDataFile("xml/" + directories[i] + "/object" + xmlDateiEndung);
            Factory.setTheStartStationDataFile("xml/" + directories[i] + "/startstation" + xmlDateiEndung);
            Factory.setTheStationDataFile("xml/" + directories[i] + "/station" + xmlDateiEndung);
            Factory.setTheEndStationDataFile("xml/" + directories[i] + "/endstation" + xmlDateiEndung);
        } catch (ArrayIndexOutOfBoundsException e) {
            // e.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * Display's the different JSON-"Szenarien" in a pop up message and let the user choose which he wants to execute
     *
     * @throws IOException
     */
    private void welchesSzenarioJSON() throws IOException {
        //ließt subdirectories
        Path path = Paths.get("json");
        path = path.toRealPath(LinkOption.NOFOLLOW_LINKS);


        File file = new File(String.valueOf(path));
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        //zeigt sie im fenster an

        try {
            int i = JOptionPane.showOptionDialog(null, "Welches Szenario?", "Szenarioauswahl", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, directories, directories[0]);
            String dateiEndung = ".json";
            //Statistics.show("json/" + directories[i] + "/object"+ dateiEndung);
            FactoryJSON.setTheObjectDataFile("json/" + directories[i] + "/object" + dateiEndung);
            FactoryJSON.setTheStartStationDataFile("json/" + directories[i] + "/startstation" + dateiEndung);
            FactoryJSON.setTheStationDataFile("json/" + directories[i] + "/station" + dateiEndung);
            FactoryJSON.setTheEndStationDataFile("json/" + directories[i] + "/endstation" + dateiEndung);
        } catch (ArrayIndexOutOfBoundsException e) {
            // e.printStackTrace();
            System.exit(0);
        }

    }


    /**
     * initialize the simulation
     */
    private void init() {

        //the view of our simulation
        new SimulationView();

        // set up the the heartbeat (clock) of the simulation
        new HeartBeat().start();

        Statistics.show("---- Simulation gestartet ---\n");

        // start all the actor threads
        for (Actor actor : getAllActors()) {
            actor.start();

        }

        /*
         * Hinweis: wenn nicht �ber den Startbutton gestartet werden soll oder die Simulation ohne View laufen soll,
         * den auskommentierten Code unten verwenden
         */
				
		/*
		//Zeitpuffer vor Start -> sonst l�uft der letzte manchmal nicht los
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		//wake up the start station -> lets the simulation run
		StartStation.getStartStation().wakeUp();
		
		*/

    }


    /**
     * The heartbeat (the pulse) of the simulation, controls the clock.
     */
    private class HeartBeat extends Thread {

        @Override
        public void run() {

            while (true) {

                try {

                    Thread.sleep(CLOCKBEAT);

                    //Increase the global clock
                    clock.incrementAndGet();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    /**
     * Get the global time
     *
     * @return the global time
     */
    public static long getGlobalTime() {
        return clock.get();
    }

}
