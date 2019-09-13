package ch.ffhs.pa.competitionmanager.dto;

import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.interfaces.ICRUD;

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
    private byte minAgeInclusive;
    private byte maxAgeInclusive;
    private Gender gender;

    public Category(long id, long eventId, String name, String description,
                    byte minAgeInclusive, byte maxAgeInclusive, Gender gender) {
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
    public void setX(String description) { this.description = description; }
    // miAgeInclusive
    public byte getMinAgeInclusive() {
        return minAgeInclusive;
    }
    public void setMinAgeInclusive(byte minAgeInclusive) { this.minAgeInclusive = minAgeInclusive; }
    // maxAgeInclusive
    public byte getMaxAgeInclusive() {
        return maxAgeInclusive;
    }
    public void setMaxAgeInclusive(byte maxAgeInclusive) { this.maxAgeInclusive = maxAgeInclusive; }
    // gender
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) { this.gender = gender; }

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
