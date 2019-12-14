package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.db.ResultSetConverter;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.entities.Score;
import ch.ffhs.pa.competitionmanager.interfaces.INotifiable;

import java.sql.*;
import java.util.*;

/**
 * The ranking list. When notified, it will pull the database for new entries and sort the entries according
 * to their score or time (needed).
 * @author Daniel Hoop, Reto Laesser, Christian Ion
 */
public class RankingList implements INotifiable {

    private GlobalState globalState = GlobalState.getInstance();
    private Event event;
    private CategoryList categoryList;
    private Map<Category, List<Score>> scores;
    private Map<Category, Long> highestIdInScores;

    public RankingList(Event event, CategoryList categoryList) {
        this.event = event;
        this.categoryList = categoryList;
        this.scores = new HashMap<>();
        getScoresFromDb(event, categoryList);
    }

    public void reloadFromDb(boolean reloadCategories) {
        if (reloadCategories) {
            categoryList.reloadFromDb();
        }
        getScoresFromDb(event, categoryList);
    }

    public Map<Category, List<Score>> getScores() {
        return scores;
    }

    /**
     * Gets all Score of the category from database and replaces inner list 'Score' completely.
     * @return A map where the key is the category, and the value is the score list.
     */
    private void getScoresFromDb(Event event, CategoryList categoryList) {

        long eventId = event.getId();
        List<Category> categories = categoryList.getCategories();

        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);

        scores.clear();
        for (Category category : categories) {

            List<Score> listOfScores = new LinkedList<>();
            try {
                String viewName = Query.categoryViewFullName(eventId, category.getId());
                stmt.execute(Query.getScoresForCategory(eventId, viewName, event.isTimeRelevant()));
                ResultSet rs = stmt.getResultSet();

                while (rs.next()) {
                    listOfScores.add(ResultSetConverter.toScore(rs));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                ExceptionVisualizer.showAndAddMessage(e, "When getting the scores for category " +
                        category.getName() +
                        " from the database and storing them into a list, the following error occurred: ");
            }

            scores.put(category, listOfScores);
            //System.out.println("CName:" + category.getName());
        }

        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
    }

//    /**
//     * Only gets new Score that were not in the database before and thus is more efficient than method 'updateScoreCompletely'.
//     * @return Boolean value indicating if the transaction was successful.
//     */
//    private Map<Category, List<Score>> getNewScoreFromDb(Event event, CategoryList categoryList) {
//        // TO DO: Execute query to get newest Score of which the id is above the current 'highestScoreIdInList'. Really necessary? Only for performance. But let's not do it.
//        return new HashMap<Category, List<Score>>();
//    }

    @Override
    public void notifyMe() {
        System.out.print("Database has changed. Pulling newest results...");

        reloadFromDb(false);
        System.out.print(" Done.\n");
    }

}
