package ch.ffhs.pa.competitionmanager.dto;

import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.interfaces.ICRUD;

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
    private byte age;

    public Competitor(long id, long eventId, String name,
                      Gender gender, LocalDate dateOfBirth, byte age) {
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
    public byte getAge() {
        return age;
    }
    public void setAge(byte age) { this.age = age; }

    // CRUD operations
    @Override
    public boolean create() {
        // TODO: Add a new row to database table.
        //       Then, use the new id provided by the database and update the id in this class instance.
        return false;
    }

    @Override
    public boolean update() {
        // TODO: First, set 'deleted', 'updated', and 'deletedDateTime' attribute in database to true, then create a new competitor in the database.
        //       Like this, the history of updates will be available.
        return false;
    }

    @Override
    public boolean delete() {
        // TODO: Set 'deleted' attribute in database and set 'deletedDateTime' accordingly.
        return false;
    }
}