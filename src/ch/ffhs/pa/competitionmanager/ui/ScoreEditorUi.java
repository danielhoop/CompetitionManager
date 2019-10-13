package ch.ffhs.pa.competitionmanager.ui;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.dto.Score;

public class ScoreEditorUi {

    public ScoreEditorUi(boolean createNew, Score score) {
        // TODO: Make GUI with which all details of a score can be configured.
        // TODO: A competitor field with dropdown needs to be implemented in which a competitor can be searched for by first name and last name.
        // TODO: Score GUI must contain all competitor details in order to avoid confusions of competitors with same or similar names.
        // TODO: If createNew = true, then create a new database entry instead of altering it.
        if (createNew && score == null) {
            ExceptionVisualizer.show(new IllegalArgumentException("If `createNew` is true, then `event` must not be null."));
        }
    }
}
