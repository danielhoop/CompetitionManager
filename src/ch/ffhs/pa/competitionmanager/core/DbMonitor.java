package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.interfaces.IDbPuller;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Periodically executes a query on a database to check whether now entries have been added.
 * If so, then it will notify all objects in an internal list. The objects must implement the INotifiable interface.
 * The database query is done by an object that implements IDbPuller and can be given in constructor.
 * @author Daniel Hoop, Reto Laesser, Christian Ion
 */
public class DbMonitor {
    private List<INotifiable> objectsToNotify;
    private int pullFrequencyInSecs;
    private IDbPuller dbPuller;
    private boolean interrupted;

    /**
     * @author Daniel Hoop, Reto Laesser, Christian Ion
     * @param objectsToNotify The list containing all objects that will be notified if the content of the database has changed.
     * @param pullFrequencyInSecs The pull frequency (in seconds) to check if the database has changed.
     * @param dbPuller An objects that will pull the database and provided methods to check whether the content has changed.
     */
    public DbMonitor(List<INotifiable> objectsToNotify, int pullFrequencyInSecs, IDbPuller dbPuller) {
        this.objectsToNotify = objectsToNotify;
        this.pullFrequencyInSecs = pullFrequencyInSecs;
        this.dbPuller = dbPuller;
    }

    /**
     * Constructor, but will take only one single object to notify.
     * @param objectToNotify An object to notify.
     * @param pullFrequencyInSecs The pull frequency (in seconds) to check if the database has changed.
     * @param dbPuller An objects that will pull the database and provided methods to check whether the content has changed.
     */
    public DbMonitor(INotifiable objectToNotify, int pullFrequencyInSecs, IDbPuller dbPuller) {
        this.objectsToNotify = new LinkedList<INotifiable>();
        this.objectsToNotify.add(objectToNotify);
        this.pullFrequencyInSecs = pullFrequencyInSecs;
        this.dbPuller = dbPuller;
    }

    /**
     * Add an object to be notified to the list.
     * @param objectToNotify The object to notify.
     */
    public void addObjectToNotify(INotifiable objectToNotify) {
        objectsToNotify.add(objectToNotify);
    }
    /**
     * Add an object to be notified to the list.
     * @param index The index to put the element into the list.
     * @param objectToNotify The object to notify.
     */
    public void addObjectToNotify(int index, INotifiable objectToNotify) {
        objectsToNotify.add(index, objectToNotify);
    }
    /**
     * Add a collection to be notified to notify to the list.
     * @param objectsToNotify A collection of objects to be notified.
     */
    public void addObjectsToNotify(Collection<INotifiable> objectsToNotify) {
        this.objectsToNotify.addAll(objectsToNotify);
    }

    /**
     * Clear list of objects to be notified and fill it again with given collection.
     * @param objectsToNotify A collection of objects to be notified.
     */
    public void resetObjectsToNotify(Collection<INotifiable> objectsToNotify) {
        this.objectsToNotify.clear();
        this.objectsToNotify.addAll(objectsToNotify);
    }

    /**
     * Get objects to notify as a list.
     * @return The objects to notify in a list.
     */
    public List<INotifiable> getObjectsToNotify() {
        return objectsToNotify;
    }

    /**
     * Start monitoring the database. If changes occur, all objects in attribute 'objectsToNotify' will be notified.
     */
    public void start() {
        Thread thread = new Thread(() -> {
            // Endless while loop until interrupted.
            while (!interrupted) {
                // If change has occurred, then notify all objects.
                if (dbPuller.hasDbContentChanged()) {
                    notifyAllObjects();
                }
                // Wait n seconds until next pull.
                try {
                    TimeUnit.SECONDS.sleep(pullFrequencyInSecs);
                } catch (InterruptedException e) {
                    // Should not happen.
                    e.printStackTrace();
                }
            }
        });
        interrupted = false;
        thread.start();
    }

    /**
     * Stop monitoring the database for changes.
     */
    public void stop() {
        interrupted = true;
    }


    /**
     * Notify all objects in 'objectsToNotify'.
     */
    private void notifyAllObjects() {
        // If the number of objectsToNotify has changed, then do it again.
        int nObjects = objectsToNotify.size();
        while (true) {
            for (int i = 0; i < objectsToNotify.size(); i++) {
                objectsToNotify.get(i).notifyMe();
            }
            if (nObjects == objectsToNotify.size()) {
                break;
            }
            nObjects = objectsToNotify.size();
        }
    }

}

