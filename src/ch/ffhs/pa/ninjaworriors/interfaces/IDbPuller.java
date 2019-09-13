package ch.ffhs.pa.ninjaworriors.interfaces;

/**
 * Interface to pull from database and determine if anything has changed.
 */
public interface IDbPuller {
    /**
     * Pull from database, i.e. execute a query on database.
     * @return Boolean value indicating if the database transaction was successful.
     */
    public boolean pullFromDb();

    /**
     * Determine if
     * @param previousPull The result of the previous pull.
     * @param thisPull The result of this (newer) pull.
     * @param <T> An instance of any class.
     * @return A boolean value indicating if the value has changed.
     */
    public <T> boolean hasValueChanged(T previousPull, T thisPull);

    /**
     * Get the value that was received on pulling the database.
     * @param <T> An instance of any class.
     * @return An object of type <T> containing the value that was received on pulling the database.
     */
    public <T> T getValueFromPull();
}
