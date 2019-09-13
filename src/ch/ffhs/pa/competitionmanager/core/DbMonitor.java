package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.interfaces.IDbPuller;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;

import java.util.List;

/**
 * Periodically executes a query on a database to check whether now entries have been added.
 * If so, then it will notify all objects in an internal list. The objects must implement the INotifiable interface.
 * The database query is done by an object that implements IDbPuller and can be given in constructur.
 * @author Daniel Hoop, Reto Laesser, Christian Ion
 */
public class DbMonitor <T> {
    private List<INotifiable> objectsToNotify;
    private int pullFrequencyInSecs;
    private IDbPuller dbPuller;
    private T previousPull;
    private T thisPull;

    /**
     * @author Daniel Hoop, Reto Laesser, Christian Ion
     * @param objectsToNotify The list containing all objects that will be notified if the content of the database has changed.
     * @param pullFrequencyInSecs The pull frequency (in seconds) to check if the database has changed.
     * @param dbPuller An objects that will pull the database and provided methods to check whether the content has changed.
     * @param pullResultClass The class that will be returned by dbPuller.
     */
    public DbMonitor(List<INotifiable> objectsToNotify, int pullFrequencyInSecs,
                     IDbPuller dbPuller, T pullResultClass) {
        this.objectsToNotify = objectsToNotify;
        this.pullFrequencyInSecs = pullFrequencyInSecs;
        this.dbPuller = dbPuller;
    }
}
