package ch.ffhs.pa.ninjaworriors.interfaces;

/**
 * Interface for persistence.
 * CRUD = Create, update, delete.
 */
public interface ICRUD {
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
