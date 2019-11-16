package ch.ffhs.pa.competitionmanager.gui;

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

    private void createUIComponents (){

        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
        DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

        if (editExisting){
            setCompetitorValues();

            updateCompetitorButton.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    competitor.setName(competitorNameField.getText());
                    try {
                        competitor.setDateOfBirth(dateStringConverter.asLocalDate(competitorDateField.getText()));
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    if (maleRadioButton.isSelected()) {
                        competitor.setGender(Gender.MALE);
                    } else {
                        competitor.setGender(Gender.FEMALE);
                    }
                    competitor.update();
                });
            });

            deleteCompetitorButton.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    competitor.delete();
                    JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.deletedText"));
                    EventSelector.getInstanceAndSetVisible();
                    setInvisibleAndClearAllFields();
                });
            });
        }
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

        newCompetitorCreateButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                clearAllFields();
                newCompetitorCreateButton.setVisible(false);
                deleteCompetitorButton.setVisible(false);
                updateCompetitorButton.removeActionListener(updateActionListener);
                updateActionListener = e12 -> {
                    try {
                        Gender selectedGender = Gender.NOT_RELEVANT;
                        if (maleRadioButton.isSelected()) {
                            selectedGender = Gender.MALE;
                        } else if (femaleRadioButton.isSelected()) {
                            selectedGender = Gender.FEMALE;
                        }
                        newCompetitor = new Competitor(1,
                                competitorNameField.getText(),
                                selectedGender,
                                dateStringConverter.asLocalDate(competitorDateField.getText()),
                                5); //TODO calculate Age based on Birthdate?
                        newCompetitor.setName(competitorNameField.getText());
                        newCompetitor.create();
                        JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.newCompCreatedText"));
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorOnCreateCompetitor"));
                    }
                };
                updateCompetitorButton.addActionListener(updateActionListener);
            });
        });

        if(!editExisting){
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);
            newCompetitorCreateButton.setVisible(false);
            deleteCompetitorButton.setVisible(false);
            newCompetitorCreateButton.addActionListener(e -> {
                clearAllFields();
                newCompetitorCreateButton.setVisible(false);
                deleteCompetitorButton.setVisible(false);

                updateActionListener = e12 -> {
                    try {
                        Gender selectedGender = Gender.NOT_RELEVANT;
                        if (maleRadioButton.isSelected()){
                            selectedGender = Gender.MALE;
                        } else if (femaleRadioButton.isSelected()){
                            selectedGender = Gender.FEMALE;
                        }
                        newCompetitor = new Competitor(1,
                                competitorNameField.getText(),
                                selectedGender,
                                dateStringConverter.asLocalDate(competitorDateField.getText()),
                                5); //TODO calculate Age based on Birthdate?
                        newCompetitor.setName(competitorNameField.getText());
                        newCompetitor.create();
                        JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.newCompCreatedText"));
                    }catch (Exception e1){
                        JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.errorOnCreateCompetitor"));
                    }
                };
                updateCompetitorButton.addActionListener(updateActionListener);
            });
        }

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
        }
    }

    private void clearAllFields() {
        SwingUtilities.invokeLater(() -> {
            descriptionLabel.setText(bundle.getString("CompetitorEditor.titleDescription"));
            competitorNameField.setText("");
            competitorDateField.setText("");
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);
        });
    }

    private void setInvisibleAndClearAllFields() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(false);
        });
        clearAllFields();
    }

}
