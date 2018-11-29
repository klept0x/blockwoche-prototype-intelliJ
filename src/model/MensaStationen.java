package model;

import io.Statistics;

import java.util.ArrayList;

/**
 * Beschreibung der Klasse MensaStationen.
 * Weiter Beschreibung
 * <p>
 * Die Klasse wurde am 28.November.2018 um 16:16 Uhr erstellt.
 *
 * @author Team5
 * @version 1.0
 */

public class MensaStationen extends ProcessStation{

    double preis;

    private MensaStationen(String label, ArrayList<SynchronizedQueue> inQueues, ArrayList<SynchronizedQueue> outQueues, double troughPut, int xPos, int yPos, String image, double preis) {
        super(label, inQueues, outQueues, troughPut, xPos, yPos, image);

       // this.preis = preis;
    }

    public static void create(String label, ArrayList<SynchronizedQueue> inQueues, ArrayList<SynchronizedQueue> outQueues, double troughPut, int xPos, int yPos, String image, double preis){
        new MensaStationen(label, inQueues, outQueues, troughPut, xPos, yPos, image, preis);
    }

    @Override
    protected void handleObject(TheObject theObject) {
        Statistics.show("EssenAusgabe");
        super.handleObject(theObject);
        Student s = (Student) theObject;
        s.measurement.guthaben++;
    }

    @Override
    protected Student getNextInQueueObject() {
        return (Student)super.getNextInQueueObject();
    }

    @Override
    protected Student getNextOutQueueObject() {
        return (Student) super.getNextOutQueueObject();
    }

}
