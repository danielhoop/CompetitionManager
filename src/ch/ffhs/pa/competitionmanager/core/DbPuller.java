package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConfig;
import ch.ffhs.pa.competitionmanager.interfaces.IDbPuller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DbPuller implements IDbPuller {

    private GlobalState globalState;
    private long maxIdPrevious = 0;
    private long maxIdNow = 0;
    private long nValidScoresPrevious = 0;
    private long nValidScoresNow = 0;

    public DbPuller() {
        globalState = GlobalState.getInstance();
    }

    @Override
    public boolean hasDbContentChanged() {
        Connection conn = null;
        Statement stmt = null;
        int eventId = globalState.getEventId();
        boolean hasChanged = false;
        try {
            conn = globalState.getDbConnector().getConnection();
            stmt = globalState.getDbConnector().createStatmentForConnection(conn);
            // Get the number of valid scores and compare against old value.
            stmt.execute(DbConfig.numberOfValidScores(eventId));
            ResultSet rs = stmt.getResultSet();
            if (rs == null || !rs.next())
                throw new IllegalStateException("The result set returned from the query to count the number of valid scores was null or did not contain any rows.");
            nValidScoresNow = rs.getLong(1);
            rs.close();

            // Get the maximum id of scores
            if (nValidScoresNow == nValidScoresPrevious) {
                stmt.execute(DbConfig.maxIdInScores(eventId));
                rs = stmt.getResultSet();
                if (rs == null || !rs.next())
                    throw new IllegalStateException("The result set returned from the query to get the maximum id in table 'score' was null or did not contain any rows.");
                rs.next();
                maxIdNow = rs.getLong(1);
                rs.close();
                hasChanged = maxIdNow != maxIdPrevious;
            } else {
                hasChanged = true;
            }

        } catch (SQLException e) {
            ExceptionVisualizer.show(e);
        } finally {
            globalState.getDbConnector().closeStatement(stmt);
            globalState.getDbConnector().closeConnection(conn);
        }

        maxIdPrevious = maxIdNow;
        nValidScoresPrevious = nValidScoresNow;
        return hasChanged;
    }
}
