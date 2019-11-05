package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CompetitorList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.dto.Score;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ScoreEditor {

    GlobalState globalState;
    ResourceBundle bundle;
    CompetitorTableModel competitorTableModel;
    CompetitorList competitorList;
    TableRowSorter<CompetitorTableModel> competitorTableSorter;
    Score scoreToEdit;
    boolean editExisting;

    private JPanel outerPanel;
    private JLabel text1;
    private JTextField nameTextField;
    private JTextField dateOfBirthTextField;
    private JTable competitorTable;
    private JTextField timeNeededTextField;
    private JCheckBox isValidCheckBox;
    private JButton saveButton;
    private JTextField pointsAchievedTextField;
    private JLabel timeNeededLabel;
    private JLabel pointsAchievedLabel;
    private JButton neueNWettkämpferInButton;
    private JButton reloadCompetitorsButton;
    private JScrollPane competitorScrollPane;
    private JButton wettkämpferInBearbeitenButton;
    private int selectedRow;

    private ScoreEditor(JFrame mainFrame, Score scoreToEdit) {
        this.scoreToEdit = scoreToEdit;
        this.editExisting = scoreToEdit != null;

        createUIComponents();


        // Do not move the code below into createUIComponents() !
        globalState = GlobalState.getInstance();
        bundle = globalState.getGuiTextBundle();

        // Create text1 string, depending on whether time is relevant or points.
        // TODO: Does not work.
        String text1String = bundle.getString("ScoreEditor.text1.start");
        if (globalState.getEvent().isTimeRelevant()) {
            text1String += bundle.getString("ScoreEditor.text1.time");
        } else {
            text1String += bundle.getString("ScoreEditor.text1.points");
        }
        text1.setText(text1String);

        // Add listeners to text fields.
        // It won't work in the createUIComponents() method!!!
        nameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                filterCompetitorTable();
            }
        });

        dateOfBirthTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                filterCompetitorTable();
            }
        });

        // Table
        competitorList = globalState.getCompetitorList();
        competitorTableModel = competitorList.getCompetitorsAsTableModel();
        competitorTable.setModel(competitorTableModel);
        // Sorter & filter. See also filterCompetitorTable()
        competitorTableSorter = new TableRowSorter<CompetitorTableModel>(competitorTableModel);
        competitorTable.setRowSorter(competitorTableSorter);

        competitorTable.getSelectionModel().addListSelectionListener(e -> {
            selectedRow = competitorTable.getSelectedRow();
            // System.out.println("RowSelectionEvent fired. selectedRow: " + selectedRow);
        });

        // Time needed or points achieved
        if (globalState.getEvent().isTimeRelevant()) {
            pointsAchievedLabel.setVisible(false);
            pointsAchievedTextField.setVisible(false);
        } else {
            timeNeededLabel.setVisible(false);
            timeNeededTextField.setVisible(false);
        }

        // Reload Competitors Button
        reloadCompetitorsButton = new JButton();
        reloadCompetitorsButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                globalState.reloadCompetitorListFromDb();
                competitorTableModel.fireTableDataChanged();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        // Button must either say "save" or "save changes".
        // TODO: Does not work.
        if (editExisting) {
            saveButton.setText(bundle.getString("change"));
        } else {
            saveButton.setText(bundle.getString("save"));
        }
        saveButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                boolean savingHasWorked = saveOrEditScore();
                if (savingHasWorked) {
                    // Success message, dispose old window and open new one.
                    JOptionPane.showMessageDialog(null, bundle.getString("savingToDbWorked"));
                    mainFrame.dispose();
                    ScoreEditor.main();
                } // else { JOptionPane.showMessageDialog(null, bundle.getString("savingToDbFailed")); }
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        wettkämpferInBearbeitenButton.addActionListener(e -> {
            Competitor selectedCompetitor = competitorTableModel.getCompetitorFromRow(selectedRow);
            GlobalState.getInstance().setCompetitor(selectedCompetitor);
            mainFrame.dispose();
            CompetitorEditor.main(false);
        });

        neueNWettkämpferInButton.addActionListener(e -> {
            mainFrame.dispose();
            CompetitorEditor.main(true);
        });
    }

    private void createUIComponents() {
    }

    public static void main() {
        main(null);
    }
    public static void main(Score scoreToEdit) {
        SwingUtilities.invokeLater(() -> {
            ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();

            JFrame frame = new JFrame(bundle.getString("ScoreEditor.title"));
            frame.setContentPane(new ScoreEditor(frame, scoreToEdit).outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }




    private void filterCompetitorTable() {
        List<RowFilter<Object,Object>> filters = new LinkedList<>();
        // First, filter on name
        try {
            // If current expression doesn't parse, don't update.
            // https://stackoverflow.com/questions/7904695/java-escaping-meta-characters-and-in-regex
            String name = "(?i)" + java.util.regex.Pattern.quote(nameTextField.getText());
            filters.add(RowFilter.regexFilter(name, 0));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        // Then filter on birthday
        try {
            // If current expression doesn't parse, don't update.
            // https://stackoverflow.com/questions/7904695/java-escaping-meta-characters-and-in-regex
            String dateOfBirth = java.util.regex.Pattern.quote(dateOfBirthTextField.getText());
            System.out.println(dateOfBirth);
            filters.add(RowFilter.regexFilter(dateOfBirth, 1));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }

        // Combine filters and apply. https://stackoverflow.com/questions/31372553/jtable-rowfilter-between-two-dates-same-column
        competitorTableSorter.setRowFilter(RowFilter.andFilter(filters));
        competitorTableModel.fireTableDataChanged();
    }




    private boolean saveOrEditScore() {
        boolean shouldContinue = true;
        boolean isValid = isValidCheckBox.isSelected();
        int selectedRow = -1;
        // Exception happens when table was not sorted/filtered at all.
        try {
            selectedRow = competitorTable.convertRowIndexToModel(competitorTable.getSelectedRow());
        } catch (IndexOutOfBoundsException ex) {
            selectedRow = competitorTable.getSelectedRow();
        }

        // Continue only if a row is selected or only 1 row is displayed
        if (!editExisting && selectedRow == -1) {
            if (competitorTable.getRowCount() == 1) {
                selectedRow = 0;
            } else {
                JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorNoCompetitorSelected"));
                shouldContinue = false;
            }
        }

        // Continue only if time and double can be parsed.
        LocalTime timeNeeded = null;
        Double pointsAchieved = null;
        if (shouldContinue) {
            if (globalState.getEvent().isTimeRelevant()) {
                // TODO: Time has to be parsed from textField
                try {
                    timeNeeded = LocalTime.parse(timeNeededTextField.getText(), DateTimeFormatter.ISO_LOCAL_TIME);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorTimeNotParsed"));
                    shouldContinue = false;
                }
            } else {
                try {
                    pointsAchieved = Double.valueOf(pointsAchievedTextField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorPointsNotParsed"));
                    shouldContinue = false;
                }
            }
        }

        if (shouldContinue) {
            // Get competitor
            Competitor competitor;
            if (editExisting && selectedRow == -1) {
                competitor = scoreToEdit.getCompetitor();
            } else {
                competitor = competitorTableModel.getCompetitorFromRow(selectedRow);
                //System.out.println("Selected competitor: " + competitor.getName());
            }

            // Assert that competitor was not changed by by accident.
            if (editExisting) {
                if (scoreToEdit.getCompetitor() != competitor) {
                    int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("ScoreEditor.hintCompetitorIsNotTheSame"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    shouldContinue = shouldBeZero == 0;
                }
                if (shouldContinue) {
                    scoreToEdit.setCompetitor(competitor);
                    scoreToEdit.setTimeNeeded(timeNeeded);
                    scoreToEdit.setPointsAchieved(pointsAchieved);
                    scoreToEdit.setNumberOfTries(1);
                    scoreToEdit.setValid(isValid);
                    // Update in database.
                    return scoreToEdit.update();
                }
            } else {
                // Create object and save into database (score.create()).
                Score score = new Score(-1, globalState.getEvent().getId(), competitor, timeNeeded, null, 1, isValid, LocalDateTime.now());
                return score.create();
            }
        }
        return false;
    }
}
