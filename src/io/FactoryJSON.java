package io;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import view.QueueViewJPanel;
import view.QueueViewText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This is an abstract factory that creates instances
 * of actor types like objects, stations and their queues 
 * 
 * @author Neumann
 * @version 2018-11-26
 */
public class FactoryJSON {

	/**
	 * the objects Json data file
	 */
	private static String theObjectDataFile = "json/object";

	/**
	 * the stations Json data file
	 */
	private static String theStationDataFile = "json/station";

	/**
	 * the start station Json data file
	 */
	private static String theStartStationDataFile = "json/startstation";

	/**
	 * the end station Json data file
	 */
	private static String theEndStationDataFile = "json/endstation";

	/**
	 * the x position of the starting station, also position for all starting objects
	 */
	private static int XPOS_STARTSTATION;

	/**
	 * the y position of the starting station, also position for all starting objects
	 */
	private static int YPOS_STARTSTATION;


	/**
	 * create the actors for the starting scenario
	 */
	public static void createStartScenario() {

		/*NOTE: The start station must be created first,
		 * because the objects constructor puts the objects into the start stations outgoing queue
		 */
		createStartStation();
		createObjects();
		createProcessStations();
		createEndStation();
	}

	/**
	 * create the start station
	 */
	private static void createStartStation() {
		ArrayList<JSONObject> objects= new ArrayList<JSONObject>();
		//lese Datei ein und erstelle ein String
		try {
			FileReader fr = new FileReader(theStartStationDataFile);
			BufferedReader br = new BufferedReader(fr);
			String json = "";
			for (String line = ""; line != null; line = br.readLine())
				json += line;
			br.close();
			System.out.println(json + "\n");

			JSONObject j =  new JSONObject(json);
			JSONArray settings = j.getJSONArray("settings");

			objects.add((JSONObject) settings.get(0));

			JSONArray start_station = objects.get(0).getJSONArray("start_station");

			JSONObject theStartStation = start_station.getJSONObject(0);

			String label = theStartStation.getString("label");

			//Location StartStation
			XPOS_STARTSTATION=Integer.parseInt(theStartStation.getString("x_Position"));
			YPOS_STARTSTATION= Integer.parseInt(theStartStation.getString("y_Position"));



			JSONArray view = theStartStation.getJSONArray("view");
			JSONObject theview = view.getJSONObject(0);

			String image= theview.getString("image");

			JSONArray inqueue = theStartStation.getJSONArray("inqueue");
			JSONObject theInqueues = inqueue.getJSONObject(0);

			//location Inqueue
			int xInqueuePos= Integer.parseInt(theInqueues.getString("x_Position"));
			int yInqueuePos= Integer.parseInt(theInqueues.getString("y_Position"));
			//ertselle Inqueue
			SynchronizedQueue theInqueue=SynchronizedQueue.createQueue(QueueViewText.class,xInqueuePos,yInqueuePos);

			JSONArray outqueue = theStartStation.getJSONArray("outqueue");
			JSONObject theOutqueues = outqueue.getJSONObject(0);

			//location Outqueue
			int xOutqueuePos= Integer.parseInt(theOutqueues.getString("x_Position"));
			int yOutqueuePos= Integer.parseInt(theOutqueues.getString("y_Position"));
			//erstelle Outqueue
			SynchronizedQueue theOutqueue=SynchronizedQueue.createQueue(QueueViewText.class,xOutqueuePos,yOutqueuePos);
			//erstelle StartStaion
			StartStation.create(label,theInqueue,theOutqueue,XPOS_STARTSTATION,YPOS_STARTSTATION,image);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create some objects out of the Json file
	 */
	private static void createObjects() {
		ArrayList<JSONObject> objects = new ArrayList<JSONObject>();
		//Datei einlesen
		try {
			FileReader fr = new FileReader(theObjectDataFile);
			BufferedReader br = new BufferedReader(fr);
			String json = "";
			for (String line = ""; line != null; line = br.readLine())
				json += line;
			br.close();
			System.out.println(json + "\n");
			JSONObject j = new JSONObject(json);

			JSONArray settings = j.getJSONArray("settings");
			objects.add((JSONObject) settings.get(0));

			JSONArray object = objects.get(0).getJSONArray("object");

			ArrayList<TheObject> allObjects= new ArrayList<TheObject>();

			for(JSONObject theobject : toJSONList(object)){

				// data variables:
				String label = null;
				int processtime = 0;
				int speed = 0;
				String image = null;
				int schleife=0;

				label= theobject.getString("label");
				processtime= Integer.parseInt(theobject.getString("processtime"));
				speed = Integer.parseInt(theobject.getString("speed"));
				schleife= Integer.parseInt(theobject.getString("schleife"));

				JSONArray theview = theobject.getJSONArray("view");
				JSONObject view = (JSONObject) theview.get(0);
				image = view.getString("image");

				JSONArray theSequence = theobject.getJSONArray("sequence");

				ArrayList<String> stationsToGo= new ArrayList<String>();

				for (int i = 0;i<theSequence.length();i++){
					stationsToGo.add(theSequence.getString(i));
				}

				for(int i = 0; i<schleife;i++) {
					label=label+i;
					TheObject.create(label, stationsToGo, processtime, speed, XPOS_STARTSTATION, YPOS_STARTSTATION, image);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create some process stations out of the JSon file
	 */
	private static void createProcessStations() {
		ArrayList<JSONObject> station = new ArrayList<JSONObject>();
		ArrayList<JSONObject> s = new ArrayList<JSONObject>();

		//lese Datei ein und erstelle ein string
		try {
			//lese die Datei ein und erstelle String
			FileReader fr = new FileReader(theStationDataFile);
			BufferedReader br = new BufferedReader(fr);
			String json = "";
			for (String line = ""; line != null; line = br.readLine())
				json += line;
			br.close();
			System.out.println(json + "\n");


			JSONObject j = new JSONObject(json);

			JSONArray settings = j.getJSONArray("settings");
			for (Iterator i = settings.iterator(); i.hasNext(); ) {
				station.add((JSONObject) i.next());
			}
			JSONArray stations = station.get(0).getJSONArray("station");


			for (Iterator i = stations.iterator(); i.hasNext(); ) {
				s.add((JSONObject) i.next());
			}
			System.out.println(s.size());

			//für jedes TheObject daten aus Datei lesen
			for (JSONObject st : s) {

				String label = null;
				double troughPut = 0;
				int xPos = 0;
				int yPos = 0;
				String image = null;

				//label
				label = (String) st.get("label");
				//troughput
				troughPut = Double.parseDouble(st.getString("troughput"));
				//location
				xPos= Integer.parseInt(st.getString("x_Position"));
				yPos=Integer.parseInt(st.getString("y_Position"));

				JSONArray view = st.getJSONArray("view");

				JSONObject theview = (JSONObject) view.get(0);
				//image
				image = theview.getString("image");

				JSONArray inqueues = st.getJSONArray("inqueue");

				ArrayList<SynchronizedQueue>theInqueues= new ArrayList<SynchronizedQueue>();

				//erstelle alle Inqueues
				for(JSONObject inqueue : toJSONList(inqueues)) {
					int xInqueuePos = Integer.parseInt(inqueue.getString("x_Position"));

					int yInqueuePos = Integer.parseInt(inqueue.getString("y_Position"));

					 theInqueues.add(SynchronizedQueue.createQueue(QueueViewJPanel.class, xInqueuePos, yInqueuePos));
				}
				JSONArray outqueues = st.getJSONArray("outqueue");

				ArrayList<SynchronizedQueue>theOutqueues= new ArrayList<SynchronizedQueue>();
				//erstelle alle Outqueues
				for(JSONObject outqueue : toJSONList(outqueues)) {
					int xOutqueuePos = Integer.parseInt(outqueue.getString("x_Position"));
					int yOutqueuePos = Integer.parseInt(outqueue.getString("y_Position"));
					theOutqueues.add(SynchronizedQueue.createQueue(QueueViewText.class,xOutqueuePos,yOutqueuePos));
				}
				ProcessStation.create(label,theInqueues,theOutqueues,troughPut,xPos,yPos,image);
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * create the end station
	 */
	private static void createEndStation() {
		ArrayList<JSONObject> objects= new ArrayList<JSONObject>();
		try {
			//lese die Datei ein und erstelle String
			FileReader fr = new FileReader(theEndStationDataFile);
			BufferedReader br = new BufferedReader(fr);
			String json = "";
			for (String line = ""; line != null; line = br.readLine())
				json += line;
			br.close();
			System.out.println(json + "\n");


			JSONObject j =  new JSONObject(json);
			JSONArray settings = j.getJSONArray("settings");

			objects.add((JSONObject) settings.get(0));

			JSONArray end_station = objects.get(0).getJSONArray("end_station");

			JSONObject theEndStation = end_station.getJSONObject(0);

			String label = theEndStation.getString("label");

			// Location wird gespeichert
			int xPos=Integer.parseInt(theEndStation.getString("x_Position"));
			int yPos= Integer.parseInt(theEndStation.getString("y_Position"));



			JSONArray view = theEndStation.getJSONArray("view");
			JSONObject theview = view.getJSONObject(0);


			//image wird gespeichert
			String image= theview.getString("image");

			JSONArray inqueue = theEndStation.getJSONArray("inqueue");
			JSONObject theInqueues = inqueue.getJSONObject(0);

			//location Inqueue wird gespeichert
			int xInqueuePos= Integer.parseInt(theInqueues.getString("x_Position"));
			int yInqueuePos= Integer.parseInt(theInqueues.getString("y_Position"));


			//erstelle Inqueue
			SynchronizedQueue theInqueue=SynchronizedQueue.createQueue(QueueViewText.class,xInqueuePos,yInqueuePos);

			JSONArray outqueue = theEndStation.getJSONArray("outqueue");
			JSONObject theOutqueues = outqueue.getJSONObject(0);

			//location Outqueue wird gespeichert
			int xOutqueuePos= Integer.parseInt(theOutqueues.getString("x_Position"));
			int yOutqueuePos= Integer.parseInt(theOutqueues.getString("y_Position"));

			//erstelle Outqueue
			SynchronizedQueue theOutqueue=SynchronizedQueue.createQueue(QueueViewText.class,xOutqueuePos,yOutqueuePos);
			//erstelle EndStation
			EndStation.create(label,theInqueue,theOutqueue,xPos,yPos,image);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<JSONObject> toJSONList(JSONArray a){
		ArrayList<JSONObject> theList = new ArrayList<JSONObject>();
		for (Iterator i = a.iterator();i.hasNext();){
			theList.add((JSONObject) i.next());
		}
		return theList;
	}

	// setze Dateipfad
	public static void setTheObjectDataFile(String theObjectDataFile) {
		FactoryJSON.theObjectDataFile = theObjectDataFile;
	}
	// setze Dateipfad
	public static void setTheStationDataFile(String theStationDataFile) {
		FactoryJSON.theStationDataFile = theStationDataFile;
	}
	// setze Dateipfad
	public static void setTheStartStationDataFile(String theStartStationDataFile) {
		FactoryJSON.theStartStationDataFile = theStartStationDataFile;
	}
	// setze Dateipfad
	public static void setTheEndStationDataFile(String theEndStationDataFile) {
		FactoryJSON.theEndStationDataFile = theEndStationDataFile;
	}
}
