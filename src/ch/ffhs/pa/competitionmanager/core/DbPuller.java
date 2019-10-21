package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.interfaces.IDbPuller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Periodically checks if the relevant content of the database has changed such that all RankingLists should pull again from the database.
 */
public class DbPuller implements IDbPuller {

    private GlobalState globalState = GlobalState.getInstance();
    private long maxIdPrevious = 0;
    private long maxIdNow = 0;
    private long nValidScoresPrevious = 0;
    private long nValidScoresNow = 0;

    public DbPuller() { }

    @Override
    public boolean hasDbContentChanged() {
        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);
        long eventId = globalState.getEvent().getId();
        boolean hasChanged = false;
        try {
            // Get the number of valid scores and compare against old value.
            stmt.execute(Query.numberOfValidScores(eventId));
            ResultSet rs = stmt.getResultSet();
            if (rs == null || !rs.next())
                throw new IllegalStateException("The result set returned from the query to count the number of valid scores was null or did not contain any rows.");
            nValidScoresNow = rs.getLong(1);
            rs.close();
            hasChanged = nValidScoresNow != nValidScoresPrevious;

            // Get the maximum id of scores
            stmt.execute(Query.maxIdInScores(eventId));
            rs = stmt.getResultSet();
            if (rs == null || !rs.next())
                throw new IllegalStateException("The result set returned from the query to get the maximum id in table 'score' was null or did not contain any rows.");
            maxIdNow = rs.getLong(1);
            rs.close();
            hasChanged = maxIdNow != maxIdPrevious;

        } catch (SQLException e) {
            ExceptionVisualizer.showAndAddMessage(e, "DbPuller.hasDbContentChanged(): ");
        } finally {
            dbConnector.closeStatement(stmt);
            dbConnector.closeConnection(conn);
        }

        maxIdPrevious = maxIdNow;
        nValidScoresPrevious = nValidScoresNow;
        return hasChanged;
    }
}
