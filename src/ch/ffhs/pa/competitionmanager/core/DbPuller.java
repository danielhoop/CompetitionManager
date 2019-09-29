package ch.ffhs.pa.competitionmanager.core;

import ch.ffhs.pa.competitionmanager.interfaces.IDbPuller;

public class DbPuller implements IDbPuller {
    @Override
    public boolean pullFromDb() {
        // TODO
        return false;
    }

    @Override
    public <T> boolean hasValueChanged(T previousPull, T thisPull) {
        // TODO
        return false;
    }

    @Override
    public <T> T getValueFromPull() {
        // TODO
        return null;
    }
}
