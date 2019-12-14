package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.EventList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Event;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EventEditor {
    private static EventEditor eventEditor = null;
    private GlobalState globalState = GlobalState.getInstance();
    private ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
    private Event event;
    private EventTableModel eventTableModel;
    private EventList eventList;
    private TableRowSorter<EventTableModel> eventTableSorter;

    private JFrame mainFrame;

    private JPanel outerPanel;
    private JTextField nameTextField;
    private JTextField descriptionTextField;
    private JRadioButton isTimeRelevantRadioButton;
    private JRadioButton arePointsRelevantRadioButton;
    private JTable eventTable;
    private JTextField dateTextField;
    private JButton saveButton;
    private JButton resetSelectionButton;
    private JButton deleteButton;
    private JButton navigateToEventSelectorButton;
    private JTextField dateDescriptionTextField;

    public static EventEditor getInstanceAndSetVisible() {
        if (eventEditor == null) {
            eventEditor = eventEditor.main();
        }
        SwingUtilities.invokeLater(() -> {
            eventEditor.mainFrame.pack();
            eventEditor.mainFrame.setVisible(true);
        });
        return eventEditor;
    }

    private EventEditor(JFrame mainFrame) {
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
        eventList = globalState.getEventList();
        eventTableModel = eventList.getEventsAsTableModel();
        eventTable.setModel(eventTableModel);
        // Sorter & filter. See also filterEventTable()
        eventTableSorter = new TableRowSorter<EventTableModel>(eventTableModel);
        eventTable.setRowSorter(eventTableSorter);

        eventTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                setSelectedEvent();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        // Radio buttons
        isTimeRelevantRadioButton.addActionListener(e -> {
            setModeButton(isTimeRelevantRadioButton);
        });
        arePointsRelevantRadioButton.addActionListener(e -> {
            setModeButton(arePointsRelevantRadioButton);
        });

        // Reset selection button
        resetSelectionButton.addActionListener(e -> {
            clearAllFields();
        });

        // Save button
        setSaveButtonText();
        saveButton.addActionListener(e -> {
            boolean editExisting = event != null;

            // This method call will set this.event! I.e., it will not be null anymore!
            boolean isEventInputValid = fillEventFromFields();
            if (!isEventInputValid)
                return;

            if (editExisting) {
                if (event.update()) {
                    JOptionPane.showMessageDialog(null, bundle.getString("savingToDbWorked"));
                    clearAllFields();
                }
            } else {
                if (event.create()) {
                    JOptionPane.showMessageDialog(null, bundle.getString("savingToDbWorked"));
                    clearAllFields();
                }
            }
            globalState.reloadEventListFromDb();
            eventTableModel.fireTableDataChanged();
        });

        // Delete button
        deleteButton.addActionListener(e -> {
            int selectedRow = getSelectedRowOfTable();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, bundle.getString("EventEditor.noEventSelected"));
            } else {
                int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("EventEditor.sureToDelete"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (shouldBeZero == 0) {
                    if (event.delete()) {
                        JOptionPane.showMessageDialog(null, bundle.getString("deletingInDbWorked"));
                    }
                }
            }
            globalState.reloadEventListFromDb();
            eventTableModel.fireTableDataChanged();
        });

        // Navigate to event selector
        navigateToEventSelectorButton.addActionListener(e -> {
            setInvisibleAndClearAllFields();
            EventSelector.getInstanceAndSetVisible();
        });
    }

    private static EventEditor main() {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

        JFrame frame = new JFrame(bundle.getString("EventEditor.title"));
        EventEditor EventEditor = new EventEditor(frame);

        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(EventEditor.outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        return EventEditor;
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
        event = null;
        SwingUtilities.invokeLater(() -> {
            nameTextField.setText("");
            dateTextField.setText("");
            dateDescriptionTextField.setText("");
            descriptionTextField.setText("");
            isTimeRelevantRadioButton.setSelected(false);
            arePointsRelevantRadioButton.setSelected(false);
            saveButton.setText(bundle.getString("EventEditor.buttonSaveNew"));
            if (clearTableSorting) {
                eventTableSorter.setRowFilter(null);
                eventTableModel.fireTableDataChanged();
            }
            globalState.reloadEventListFromDb();
            eventTableModel.fireTableDataChanged();
        });
    }

    private int getSelectedRowOfTable() {
        int selectedRow = -1;
        // Exception happens when eventTable.getSelectedRow() is -1.
        try {
            selectedRow = eventTable.convertRowIndexToModel(eventTable.getSelectedRow());
        } catch (IndexOutOfBoundsException ex) {
            selectedRow = -1;
        }
        /*if (!editExisting && selectedRow == -1 && eventTable.getRowCount() == 1) {
            selectedRow = eventTable.convertRowIndexToModel(0);
        }*/
        return selectedRow;
    }


    private void setSelectedEvent() {
        int selectedRow = getSelectedRowOfTable();
        if (selectedRow == -1) {
            setEvent(null);
        } else {
            setEvent(eventTableModel.getEventFromRow(selectedRow));
        }
        setSaveButtonText();
    }

    private void setEvent(Event event) {
        this.event = event;
        if (event != null) {
            SwingUtilities.invokeLater(() -> {
                nameTextField.setText(event.getName());
                dateTextField.setText(new DateStringConverter(globalState.getLocale()).asString(event.getDate()));
                dateDescriptionTextField.setText(event.getDateDescription());
                descriptionTextField.setText(event.getDescription());
                setModeButton(event.isTimeRelevant());
            });
        } else {
            clearAllFieldsButTable();
        }
    }

    private boolean fillEventFromFields() {
        String name = nameTextField.getText();
        String description = descriptionTextField.getText();
        String dateDescription = dateDescriptionTextField.getText();

        LocalDate date;
        try {
            date = new DateStringConverter(globalState.getLocale()).asLocalDate(dateTextField.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("EventEditor.errorDateFormat"));
            return false;
        }

        Boolean isTimeRelevant = getMode();
        if (isTimeRelevant == null) {
            JOptionPane.showMessageDialog(null, bundle.getString("EventEditor.errorNoModeSelected"));
            return false;
        }

        if (event == null) {
            event = new Event(-1, name, date, dateDescription, description, isTimeRelevant);
        } else {
            event.setName(name);
            event.setDate(date);
            event.setDateDescription(dateDescription);
            event.setDescription(description);
            event.setTimeRelevant(isTimeRelevant);
        }
        return true;
    }

    private void setModeButton(boolean isTimeRelevant) {
        SwingUtilities.invokeLater(() -> {
            isTimeRelevantRadioButton.setSelected(isTimeRelevant);
            arePointsRelevantRadioButton.setSelected(!isTimeRelevant);
            // throw new IllegalArgumentException("Type of gender not supported.");
        });
    }

    private void setModeButton(JRadioButton button) {
        SwingUtilities.invokeLater(() -> {
            isTimeRelevantRadioButton.setSelected(false);
            arePointsRelevantRadioButton.setSelected(false);
            if (button != null) {
                if (button == isTimeRelevantRadioButton) {
                    isTimeRelevantRadioButton.setSelected(true);
                } else if (button == arePointsRelevantRadioButton) {
                    arePointsRelevantRadioButton.setSelected(true);
                } else {
                    throw new IllegalArgumentException("This mode is not supported.");
                }
            }
        });
    }

    public Boolean getMode() {
        if (!(isTimeRelevantRadioButton.isSelected() || arePointsRelevantRadioButton.isSelected())) {
            return null;
        }
        return isTimeRelevantRadioButton.isSelected();
    }

    private void setSaveButtonText() {
        if (event == null) {
            saveButton.setText(bundle.getString("EventEditor.buttonSaveNew"));
        } else {
            saveButton.setText(bundle.getString("EventEditor.buttonSaveChanges"));
        }
    }
}
