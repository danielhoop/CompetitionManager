package ch.ffhs.pa.competitionmanager.dto;

import ch.ffhs.pa.competitionmanager.interfaces.ICRUD;

public class Event implements ICRUD {
    private long id;
    private String name;
    // Hier kann z.B. geschrieben werden «Mittwoch, 11. September 2019». Deshalb ist es ein String Feld.
    private String date;
    private String description;

    public Event(long id, String name, String date, String description) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
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
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    // description
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

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
