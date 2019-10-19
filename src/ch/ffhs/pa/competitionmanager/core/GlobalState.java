package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.db.DbConnector;

/**
 * Singleton containing some global variables.
 */
public class GlobalState {

    private static GlobalState globalState = null;
    private DbConnector dbConnector;
    private int eventId = -1;

    private GlobalState() {}

    public static GlobalState getInstance() {
        if (globalState == null) {
            globalState = new GlobalState();
        }
        return globalState;
    }

    public void setDbConnector(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }
    public DbConnector getDbConnector() {
        return dbConnector;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    public int getEventId() {
        return eventId;
    }
}
