package ch.ffhs.pa.competitionmanager.db;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.CategoryList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.entities.Event;

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

    public static void createSchemaIfNotExists() {
        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);

        try {
            stmt.execute(Query.doesDatabaseSchemaExist());
            ResultSet rs = stmt.getResultSet();
            // If there are no rows in that query, then the schema does not exist.
            if (!rs.next()) {
                for (String query : Query.createDatabaseSchema()) {
                    if (!query.equals(";") && !query.equals("\n;")) {
                        stmt.execute(query);
                        System.out.print(query);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "When trying to create the database schema, the following error occurred: ");
        }

        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
    }

    public static boolean prepare(Event event, CategoryList categoryList) {
        boolean hasWorked = true;
        hasWorked = hasWorked & createAgeColumn(event);
        hasWorked = hasWorked & createCategoryViewsInDb(event, categoryList);
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
            stmt.execute(Query.dropAgeColumn(eventId));
        } catch (SQLException e) {
            // Do nothing. This error can occur if the age column does not yet exist. Just carry on.
        }
        try {
            String query = Query.createAgeColumn(eventId, date);
            stmt.execute(Query.createAgeColumn(eventId, date));
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
     * @param categoryList An object of type CategoryList containing all categories.
     * @return Logical value indicating if the database operations were successful.
     */
    private static boolean createCategoryViewsInDb(Event event, CategoryList categoryList) {

        long eventId = event.getId();
        List<Category> categories = categoryList.getCategories();

        // DbConnection
        boolean hasWorked = true;
        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);
        Statement stmtDeleteView;

        // First, delete all existing views concerning categories.
        try {
            stmt.execute(Query.getAllViewNames());
            ResultSet rs = stmt.getResultSet();
            String viewName;
            while (rs.next()) {
                viewName = rs.getString(1);
                if (viewName.startsWith(Query.categoryViewEventName(eventId))) {
                    stmtDeleteView = dbConnector.createStatmentForConnection(conn);
                    stmtDeleteView.execute(Query.dropView(viewName));
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
            categoryViewName = Query.categoryViewFullName(eventId, 0);
            stmt.execute(Query.createScoreViewForAllCompetitors(
                    categoryViewName,
                    eventId));
            // A view for each category.
            for (Category category : categories) {
                categoryViewName = Query.categoryViewFullName(eventId, category.getId());
                stmt.execute(Query.createScoreViewForCategory(
                        eventId,
                        categoryViewName,
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
