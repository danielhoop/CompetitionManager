package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.dto.Event;

/**
 * Singleton containing some global variables.
 */
public class GlobalState {

    private static GlobalState globalState = null;
    private DbConnector dbConnector;
    private Event event;

    private GlobalState() {}

    public static GlobalState getInstance() {
        if (globalState == null) {
            globalState = new GlobalState();
        }
        return globalState;
    }

    public DbConnector getDbConnector() {
        return dbConnector;
    }
    public void setDbConnector(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }
    public Event getEvent() { return event; }
    public void setEvent(Event event) {  this.event = event; }
}
