package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CategoryList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Category;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.ResourceBundle;

public class CategoryEditor {
    private static CategoryEditor categoryEditor = null;
    private GlobalState globalState = GlobalState.getInstance();
    private ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
    private Category category;
    private CategoryTableModel categoryTableModel;
    private CategoryList categoryList;
    private TableRowSorter<CategoryTableModel> categoryTableSorter;

    private JFrame mainFrame;

    private JPanel outerPanel;
    private JTextField nameTextField;
    private JTextField descriptionTextField;
    private JTextField maxAgeTextField;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JRadioButton notRelevantRadioButton;
    private JTable categoryTable;
    private JTextField minAgeTextField;

    public static CategoryEditor getInstanceAndSetVisible() {
        if (categoryEditor == null) {
            categoryEditor = categoryEditor.main();
        }
        SwingUtilities.invokeLater(() -> {
            categoryEditor.mainFrame.pack();
            categoryEditor.mainFrame.setVisible(true);
        });
        return categoryEditor;
    }

    private CategoryEditor(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        SwingUtilities.invokeLater(() -> {
            createUIComponents();
        });
    }

    private void createUIComponents() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
        DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

        // Table
        categoryList = globalState.getCategoryList();
        categoryTableModel = categoryList.getCategoriesAsTableModel();
        categoryTable.setModel(categoryTableModel);
        // Sorter & filter. See also filterCategoryTable()
        categoryTableSorter = new TableRowSorter<CategoryTableModel>(categoryTableModel);
        categoryTable.setRowSorter(categoryTableSorter);

    }

    private static CategoryEditor main() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

        JFrame frame = new JFrame(bundle.getString("CategoryEditor.title"));
        CategoryEditor CategoryEditor = new CategoryEditor(frame);

        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(CategoryEditor.outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        return CategoryEditor;
    }

    private void setInvisibleAndClearAllFields() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(false);
        });
        clearAllFields();
    }

    private void clearAllFields() {
        SwingUtilities.invokeLater(() -> {
            nameTextField.setText("");
            descriptionTextField.setText("");
            minAgeTextField.setText("");
            maxAgeTextField.setText("");
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);
            notRelevantRadioButton.setSelected(false);
            categoryTable.getRowSorter().setSortKeys(null);
        });
    }
}
