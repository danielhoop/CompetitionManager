package ch.ffhs.pa.competitionmanager.interfaces;

/**
 * Interface for persistence
 * CRUD = Create, read, update, delete.
 * Hint: Read is not requiered in our project because the 'list' classes in the core package will read the objects.
 */
public interface ICRUD {

//    /**
//     * For a given id, get the right row from the database and create an object with according attributes.
//     * @param type Type of the object to instantiate.
//     * @param id The id of the row in the database.
//     * @param <T> The generic type.
//     * @return The object for the given id.
//     */
//    public <T> T getById(T type, long id);

    /**
     * Create a database entry.
     * @return Boolean value indicating if the transaction was successful.
     */
    public boolean create();

    /**
     * Update a database entry.
     * @return Boolean value indicating if the transaction was successful.
     */
    public boolean update();


    /**
     * Delete a database entry.
     * @return Boolean value indicating if the transaction was successful.
     */
    public boolean delete();
}
