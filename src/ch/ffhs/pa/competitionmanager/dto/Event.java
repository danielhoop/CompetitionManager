package ch.ffhs.pa.competitionmanager.dto;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.interfaces.ICRUD;

import java.sql.*;
import java.time.LocalDate;

public class Event implements ICRUD {
    private long id;
    private String name;
    private LocalDate date;
    // Hier kann z.B. geschrieben werden «Mittwoch, 11. September 2019». Deshalb ist es ein String Feld.
    private String dateDescription;
    private String description;
    private boolean isTimeRelevant;

    public Event(long id, String name, LocalDate date, String dateDescription, String description, boolean isTimeRelevant) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.dateDescription = dateDescription;
        this.description = description;
        this.isTimeRelevant = isTimeRelevant;
    }

    // id. No setter!
    public long getId() {
        return id;
    }
    // name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    // date
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    // dateDescription
    public String getDateDescription() {
        return dateDescription;
    }
    public void setDateDescription(String dateDescription) {
        this.dateDescription = dateDescription;
    }
    // description
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    // Time relevant
    public boolean isTimeRelevant() {
        return isTimeRelevant;
    }
    public void setTimeRelevant(boolean timeRelevant) {
        isTimeRelevant = timeRelevant;
    }

    // Load event from database.
    public static Event getById(long id) {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();
        Statement stmt = dbConnector.createStatmentForConnection(conn);
        Event event = null;

        try {
            stmt.execute(Query.eventById(id));
            ResultSet rs = stmt.getResultSet();
            if (rs == null || !rs.next()) {
                ExceptionVisualizer.showAndAddMessage(new IllegalStateException("No event was found with id = " + id), "Event.getById(): ");
            } else {
                event = new Event(id,
                        rs.getString("name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("date_descr"),
                        rs.getString("description"),
                        rs.getBoolean("is_time_relevant"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Event.getById(): ");
        }

        dbConnector.closeStatement(stmt);
        dbConnector.closeConnection(conn);
        return event;
    }

    // CRUD operations
    @Override
    public boolean create() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.createEvent(name,date, dateDescription, description, isTimeRelevant), stmt.RETURN_GENERATED_KEYS)) {
                preparedStatement.executeUpdate();
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getLong(1);
                    }
                }
            }
            dbConnector.closeStatement(stmt);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Event.create(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }

    @Override
    public boolean update() {
        this.delete();
        this.create();
        return true;
    }

    @Override
    public boolean delete() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.deleteEvent(id))) {
                preparedStatement.executeUpdate();
            }
            dbConnector.closeStatement(stmt);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Event.delete(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }
}
