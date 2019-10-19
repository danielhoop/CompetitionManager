package ch.ffhs.pa.competitionmanager.db;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Event;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Prepares the database such that it is ready for all queries that are needed in the app.
 */
public class DbPreparator {

    private GlobalState globalState;

    public DbPreparator() {
        globalState = GlobalState.getInstance();
    }

    public boolean prepare() {
        boolean hasWorked = true;
        hasWorked = hasWorked & createAgeColumn(Event.getById(globalState.getEventId()).getDate().toString());
        return hasWorked;
    }

    /**
     * Create age column in table 'competitors'
     * @param date A date string ISO-8601 format uuuu-MM-dd, like '2019-01-01'.
     * @return Boolean value in indicating if the database manipulation has worked.
     */
    private boolean createAgeColumn(String date) {
        Connection conn = globalState.getDbConnector().getConnection();
        Statement stmt = globalState.getDbConnector().createStatmentForConnection(conn);
        try {
            stmt.execute(DbConfig.dropAgeColumn());
        } catch (SQLException e) {
            // Do nothing. This error can occur if the age column does not yet exist. Just carry on.
        }
        try {
            stmt.execute(DbConfig.createAgeColumn(date));
        } catch (SQLException e) {
            ExceptionVisualizer.showAndAddMessage(e, "When trying to create the age column in the competitor table, the following error occurred: ");
            return false;
        } finally {
            globalState.getDbConnector().closeStatement(stmt);
            globalState.getDbConnector().closeConnection(conn);
        }
        return true;
    }
}
