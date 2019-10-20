package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.DbConfig;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.enums.Gender;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * An object that holds information on all categories of the competition.
 */
public class CategoryList {

    private GlobalState globalState = GlobalState.getInstance();
    private Event event;
    private List<Category> categories;

    public CategoryList(Event event) {
        this.event = event;
        this.categories = getCategoriesFromDb(event.getId());
    }

    public void reloadFromDb() {
        this.categories = getCategoriesFromDb(event.getId());
    }

    public List<Category> getCategories() {
        return categories;
    }

    private List<Category> getCategoriesFromDb(long eventId) {

        List<Category> categoryList = new LinkedList<>();
        DbConnector dbConnector = globalState.getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);
        try {
            stmt.execute(DbConfig.getAllCategories(eventId));
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                categoryList.add(new Category(
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
        return categoryList;
    }
}
