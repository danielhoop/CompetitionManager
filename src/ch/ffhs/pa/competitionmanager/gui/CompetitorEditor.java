package ch.ffhs.pa.competitionmanager.gui;

import ch.danielhoop.utils.ExceptionVisualizer;
import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.enums.Gender;

import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ResourceBundle;

/**
 * Signleton
 */
public class CompetitorEditor {

    private static CompetitorEditor competitorEditor = null;
    private GlobalState globalState = GlobalState.getInstance();
    private ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
    private Competitor competitor;

    private JPanel outerPanel;
    private JButton navigateToScoreEditor;
    private JButton navigateToEventSelector;
    private JButton newCompetitorCreateButton;
    private JLabel descriptionLabel;
    private JTextField competitorNameField;
    private JTextField competitorDateField;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JButton updateCompetitorButton;
    private JButton deleteCompetitorButton;
    private boolean editExisting;
    private JFrame mainFrame;
    private ActionListener updateActionListener;
    private Competitor newCompetitor;


    public static CompetitorEditor getInstanceAndSetVisible() {
        return getInstanceAndSetVisible(null);
    }

    public static CompetitorEditor getInstanceAndSetVisible(Competitor competitor) {
        if (competitorEditor == null) {
            competitorEditor = CompetitorEditor.main(competitor);
        }
        competitorEditor.competitor = competitor;
        competitorEditor.setCompetitorValues();
        competitorEditor.mainFrame.setVisible(true);
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

        ActionListener createUpdateActionListener = (e) -> {
            SwingUtilities.invokeLater(() -> {
                if (!editExisting) {
                    competitor = new Competitor(-1, null, null, null, -1);
                }
                String name = competitorNameField.getText();
                if (name == null || name.equals("")) {
                    JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorEmptyName"));
                    return;
                }
                competitor.setName(competitorNameField.getText());

                try {
                    competitor.setDateOfBirth(dateStringConverter.asLocalDate(competitorDateField.getText()));
                    if (maleRadioButton.isSelected()) {
                        competitor.setGender(Gender.MALE);
                    } else if (femaleRadioButton.isSelected()) {
                        competitor.setGender(Gender.FEMALE);
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (ParseException e1) {
                    JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorDateFormat"));
                    return;
                } catch (IllegalArgumentException e1) {
                    JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorNoGenderSelected"));
                    return;
                }

                if (editExisting) {
                    if (!competitor.update()) {
                        JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorOnPersist"));
                    } else {
                        JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.updatedText"));
                        clearAllFields();
                    }
                } else {
                    if (!competitor.create()) {
                        JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorOnPersist"));
                    } else {
                        JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.newCompCreatedText"));
                        clearAllFields();
                    }
                }
            });
        };

        updateCompetitorButton.addActionListener(createUpdateActionListener);
        newCompetitorCreateButton.addActionListener(createUpdateActionListener);

        deleteCompetitorButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                int shouldBeZero = JOptionPane.showConfirmDialog(null, bundle.getString("CompetitorEditor.deleteAreYouSure"), bundle.getString("pleaseConfirm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (shouldBeZero != 0) {
                    return;
                }
                competitor.delete();
                clearAllFields();
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

        navigateToScoreEditor.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                ScoreEditor.getInstanceAndSetVisible();
                setInvisibleAndClearAllFields();
            });
        });

        navigateToEventSelector.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                EventSelector.getInstanceAndSetVisible();
                setInvisibleAndClearAllFields();
            });
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

            descriptionLabel.setText(bundle.getString("CompetitorEditor.descEditCompetitor") + competitor.getName());
            competitorNameField.setText(competitor.getName());
            competitorDateField.setText(competitorBirthDate);
            if (competitor.getGender() == Gender.FEMALE) {
                femaleRadioButton.setSelected(true);
            } else if (competitor.getGender() == Gender.MALE) {
                maleRadioButton.setSelected(true);
            }
            newCompetitorCreateButton.setVisible(false);
            updateCompetitorButton.setVisible(true);
            deleteCompetitorButton.setVisible(true);
        }
    }

    private void clearAllFields() {
        competitor = null;
        SwingUtilities.invokeLater(() -> {
            descriptionLabel.setText(bundle.getString("CompetitorEditor.titleDescription"));
            competitorNameField.setText("");
            competitorDateField.setText("");
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);

            newCompetitorCreateButton.setVisible(true);
            updateCompetitorButton.setVisible(false);
            deleteCompetitorButton.setVisible(false);
        });
    }

    private void setInvisibleAndClearAllFields() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(false);
        });
        clearAllFields();
    }

}
