package ch.ffhs.pa.competitionmanager.core;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.dto.Event;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.webserver.HtmlPage;

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
        this.categories = new LinkedList<>();
        getCategoriesFromDb(event);
    }

    public void reloadFromDb() {
        getCategoriesFromDb(event);
    }

    public List<Category> getCategories() {
        return categories;
    }

    private void getCategoriesFromDb(Event event) {

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
        //System.out.println("Categories:" + categories.size());
        //System.out.println("C HTML:" + HtmlPage.CategoryIDs(categories).render());
        HtmlPage.writetoHTML(HtmlPage.CategoryIDs(categories).render(),"category_content.txt");
        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
    }
}
