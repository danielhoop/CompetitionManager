package ch.ffhs.pa.competitionmanager.interfaces;

/**
 * Interface to be notified
 */
public interface INotifiable {
    /**
     * The method to be called on notification. When called, then specific business logic should be executed.
     */
    public void notifyMe();
}
