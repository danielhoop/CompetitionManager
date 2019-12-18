package ch.ffhs.pa.competitionmanager.interfaces;

/**
 * Interface to pull from database and determine if anything has changed
 */
public interface IDbPuller {
    /**
     * Pull from database, i.e. execute a query on database. Check whether the database content has changed. Indicate change with boolean value.
     * @return Boolean value indicating if the database content has changed.
     */
    public boolean hasDbContentChanged();
}
