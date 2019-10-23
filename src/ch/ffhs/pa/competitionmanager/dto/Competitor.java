package ch.ffhs.pa.competitionmanager.dto;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.db.DbConnector;
import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.interfaces.ICRUD;

import java.sql.*;
import java.time.LocalDate;

/**
 * Contains information on a competitor.
 * Simple data object without functionality (only getter, setter and persistence methods).
 * @author Daniel Hoop, Reto Laesser, Christian Ion
 */
public class Competitor implements ICRUD {
    private long id;
    private long eventId;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
    private int age;

    public Competitor(long id, long eventId, String name, Gender gender, LocalDate dateOfBirth, int age) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
    }

    // id. No setter!
    public long getId() {
        return id;
    }
    public long getEventId() {
        return eventId;
    }
    // name
    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }
    // gender
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) { this.gender = gender; }
    // dateOfBirth
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    // age
    public int getAge() {
        return age;
    }
    public void setAge(int age) { this.age = age; }

    // CRUD operations
    @Override
    public boolean create() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.createCompetitor(name,gender, dateOfBirth), stmt.RETURN_GENERATED_KEYS)) {
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
            ExceptionVisualizer.showAndAddMessage(e, "Competitor.create(): ");
        return false;
    }

        dbConnector.closeConnection(conn);
        return true;
    }

    @Override
    public boolean update() {
        if(this.delete() == true && this.create() == true){
            return true;
        }
        return false;
    }

    @Override
    public boolean delete() {
        DbConnector dbConnector = GlobalState.getInstance().getDbConnector();
        Connection conn = dbConnector.getConnection();

        try (Statement stmt = dbConnector.createStatmentForConnection(conn)) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(Query.deleteCompetitor(id))) {
                preparedStatement.executeUpdate();
            }
            dbConnector.closeStatement(stmt);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "Competitor.delete(): ");
            return false;
        }

        dbConnector.closeConnection(conn);
        return true;
    }
}
