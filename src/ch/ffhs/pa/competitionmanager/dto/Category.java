package ch.ffhs.pa.competitionmanager.dto;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.interfaces.ICRUD;

import java.sql.*;

/**
 * Contains information on a competition category.
 * Simple data object without functionality (only getter, setter and persistence methods).
 * @author Daniel Hoop, Reto Laesser, Christian Ion
 */
public class Category implements ICRUD {
    private long id;
    private long eventId;
    private String name;
    private String description;
    private int minAgeInclusive;
    private int maxAgeInclusive;
    private Gender gender;

    public Category(long id, long eventId, String name, String description,
                    int minAgeInclusive, int maxAgeInclusive, Gender gender) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.minAgeInclusive = minAgeInclusive;
        this.maxAgeInclusive = maxAgeInclusive;
        this.gender = gender;
    }

    // id. No setter!
    public long getId() {
        return id;
    }
    public long getEventId() {
        return eventId;
    }
    // name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // description
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) { this.description = description; }
    // miAgeInclusive
    public int getMinAgeInclusive() {
        return minAgeInclusive;
    }
    public void setMinAgeInclusive(int minAgeInclusive) { this.minAgeInclusive = minAgeInclusive; }
    // maxAgeInclusive
    public int getMaxAgeInclusive() {
        return maxAgeInclusive;
    }
    public void setMaxAgeInclusive(int maxAgeInclusive) { this.maxAgeInclusive = maxAgeInclusive; }
    // gender
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) { this.gender = gender; }

    // CRUD operations
    @Override
    public boolean create() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.createCategory(eventId, name, description, minAgeInclusive, maxAgeInclusive, gender), stmt.RETURN_GENERATED_KEYS)) {
                preparedStatement.executeUpdate();
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getLong(1);
                    }
                }
            }
            dbConnector.closeStatement(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Category.create(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }

    @Override
    public boolean update() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.updateCategory(id, eventId, name, description, minAgeInclusive, maxAgeInclusive, gender))) {
                preparedStatement.executeUpdate();
            }
            dbConnector.closeStatement(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Category.update(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }

    @Override
    public boolean delete() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.deleteCategory(id))) {
                preparedStatement.executeUpdate();
            }
            dbConnector.closeStatement(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Category.delete(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }
}
