package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.entities.Competitor;
import ch.ffhs.pa.competitionmanager.enums.Gender;

import ch.ffhs.pa.competitionmanager.utils.AgeUtils;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Singleton that implements logic for the separate GUI class.
 * The GUI was created using IntelliJ. Therefore, the structure of some methods was predefined.
 */
public class CompetitorEditor {

    private static CompetitorEditor competitorEditor = null;
    private GlobalState globalState = GlobalState.getInstance();
    private ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
    private Competitor competitor;
    private boolean shouldShowSuccessOrUpdateMessage;

    private JPanel outerPanel;
    private JButton navigateToScoreCreator;
    private JButton navigateToEventSelector;
    private JLabel descriptionLabel;
    private JTextField competitorNameField;
    private JTextField competitorDateField;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton saveAndGoToScoreEditorButton;
    private JLabel title;
    private boolean editExisting;
    private JFrame mainFrame;


    /**
     * Return an instance of object and set GUI to be visible.
     * @return The object (singleton)
     */
    public static CompetitorEditor getInstanceAndSetVisible() {
        return getInstanceAndSetVisible(null);
    }

    /**
     * Return an instance of object and set GUI to be visible. Initialize some fields with attributes of given competitor.
     * @param competitor The competitor to fill some fields.
     * @return The object (singleton)
     */
    public static CompetitorEditor getInstanceAndSetVisible(Competitor competitor) {
        if (competitorEditor == null) {
            competitorEditor = CompetitorEditor.main(competitor);
        }
        competitorEditor.competitor = competitor;
        competitorEditor.setCompetitorValues();
        SwingUtilities.invokeLater(() -> {
            competitorEditor.mainFrame.pack();
            competitorEditor.mainFrame.setVisible(true);
        });
        competitorEditor.shouldShowSuccessOrUpdateMessage = true;
        return competitorEditor;
    }

    private CompetitorEditor(JFrame mainFrame, Competitor competitor) {
        this.competitor = competitor;
        this.editExisting = competitor != null;
        this.mainFrame = mainFrame;
        SwingUtilities.invokeLater(() -> {
            createUIComponents();
        });
    }

    private void createUIComponents() {

        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
        DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

        if (editExisting) {
            setCompetitorValues();
        } else {
            clearAllFields();
        }

        saveButton.addActionListener(e -> createOrUpdateCompetitor());

        saveAndGoToScoreEditorButton.addActionListener(e -> {
            shouldShowSuccessOrUpdateMessage = false;
            boolean noErrorHasOccurred = createOrUpdateCompetitor();
            shouldShowSuccessOrUpdateMessage = true;

            if(noErrorHasOccurred) {
                ScoreCreator.getInstanceAndSetVisible(null, competitor.clone());
                ScoreCreator.focusOnScoreTextField();
                setInvisibleAndClearAllFields();
            }
        });

        deleteButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("CompetitorEditor.deleteAreYouSure"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (shouldBeZero != 0) {
                    return;
                }
                competitor.delete();
                clearAllFields();
                editExisting = false;
                JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.deletedText"));
            });
        });

        maleRadioButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                if (maleRadioButton.isSelected() == true) {
                    femaleRadioButton.setSelected(false);
                }
                maleRadioButton.setSelected(true);
            });
        });

        femaleRadioButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                if (femaleRadioButton.isSelected() == true) {
                    maleRadioButton.setSelected(false);
                }
                femaleRadioButton.setSelected(true);
            });
        });

        navigateToScoreCreator.addActionListener(e -> {
            ScoreCreator.getInstanceAndSetVisible();
            setInvisibleAndClearAllFields();
        });

        navigateToEventSelector.addActionListener(e -> {
            EventSelector.getInstanceAndSetVisible();
            setInvisibleAndClearAllFields();
        });
    }


    private static CompetitorEditor main(Competitor competitor) {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

        JFrame frame = new JFrame(bundle.getString("CompetitorEditor.title"));
        CompetitorEditor competitorEditor = new CompetitorEditor(frame, competitor);

        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(competitorEditor.outerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        return competitorEditor;
    }

    private void setCompetitorValues() {
        if (competitor != null) {
            GlobalState globalState = GlobalState.getInstance();

            ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
            DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

            String competitorBirthDate = dateStringConverter.asString(competitor.getDateOfBirth());

            title.setText(bundle.getString("CompetitorEditor.titleEdit"));
            descriptionLabel.setText(bundle.getString("CompetitorEditor.descEditCompetitor") + competitor.getName());
            descriptionLabel.setVisible(true);
            competitorNameField.setText(competitor.getName());
            competitorDateField.setText(competitorBirthDate);
            if (competitor.getGender() == Gender.FEMALE) {
                femaleRadioButton.setSelected(true);
            } else if (competitor.getGender() == Gender.MALE) {
                maleRadioButton.setSelected(true);
            }
            saveAndGoToScoreEditorButton.setText(bundle.getString("CompetitorEditor.buttonSaveAndGoToScore"));
            saveButton.setText(bundle.getString("CompetitorEditor.buttonSaveChanges"));
            deleteButton.setVisible(true);
        }
    }

    private void clearAllFields() {
        SwingUtilities.invokeLater(() -> {
            title.setText(bundle.getString("CompetitorEditor.titleCreate"));
            descriptionLabel.setVisible(false);
            competitorNameField.setText("");
            competitorDateField.setText("");
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);

            saveAndGoToScoreEditorButton.setText(bundle.getString("CompetitorEditor.buttonCreateAndTotoScore"));
            saveButton.setText(bundle.getString("CompetitorEditor.buttonCreateCompetitor"));
            deleteButton.setVisible(false);

            competitor = null;
        });
    }

    private void setInvisibleAndClearAllFields() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(false);
        });
        clearAllFields();
    }

    private boolean createOrUpdateCompetitor() {
        DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

        if (!editExisting) {
            competitor = new Competitor(-1, null, null, null, -1);
        }
        String name = competitorNameField.getText();
        if (name == null || name.equals("")) {
            JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorEmptyName"));
            return false;
        }
        competitor.setName(competitorNameField.getText());

        try {
            LocalDate dateOfBirth = dateStringConverter.asLocalDate(competitorDateField.getText());
            if (!AgeUtils.isDateOfBirthPlausible(dateOfBirth)) {
                JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorDateOfBirthNotPlausible"));
                return false;
            }
            competitor.setDateOfBirth(dateOfBirth);
            if (maleRadioButton.isSelected()) {
                competitor.setGender(Gender.MALE);
            } else if (femaleRadioButton.isSelected()) {
                competitor.setGender(Gender.FEMALE);
            } else {
                throw new IllegalArgumentException();
            }
        } catch (ParseException e1) {
            JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorDateFormat"));
            return false;
        } catch (IllegalArgumentException e1) {
            JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorNoGenderSelected"));
            return false;
        }

        if (editExisting) {
            if (!competitor.update()) {
                JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorOnPersist"));
            } else {
                if (shouldShowSuccessOrUpdateMessage) {
                    JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.updatedText"));
                }
                clearAllFields();
            }
        } else {
            if (!competitor.create()) {
                JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorOnPersist"));
            } else {
                if (shouldShowSuccessOrUpdateMessage) {
                    JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.newCompCreatedText"));
                }
                clearAllFields();
            }
        }
        return true;
    }
}
