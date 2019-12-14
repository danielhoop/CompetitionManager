package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CategoryList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Category;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.enums.Gender;
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
    private JButton saveButton;
    private JButton resetSelectionButton;
    private JButton deleteButton;
    private JLabel title;
    private JButton navigateToEventSelectorButton;

    public static CategoryEditor getInstanceAndSetVisible() {
        if (categoryEditor == null) {
            categoryEditor = categoryEditor.main();
        }
        SwingUtilities.invokeLater(() -> {
            // The table has to be created each time the window opens, because the event could have changed.
            categoryEditor.createCategoryTable();
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

    private void createCategoryTable() {
        categoryList = globalState.getCategoryList();
        categoryTableModel = categoryList.getCategoriesAsTableModel();
        categoryTable.setModel(categoryTableModel);
        // Sorter & filter. See also filterCategoryTable()
        categoryTableSorter = new TableRowSorter<CategoryTableModel>(categoryTableModel);
        categoryTable.setRowSorter(categoryTableSorter);
    }

    private void createUIComponents() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
        DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

        // Title
        Event event = globalState.getEvent();
        String eventDate = new DateStringConverter(globalState.getLocale()).asString(event.getDate());
        title.setText(bundle.getString("CategoryEditor.forEvent") + " " +
                event.getName() + " (" +  eventDate + ")");

        // Table
        createCategoryTable();

        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            setSelectedCategory();
        });

        // Radio buttons
        maleRadioButton.addActionListener(e -> {
            setGenderButton(maleRadioButton);
        });
        femaleRadioButton.addActionListener(e -> {
            setGenderButton(femaleRadioButton);
        });
        notRelevantRadioButton.addActionListener(e -> {
            setGenderButton(notRelevantRadioButton);
        });

        // Reset selection button
        resetSelectionButton.addActionListener(e -> {
            clearAllFields();
        });

        // Save button
        setSaveButtonText();
        saveButton.addActionListener(e -> {
            boolean editExisting = category != null;

            // This method call will set this.category! I.e., it will not be null anymore!
            boolean isCategoryInputValid = fillCategoryFromFields();
            if (!isCategoryInputValid)
                return;

            if (editExisting) {
                if (category.update()) {
                    JOptionPane.showMessageDialog(null, bundle.getString("savingToDbWorked"));
                    clearAllFields();
                }
            } else {
                if (category.create()) {
                    JOptionPane.showMessageDialog(null, bundle.getString("savingToDbWorked"));
                    clearAllFields();
                }
            }
            globalState.reloadCategoryListFromDb();
            categoryTableModel.fireTableDataChanged();
        });

        // Delete button
        deleteButton.addActionListener(e -> {
            int selectedRow = getSelectedRowOfTable();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, bundle.getString("CategoryEditor.noCategorySelected"));
            } else {
                int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("CategoryEditor.sureToDelete"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (shouldBeZero == 0) {
                    if (category.delete()) {
                        JOptionPane.showMessageDialog(null, bundle.getString("deletingInDbWorked"));
                    }
                }
            }
            globalState.reloadCategoryListFromDb();
            categoryTableModel.fireTableDataChanged();
        });

        // Navigate to event selector
        navigateToEventSelectorButton.addActionListener(e -> {
            setInvisibleAndClearAllFields();
            EventSelector.getInstanceAndSetVisible();
        });
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

    private void clearAllFieldsButTable() {
        clearAllFields(false);
    }

    private void clearAllFields() {
        clearAllFields(true);
    }

    private void clearAllFields(boolean clearTableSorting) {
        category = null;
        SwingUtilities.invokeLater(() -> {
            nameTextField.setText("");
            descriptionTextField.setText("");
            minAgeTextField.setText("");
            maxAgeTextField.setText("");
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);
            notRelevantRadioButton.setSelected(false);
            saveButton.setText(bundle.getString("CategoryEditor.buttonSaveNew"));
            if (clearTableSorting) {
                categoryTableSorter.setRowFilter(null);
                categoryTableModel.fireTableDataChanged();
            }
            globalState.reloadCategoryListFromDb();
            categoryTableModel.fireTableDataChanged();
        });
    }

    private int getSelectedRowOfTable() {
        int selectedRow = -1;
        // Exception happens when categoryTable.getSelectedRow() is -1.
        try {
            selectedRow = categoryTable.convertRowIndexToModel(categoryTable.getSelectedRow());
        } catch (IndexOutOfBoundsException ex) {
            selectedRow = -1;
        }
        /*if (!editExisting && selectedRow == -1 && categoryTable.getRowCount() == 1) {
            selectedRow = categoryTable.convertRowIndexToModel(0);
        }*/
        return selectedRow;
    }


    private void setSelectedCategory() {
        int selectedRow = getSelectedRowOfTable();
        if (selectedRow == -1) {
            setCategory(null);
        } else {
            setCategory(categoryTableModel.getCategoryFromRow(selectedRow));
        }
        setSaveButtonText();
    }

    private void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            SwingUtilities.invokeLater(() -> {
                nameTextField.setText(category.getName());
                descriptionTextField.setText(category.getDescription());
                minAgeTextField.setText(Integer.toString(category.getMinAgeInclusive()));
                maxAgeTextField.setText(Integer.toString(category.getMaxAgeInclusive()));
                setGenderButton(category.getGender());
            });
        } else {
            clearAllFieldsButTable();
        }
    }

    private boolean fillCategoryFromFields() {
        String name = nameTextField.getText();
        String description = descriptionTextField.getText();
        int minAgeInclusive;
        int maxAgeInclusive;

        try {
            minAgeInclusive = Integer.valueOf(minAgeTextField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("CategoryEditor.minAgeIsEmpty"));
            return false;
        }

        try {
            maxAgeInclusive = Integer.valueOf(maxAgeTextField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("CategoryEditor.maxAgeIsEmpty"));
            return false;
        }
        Gender gender = getSelectedGender();
        if (gender == null) {
            JOptionPane.showMessageDialog(null, bundle.getString("CategoryEditor.errorNoGenderSelected"));
            return false;
        }

        if (category == null) {
            category = new Category(-1, globalState.getEvent().getId(), name, description, minAgeInclusive, maxAgeInclusive, gender);
        } else {
            category.setName(name);
            category.setDescription(description);
            category.setMinAgeInclusive(minAgeInclusive);
            category.setMaxAgeInclusive(maxAgeInclusive);
            category.setGender(gender);
        }
        return true;
    }

    private void setGenderButton(Gender gender) {
        SwingUtilities.invokeLater(() -> {
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);
            notRelevantRadioButton.setSelected(false);
            if (gender != null) {
                if (gender == Gender.MALE) {
                    maleRadioButton.setSelected(true);
                } else if (gender == Gender.FEMALE) {
                    femaleRadioButton.setSelected(true);
                } else if (gender == Gender.NOT_RELEVANT) {
                    notRelevantRadioButton.setSelected(true);
                } else {
                    throw new IllegalArgumentException("Type of gender not supported.");
                }
            }
        });
    }

    private void setGenderButton(JRadioButton button) {
        SwingUtilities.invokeLater(() -> {
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);
            notRelevantRadioButton.setSelected(false);
            if (button != null) {
                if (button == maleRadioButton) {
                    maleRadioButton.setSelected(true);
                } else if (button == femaleRadioButton) {
                    femaleRadioButton.setSelected(true);
                } else if (button == notRelevantRadioButton) {
                    notRelevantRadioButton.setSelected(true);
                } else {
                    throw new IllegalArgumentException("Type of gender not supported.");
                }
            }
        });
    }

    private Gender getSelectedGender() {
        if (maleRadioButton.isSelected())
            return Gender.MALE;
        if (femaleRadioButton.isSelected())
            return Gender.FEMALE;
        if (notRelevantRadioButton.isSelected())
            return Gender.NOT_RELEVANT;
        return null;
    }

    private void setSaveButtonText() {
        if (category == null) {
            saveButton.setText(bundle.getString("CategoryEditor.buttonSaveNew"));
        } else {
            saveButton.setText(bundle.getString("CategoryEditor.buttonSaveChanges"));
        }
    }
}
