package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CompetitorList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Competitor;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;
import ch.ffhs.pa.competitionmanager.utils.GenderStringConverter;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;

public class CompetitorTableModel extends AbstractTableModel {

    private ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();
    DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

    private List<Competitor> competitors;
    private String[] columns;
    private final int nameIdx, dateOfBirthIdx, genderIdx;

    public CompetitorTableModel(CompetitorList competitorList) {
        competitors = competitorList.getCompetitors();
        columns = new String[]{
                bundle.getString("Competitor.name"),
                bundle.getString("Competitor.dateOfBirth"),
                bundle.getString("Competitor.gender")
        };
        nameIdx = 0;
        dateOfBirthIdx = 1;
        genderIdx = 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Competitor competitor = competitors.get(rowIndex);

        if (columnIndex == nameIdx)
            return competitor.getName();
        if (columnIndex == dateOfBirthIdx)
            return dateStringConverter.asString(competitor.getDateOfBirth());
        if (columnIndex == genderIdx)
            return new GenderStringConverter().asString(competitor.getGender());
        return null;
    }

    public Competitor getCompetitorFromRow(int rowIndex) {
        return competitors.get(rowIndex);
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
        return competitors.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }
}
