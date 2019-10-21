package ch.ffhs.pa.competitionmanager.ui;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.dto.Competitor;

public class CompetitorEditorUi {

    public CompetitorEditorUi(boolean createNew, Competitor competitor) {
        // TODO: Make GUI with which all details of a competitor can be configured.
        // TODO: If createNew = true, then create a new database entry instead of altering it.
        if (createNew && competitor == null) {
            ExceptionVisualizer.showAndAddMessage(new IllegalArgumentException("If `createNew` is true, then `event` must not be null."), "CompetitorEditorUi()");
        }
    }
}
