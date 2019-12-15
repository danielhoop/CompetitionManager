package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.CompetitorList;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.core.Scores;
import ch.ffhs.pa.competitionmanager.entities.Competitor;
import ch.ffhs.pa.competitionmanager.entities.Score;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ScoreEditor {
    private static ScoreEditor scoreEditor = null;

    private GlobalState globalState;
    private ResourceBundle bundle;
    private Score score;
    private Competitor competitor;
    private Scores scores;
    private ScoreTableModel scoreTableModel;
    private TableRowSorter<ScoreTableModel> scoreTableSorter;
    private CompetitorTableModel competitorTableModel;
    private CompetitorList competitorList;
    private TableRowSorter<CompetitorTableModel> competitorTableSorter;
    private JFrame mainFrame;

    private JPanel outerPanel;
    private JTextField nameTextField;
    private JTextField dateOfBirthTextField;
    private JScrollPane scoreScrollPane;
    private JTable scoreTable;
    private JButton navigateToScoreEditor;
    private JButton navigateToEventSelector;
    private JPanel scorePanel;
    private JCheckBox isValidCheckBox;
    private JButton saveButton;
    private JLabel timeNeededLabel;
    private JTextField timeNeededTextField;
    private JLabel pointsAchievedLabel;
    private JTextField pointsAchievedTextField;
    private JButton deleteButton;
    private JLabel text1;
    private JScrollPane competitorScrollPane;
    private JTable competitorTable;
    private JTextField compNameTextField;
    private JTextField compDateOfBirthTextField;
    private JCheckBox deletedCheckBox;

    public static ScoreEditor getInstanceAndSetVisible() {
        if (scoreEditor == null) {
            scoreEditor = ScoreEditor.main(true);
        } else {
            scoreEditor.mainFrame.setVisible(true);
            scoreEditor.reloadScoresFromDb();
        }
        return scoreEditor;
    }

    private static ScoreEditor main(boolean setVisible) {

        ResourceBundle bundle = GlobalState.getInstance().getGuiTextBundle();

        JFrame frame = new JFrame(bundle.getString("ScoreEditor.title"));
        ScoreEditor scoreEditor = new ScoreEditor(frame);
        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(scoreEditor.outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(setVisible);
        });
        return scoreEditor;
    }

    private ScoreEditor(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.globalState = GlobalState.getInstance();
        this.bundle = globalState.getGuiTextBundle();
        createUIComponents();
    }

    private void createUIComponents() {
        // Table for Scores
        scores = new Scores(GlobalState.getInstance().getEvent(), true, true);
        scoreTableModel = scores.getScoresAsTableModel(true,true);
        scoreTable.setModel(scoreTableModel);
        // Sorter & filter. See also filterCompetitorTable()
        scoreTableSorter = new TableRowSorter<ScoreTableModel>(scoreTableModel);
        scoreTable.setRowSorter(scoreTableSorter);

        scoreTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = getSelectedRowOfScoreTable();
            if (selectedRow != -1) {
                score = scoreTableModel.getScoreFromRow(selectedRow).clone();
                competitor = score.getCompetitor().clone();
                timeNeededTextField.setText(safeToString(score.getTimeNeeded()));
                pointsAchievedTextField.setText(safeToString(score.getPointsAchieved()));
                isValidCheckBox.setSelected(score.isValid());
                deletedCheckBox.setSelected(score.isDeleted());
                compNameTextField.setText(score.getCompetitor().getName());
                compDateOfBirthTextField.setText(
                        new DateStringConverter(globalState.getLocale()).asString(score.getCompetitor().getDateOfBirth()));
                filterCompetitorTable();
                if (competitorTable.getRowCount() == 1) {
                    int compSelectedRow = competitorTable.convertRowIndexToModel(0);
                    competitorTable.setRowSelectionInterval(0, 0);
                }
            } else {
                clearAllFields();
            }
        });

        // Table for Competitors
        competitorList = globalState.getCompetitorList();
        competitorTableModel = competitorList.getCompetitorsAsTableModel();
        competitorTable.setModel(competitorTableModel);
        // Sorter & filter. See also filterCompetitorTable()
        competitorTableSorter = new TableRowSorter<CompetitorTableModel>(competitorTableModel);
        competitorTable.setRowSorter(competitorTableSorter);

        nameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                filterScoreTable();
            }
        });

        dateOfBirthTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                filterScoreTable();
            }
        });

        compNameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                filterCompetitorTable();
            }
        });

        compDateOfBirthTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                filterCompetitorTable();
            }
        });

        // Time needed or points achieved
        SwingUtilities.invokeLater(() -> {
            if (globalState.getEvent().isTimeRelevant()) {
                pointsAchievedLabel.setVisible(false);
                pointsAchievedTextField.setVisible(false);
            } else {
                timeNeededLabel.setVisible(false);
                timeNeededTextField.setVisible(false);
            }
        });

        // Save button
        saveButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) {
                boolean savingHasWorked = saveScore();
                if (savingHasWorked) {
                    // Success message, dispose old window and open new one.
                    JOptionPane.showMessageDialog(null, bundle.getString("savingToDbWorked"));
                    scores.reloadFromDb();
                    scoreTableModel.fireTableDataChanged();
                    clearAllFields();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });

        // Delete button
        deleteButton.addActionListener(e -> {
            int selectedRow = getSelectedRowOfScoreTable();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, bundle.getString("ScoreCreator.errorNoCompetitorSelected"));
                return;
            }

            SwingUtilities.invokeLater(() -> {
                int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("ScoreEditor.scoreDeleteAreYourSure"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (shouldBeZero != 0) {
                    return;
                }
                Score score = scoreTableModel.getScoreFromRow(selectedRow).clone();
                score.delete();
                scores.reloadFromDb();
                scoreTableModel.fireTableDataChanged();
                clearAllFields();
                JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.deletedText"));
            });
        });

        navigateToScoreEditor.addActionListener(e -> {
            ScoreCreator.getInstanceAndSetVisible();
            setInvisibleAndClearAllFields();
        });

        navigateToEventSelector.addActionListener(e -> {
            EventSelector.getInstanceAndSetVisible();
            setInvisibleAndClearAllFields();
        });
    }

    private void setInvisibleAndClearAllFields() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(false);
        });
        clearAllFields();
    }

    private void clearAllFields() {
        score = null;
        competitor = null;
        SwingUtilities.invokeLater(() -> {
            nameTextField.setText("");
            dateOfBirthTextField.setText("");
            compNameTextField.setText("");
            compDateOfBirthTextField.setText("");
            timeNeededTextField.setText("");
            pointsAchievedTextField.setText("");
            isValidCheckBox.setSelected(false);
            deletedCheckBox.setSelected(false);

            scoreTableSorter.setRowFilter(null);
            scoreTableModel.fireTableDataChanged();
            competitorTableSorter.setRowFilter(null);
            competitorTableModel.fireTableDataChanged();
        });
    }

    private void reloadScoresFromDb() {
        scores.reloadFromDb();
        scoreTableModel.fireTableDataChanged();
    }

    private void filterScoreTable() {
        List<RowFilter<Object, Object>> filters = new LinkedList<>();
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
            filters.add(RowFilter.regexFilter(dateOfBirth, 1));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }

        // Combine filters and apply. https://stackoverflow.com/questions/31372553/jtable-rowfilter-between-two-dates-same-column
        scoreTableSorter.setRowFilter(RowFilter.andFilter(filters));
        scoreTableModel.fireTableDataChanged();
    }

    private void filterCompetitorTable() {
        List<RowFilter<Object, Object>> filters = new LinkedList<>();
        // First, filter on name
        try {
            // If current expression doesn't parse, don't update.
            // https://stackoverflow.com/questions/7904695/java-escaping-meta-characters-and-in-regex
            String name = "(?i)" + java.util.regex.Pattern.quote(compNameTextField.getText());
            filters.add(RowFilter.regexFilter(name, 0));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        // Then filter on birthday
        try {
            // If current expression doesn't parse, don't update.
            // https://stackoverflow.com/questions/7904695/java-escaping-meta-characters-and-in-regex
            String dateOfBirth = java.util.regex.Pattern.quote(compDateOfBirthTextField.getText());
            filters.add(RowFilter.regexFilter(dateOfBirth, 1));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }

        // Combine filters and apply. https://stackoverflow.com/questions/31372553/jtable-rowfilter-between-two-dates-same-column
        competitorTableSorter.setRowFilter(RowFilter.andFilter(filters));
        competitorTableModel.fireTableDataChanged();
    }

    private int getSelectedRowOfScoreTable() {
        int selectedRow = -1;
        // Exception happens when competitorTable.getSelectedRow() is -1.
        try {
            selectedRow = scoreTable.convertRowIndexToModel(scoreTable.getSelectedRow());
        } catch (IndexOutOfBoundsException ex) {
            selectedRow = -1;
        }
        if (selectedRow == -1 && scoreTable.getRowCount() == 1) {
            selectedRow = scoreTable.convertRowIndexToModel(0);
        }
        return selectedRow;
    }
    private int getSelectedRowOfCompetitorTable() {
        int selectedRow = -1;
        // Exception happens when competitorTable.getSelectedRow() is -1.
        try {
            selectedRow = competitorTable.convertRowIndexToModel(competitorTable.getSelectedRow());
        } catch (IndexOutOfBoundsException ex) {
            selectedRow = -1;
        }
        if (selectedRow == -1 && competitorTable.getRowCount() == 1) {
            selectedRow = competitorTable.convertRowIndexToModel(0);
        }
        return selectedRow;
    }

    private boolean saveScore() {

        int selectedScoreRow = getSelectedRowOfScoreTable();
        if (selectedScoreRow == -1) {
            JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorNoScoreSelected"));
            return false;
        }

        int selectedCompetitorRow = getSelectedRowOfCompetitorTable();
        if (selectedCompetitorRow == -1) {
            JOptionPane.showMessageDialog(null, bundle.getString("ScoreEditor.errorNoCompetitorSelected"));
            return false;
        }

        Score newScore = scoreTableModel.getScoreFromRow(selectedScoreRow).clone();
        Competitor newCompetitor = competitorTableModel.getCompetitorFromRow(selectedCompetitorRow).clone();
        if (!competitor.equals(newCompetitor)) {
            int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("ScoreEditor.competitorChangedAreYouSure"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (shouldBeZero != 0) {
                return false;
            }
            newScore.setCompetitor(newCompetitor);
        }

        if (timeNeededTextField.getText().equals("")) {
            newScore.setTimeNeeded(null);
        } else {
            LocalTime timeNeeded;
            try {
                timeNeeded = LocalTime.parse(timeNeededTextField.getText(), DateTimeFormatter.ISO_LOCAL_TIME);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(null, bundle.getString("ScoreCreator.errorTimeNotParsed"));
                return false;
            }
            newScore.setTimeNeeded(timeNeeded);
        }

        if (pointsAchievedTextField.getText().equals("")) {
            newScore.setPointsAchieved(null);
        } else {
            double pointsAchieved;
            try {
                pointsAchieved = Double.valueOf(pointsAchievedTextField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, bundle.getString("ScoreCreator.errorPointsNotParsed"));
                return false;
            }
        }

        newScore.setValid(isValidCheckBox.isSelected());

        boolean shouldDelete = false;
        if (!newScore.isDeleted() && deletedCheckBox.isSelected()) {
            int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("ScoreEditor.scoreDeleteAreYourSure"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (shouldBeZero == 0) {
                shouldDelete = true;
            }
        }
        newScore.setDeleted(deletedCheckBox.isSelected());

        boolean hasWorked = newScore.update();
        if (shouldDelete)
            hasWorked = hasWorked && newScore.delete();

        return hasWorked;
    }

    private String safeToString(Object o) {
        if (o == null)
            return "";
        return o.toString();
    }
}
