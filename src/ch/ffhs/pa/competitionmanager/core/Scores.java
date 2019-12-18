package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.db.ResultSetConverter;
import ch.ffhs.pa.competitionmanager.entities.Score;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.gui.ScoreTableModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * All scores in the database, independent of categories.
 */
public class Scores {
    private GlobalState globalState = GlobalState.getInstance();
    private Event event;
    private List<Score> scores;
    private boolean withDeletedOnes;
    private boolean orderByName;

    /**
     * Construtor
     * @param event The event
     * @param withDeletedOnes Indicates if deleted scores should be loaded as well (true).
     * @param orderByName Indicates if the scores should be ordered by name (true) rather than by the timeNeeded or pointsAchieved (false).
     */
    public Scores(Event event, boolean withDeletedOnes, boolean orderByName) {
        this.event = event;
        this.scores = new LinkedList<>();
        this.withDeletedOnes = withDeletedOnes;
        this.orderByName = orderByName;
        reloadFromDb();
    }

    /**
     * Reload scores from database.
     */
    public void reloadFromDb() {
        long eventId = event.getId();
        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);

        try {
            stmt.execute(Query.getAllScores(eventId, withDeletedOnes, orderByName));
            ResultSet rs = stmt.getResultSet();

            scores.clear();
            while (rs.next()) {
                scores.add(ResultSetConverter.toScore(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "When getting all scores from the database and storing them into a list, the following error occurred: ");
        }

        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
    }

    /**
     * Get scores as a list.
     * @return The scores as a list.
     */
    public List<Score> getScores() {
        return scores;
    }

    /**
     * Get scores as a table model to be used in a JTable.
     * @return The table model.
     */
    public ScoreTableModel getScoresAsTableModel(boolean showScoreDetails, boolean showCompetitorDetails) {
        ScoreTableModel scoreTableModel = new ScoreTableModel(this.scores, showScoreDetails, showCompetitorDetails);
        return scoreTableModel;
    }
}
