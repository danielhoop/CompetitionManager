package ch.ffhs.pa.competitionmanager.ui;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.dto.Event;

public class EventEditorUi {

    private boolean createNew;

    public EventEditorUi(boolean createNew, Event event) {
        // TODO: Make GUI with which all details of an event can be configured.
        // TODO: If createNew = true, then create a new database entry instead of altering it.
        if (createNew && event == null) {
            ExceptionVisualizer.showAndAddMessage(new IllegalArgumentException("If `createNew` is true, then `event` must not be null."), "EventEditorUi(): ");
        }
    }
}
