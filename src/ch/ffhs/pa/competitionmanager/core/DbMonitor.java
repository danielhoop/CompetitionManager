package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.interfaces.IDbPuller;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;

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
     * Start monitoring the database. If changes occur, all objects in attribute 'objectsToNotify' will be notified.
     */
    public void start() {
        Thread thread = new Thread(() -> {
            // Endless while loop until interrupted.
            while (!interrupted) {
                // If change has occurred, then notify all objects.
                if (dbPuller.hasDbContentChanged()) {
                    for (int i = 0; i < objectsToNotify.size(); i++) {
                        objectsToNotify.get(i).notifyMe();
                    }
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
}
