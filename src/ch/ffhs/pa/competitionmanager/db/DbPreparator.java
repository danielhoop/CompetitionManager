package ch.ffhs.pa.competitionmanager.db;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.dto.Event;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Prepares the database such that it is ready for all queries that are needed in the app.
 */
public class DbPreparator {

    private static GlobalState globalState = GlobalState.getInstance();

    public static boolean prepare(Event event, List<Category> categories) {
        boolean hasWorked = true;
        hasWorked = hasWorked & createAgeColumn(event);
        hasWorked = hasWorked & createCategoryViewsInDb(event, categories);
        return hasWorked;
    }

    /**
     * Create age column in table 'competitors'
     * @param event The event.
     * @return Boolean value in indicating if the database manipulation has worked.
     */
    private static boolean createAgeColumn(Event event) {
        long eventId = event.getId();
        String date = event.getDate().toString();

        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);
        try {
            stmt.execute(DbConfig.dropAgeColumn(eventId));
        } catch (SQLException e) {
            // Do nothing. This error can occur if the age column does not yet exist. Just carry on.
        }
        try {
            stmt.execute(DbConfig.createAgeColumn(eventId, date));
        } catch (SQLException e) {
            ExceptionVisualizer.showAndAddMessage(e, "When trying to create the age column in the competitor table, the following error occurred: ");
            return false;
        } finally {
            dbConnector.closeStatement(stmt);
            dbConnector.closeConnection(conn);
        }
        return true;
    }


    /**
     * Create all views on scores of each category.
     * @param event The event.
     * @param categories A list of categories for which views should be created.
     * @return Logical value indicating if the database operations were successful.
     */
    private static boolean createCategoryViewsInDb(Event event, List<Category> categories) {

        long eventId = event.getId();
        // DbConnection
        boolean hasWorked = true;
        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);
        Statement stmtDeleteView;

        // First, delete all existing views concerning categories.
        try {
            stmt.execute(DbConfig.getAllViewNames());
            ResultSet rs = stmt.getResultSet();
            String viewName;
            while (rs.next()) {
                viewName = rs.getString(1);
                if (viewName.startsWith(DbConfig.categoryViewEventName(eventId))) {
                    stmtDeleteView = dbConnector.createStatmentForConnection(conn);
                    stmtDeleteView.execute(DbConfig.dropView(viewName));
                    dbConnector.closeStatement(stmtDeleteView);
                }
            }
            rs.close();
        } catch (SQLException e) {
            hasWorked = false;
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "When deleting existing category views, an error occurred: ");
        }

        // Now, create the views once again.
        String categoryViewName = "";
        try {
            // The view that includes all competitors
            categoryViewName = DbConfig.categoryViewFullName(eventId, 0);
            stmt.execute(DbConfig.createScoreViewForAllCompetitors(
                    categoryViewName,
                    eventId));
            // A view for each category.
            for (Category category : categories) {
                categoryViewName = DbConfig.categoryViewFullName(eventId, category.getId());
                stmt.execute(DbConfig.createScoreViewForCategory(
                        categoryViewName,
                        eventId,
                        category.getMinAgeInclusive(),
                        category.getMaxAgeInclusive(),
                        category.getGender().getValue()));
            }
        } catch (SQLException e) {
            hasWorked = false;
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "When creating the category views called '" + categoryViewName + "', an error occurred: ");
        } finally {
            dbConnector.closeStatement(stmt);
        }
        dbConnector.closeConnection(conn);
        return hasWorked;
    }
}
