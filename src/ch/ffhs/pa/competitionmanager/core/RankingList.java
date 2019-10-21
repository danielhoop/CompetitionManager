package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.dto.Score;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

/**
 * The ranking list. When notified, it will pull tha database for new entries and sort the entries according
 * to their score or time (needed).
 * @author Daniel Hoop, Reto Laesser, Christian Ion
 */
public class RankingList implements INotifiable {

    private GlobalState globalState = GlobalState.getInstance();
    private Event event;
    private CategoryList categories;
    private Map<Category, List<Score>> scores;
    private Map<Category, Long> highestIdInScores;

    public RankingList(Event event, CategoryList categories) {
        this.event = event;
        this.categories = categories;
        this.scores = getScoresFromDb(event, categories);
    }

    public void reloadFromDb(boolean reloadCategories) {
        if (reloadCategories) {
            categories.reloadFromDb();
        }
        this.scores = getScoresFromDb(event, categories);
    }

    /**
     * Gets all Score of the category from database and replaces inner list 'Score' completely.
     * @return A map where the key is the category, and the value is the score list.
     */
    private Map<Category, List<Score>> getScoresFromDb(Event event, CategoryList categories) {

        long eventId = event.getId();
        List<Category> categoryList = categories.getCategories();

        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);

        Map<Category, List<Score>> scoreMap = new HashMap<>();
        for (Category category : categoryList) {

            List<Score> scoreList = new LinkedList<>();
            try {
                String viewName = Query.categoryViewFullName(eventId, category.getId());
                stmt.execute(Query.getScoresForCategory(eventId, viewName, event.isTimeRelevant()));
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    scoreList.add(new Score(
                            rs.getLong("id"),
                            rs.getLong("event_id"),
                            new Competitor(
                                    rs.getLong("competitor_id"),
                                    rs.getLong("event_id"),
                                    rs.getString("name"),
                                    Gender.valueOf(rs.getInt("gender")),
                                    rs.getDate("date_of_birth").toLocalDate(),
                                    rs.getInt(Query.ageColumnName(rs.getLong("event_id")))
                            ),
                            rs.getTime("time_needed").toLocalTime(),
                            rs.getDouble("points_achieved"),
                            rs.getInt("number_of_tries"),
                            rs.getBoolean("is_valid"),
                            rs.getTimestamp("time_of_recording").toLocalDateTime()
                    ));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                ExceptionVisualizer.showAndAddMessage(e, "When getting the scores for category " +
                        category.getName() +
                        " from the database and storing them into a list, the following error occurred: ");
            }
        }

        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
        return scoreMap;
    }

    /**
     * Only gets new Score that were not in the database before and thus is more efficient than method 'updateScoreCompletely'.
     * @return Boolean value indicating if the transaction was successful.
     */
    private boolean getNewScoreFromDb() {
        // TODO Execute query to get newest Score of which the id is above the current 'highestScoreIdInList'.
        return true;
    }

    @Override
    public void notifyMe() {
        reloadFromDb(false);
    }

}
