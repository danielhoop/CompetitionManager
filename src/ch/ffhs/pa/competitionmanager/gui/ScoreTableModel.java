package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Score;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;
import ch.ffhs.pa.competitionmanager.utils.GenderStringConverter;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;

public class ScoreTableModel extends AbstractTableModel {

    private ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();
    DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

    private List<Score> scores;
    private boolean showCompetitorDetails;
    private boolean showScoreDetails;
    private boolean isTimeRelevant;
    private String[] columns;
    private final int nameIdx, dateOfBirthIdx, genderIdx, timeOrPointsIdx, isValidIdx, deletedIdx;

    public ScoreTableModel(List<Score> scores, boolean showScoreDetails, boolean showCompetitorDetails) {
        this.scores = scores;
        this.showScoreDetails = showScoreDetails;
        this.showCompetitorDetails = showCompetitorDetails;
        this.isTimeRelevant = GlobalState.getInstance().getEvent().isTimeRelevant();

        String timeOrPointsBundleName;
        if (isTimeRelevant) {
            timeOrPointsBundleName = "Score.timeNeeded";
        } else {
            timeOrPointsBundleName = "Score.pointsAchieved";
        }
        if (showCompetitorDetails) {
            columns = new String[]{
                    bundle.getString("Competitor.name"),
                    bundle.getString("Competitor.dateOfBirth"),
                    bundle.getString("Competitor.gender"),
                    bundle.getString(timeOrPointsBundleName)

            };
            nameIdx = 0;
            dateOfBirthIdx = 1;
            genderIdx = 2;
            timeOrPointsIdx = 3;
        } else {
            columns = new String[]{
                    bundle.getString("Competitor.name"),
                    bundle.getString(timeOrPointsBundleName)
            };
            nameIdx = 0;
            dateOfBirthIdx = Integer.MAX_VALUE;
            genderIdx = Integer.MAX_VALUE;
            timeOrPointsIdx = 1;
        }

        if (showScoreDetails) {
            columns = ArrayUtils.addAll(
                    columns,
                    bundle.getString("Score.isValidShort"),
                    bundle.getString("Score.deletedShort"));
            isValidIdx = timeOrPointsIdx + 1;
            deletedIdx = isValidIdx + 1;
        } else {
            isValidIdx = Integer.MAX_VALUE;
            deletedIdx = Integer.MAX_VALUE;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Score score = scores.get(rowIndex);

        if (columnIndex == nameIdx)
            return score.getCompetitor().getName();
        if (columnIndex == dateOfBirthIdx)
            return dateStringConverter.asString(score.getCompetitor().getDateOfBirth());
        if (columnIndex == genderIdx)
            return new GenderStringConverter().asString(score.getCompetitor().getGender());
        if (columnIndex == timeOrPointsIdx) {
            if (isTimeRelevant)
                return score.getTimeNeeded();
            return score.getPointsAchieved();
        }
        if (columnIndex == isValidIdx)
            return score.isValid();
        if (columnIndex == deletedIdx)
            return score.isDeleted();
        return null;
    }

    public Score getScoreFromRow(int rowIndex) {
        return scores.get(rowIndex);
    }

    public int isWhichColumn(String columnName) {
        for (int i = 1; i < getColumnCount(); i++) {
            if (columns[i].equals(columnName))
                return i;
        }
        return -1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    @Override
    public int getRowCount() {
        return scores.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }
}
