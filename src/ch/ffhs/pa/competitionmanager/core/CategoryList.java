package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.gui.CategoryTableModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds information on all categories of the competition.
 */
public class CategoryList {

    private GlobalState globalState = GlobalState.getInstance();
    private Event event;
    private List<Category> categories;

    /**
     * Constructor
     * @param event The event to make the CategoryList for
     */
    public CategoryList(Event event) {
        this.event = event;
        this.categories = new LinkedList<>();
        loadCategoriesFromDb(event);
    }

    /**
     * Reload internal data from database.
     */
    public void reloadFromDb() {
        loadCategoriesFromDb(event);
    }

    /**
     * Get categories as a list.
     * @return The categories as a list.
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Get categories as a table model to be used in a JTable.
     * @return The table model.
     */
    public CategoryTableModel getCategoriesAsTableModel() {
        CategoryTableModel categoryTableModel = new CategoryTableModel(this);
        return categoryTableModel;
    }

    /**
     * Load categories from database.
     * @param event Categories will be loaded for this event.
     */
    private void loadCategoriesFromDb(Event event) {

        long eventId = event.getId();
        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);

        try {
            stmt.execute(Query.getAllCategories(eventId));
            ResultSet rs = stmt.getResultSet();

            categories.clear();
            while (rs.next()) {
                categories.add(new Category(
                        rs.getLong("id"),
                        rs.getLong("event_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("min_age_inclusive"),
                        rs.getInt("max_age_inclusive"),
                        Gender.valueOf(rs.getInt("gender"))
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "When getting all categories from the database and storing them into a list, the following error occurred: ");
        }

        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
    }
}
