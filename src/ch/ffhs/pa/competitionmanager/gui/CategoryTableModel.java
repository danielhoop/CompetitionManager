package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CategoryList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;
import ch.ffhs.pa.competitionmanager.utils.GenderStringConverter;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ResourceBundle;

public class CategoryTableModel extends AbstractTableModel {

    private ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();
    DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

    private List<Category> categories;
    private String[] columns;
    private final int nameIdx, descriptionIdx, minAgeInclusiveIdx, maxAgeInclusiveIdx, genderIdx;

    public CategoryTableModel(CategoryList categoryList) {
        categories = categoryList.getCategories();
        columns = new String[]{
                bundle.getString("Category.name"),
                bundle.getString("Category.description"),
                bundle.getString("Category.minAgeInclusive"),
                bundle.getString("Category.maxAgeInclusive"),
                bundle.getString("Category.gender")
        };
        nameIdx = 0;
        descriptionIdx = 1;
        minAgeInclusiveIdx = 2;
        maxAgeInclusiveIdx = 3;
        genderIdx = 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Category category = categories.get(rowIndex);

        if (columnIndex == nameIdx)
            return category.getName();
        if (columnIndex == descriptionIdx)
            return category.getDescription();
        if (columnIndex == minAgeInclusiveIdx)
            return category.getMinAgeInclusive();
        if (columnIndex == maxAgeInclusiveIdx)
            return category.getMaxAgeInclusive();
        if (columnIndex == genderIdx)
            return new GenderStringConverter().asString(category.getGender());
        return null;
    }

    public Category getCategoryFromRow(int rowIndex) {
        return categories.get(rowIndex);
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
        return categories.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }
}
