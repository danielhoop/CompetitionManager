package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CompetitorList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.utils.DateStringHandler;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CompetitorTableModel extends AbstractTableModel {

    private ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();
    DateStringHandler dateStringHandler = new DateStringHandler(GlobalState.getInstance().getLocale());

    private List<Competitor> competitors;
    private String[] columns;

    public CompetitorTableModel(CompetitorList competitorList) {
        this.competitors = competitorList.getCompetitors();
        this.columns = new String[]{
                bundle.getString("Competitor.name"),
                bundle.getString("Competitor.dateOfBirth"),
                bundle.getString("Competitor.gender")
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Competitor competitor = competitors.get(rowIndex);
        LocalDate dateOfBirth = competitor.getDateOfBirth();
        switch (columnIndex) {
            case 0: return competitor.getName();
            case 1: return dateStringHandler.asString(competitor.getDateOfBirth());
            case 2: return competitor.getGender();
            default: return null;
        }
    }

    public Competitor getCompetitorFromRow(int rowIndex) {
        return competitors.get(rowIndex);
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
