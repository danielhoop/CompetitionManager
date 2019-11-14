package ch.ffhs.pa.competitionmanager.gui;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.dto.Competitor;
import ch.ffhs.pa.competitionmanager.enums.Gender;

import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ResourceBundle;

public class CompetitorEditor {
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
    private boolean createNew;
    private JFrame mainFrame;
    private ActionListener updateActionListener;
    private Competitor newCompetitor;

    GlobalState globalState = GlobalState.getInstance();
    ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

    private CompetitorEditor(JFrame mainFrame, boolean createNew) {
        this.createNew = createNew;
        this.mainFrame = mainFrame;
        createUIComponents();
    }

    private void createUIComponents (){

        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());
        DateStringConverter dateStringConverter = new DateStringConverter(GlobalState.getInstance().getLocale());

        if (!createNew){
            Competitor editedCompetitor = globalState.getCompetitor();
            descriptionLabel.setText(bundle.getString("CompetitorEditor.descEditCompetitor") + editedCompetitor.getName());
            competitorNameField.setText(editedCompetitor.getName());
            String competitorBirthDate = dateStringConverter.asString(editedCompetitor.getDateOfBirth());
            competitorDateField.setText(competitorBirthDate);
            if(editedCompetitor.getGender() == Gender.FEMALE) {
                femaleRadioButton.setSelected(true);
            } else if (editedCompetitor.getGender() == Gender.MALE){
                maleRadioButton.setSelected(true);
            }
            updateCompetitorButton.addActionListener(e -> {
                editedCompetitor.setName(competitorNameField.getText());
                try {
                    editedCompetitor.setDateOfBirth(dateStringConverter.asLocalDate(competitorDateField.getText()));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if(maleRadioButton.isSelected()){
                    editedCompetitor.setGender(Gender.MALE);
                } else {
                    editedCompetitor.setGender(Gender.FEMALE);
                }
                editedCompetitor.update();
            });
            updateActionListener = e -> {
                editedCompetitor.delete();
                JOptionPane.showMessageDialog(null, bundle.getString("CompetitorEditor.deletedText"));
                mainFrame.dispose();
                EventSelector.main();
            };

            deleteCompetitorButton.addActionListener(updateActionListener);
        }
        maleRadioButton.addActionListener(e -> {
            if (maleRadioButton.isSelected() == true){
                femaleRadioButton.setSelected(false);
            }
            maleRadioButton.setSelected(true);
        });

        femaleRadioButton.addActionListener(e -> {
            if (femaleRadioButton.isSelected() == true){
                maleRadioButton.setSelected(false);
            }
            femaleRadioButton.setSelected(true);
        });

        navigateToScoreEditor.addActionListener(e -> {
            ScoreEditor.main();
            mainFrame.dispose();
        });

        navigateToEventSelector.addActionListener(e -> {
           EventSelector.main();
           mainFrame.dispose();
        });

        newCompetitorCreateButton.addActionListener(e -> {
            competitorNameField.setText(null);
            competitorDateField.setText(null);
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);
            newCompetitorCreateButton.setVisible(false);
            deleteCompetitorButton.setVisible(false);
            updateCompetitorButton.removeActionListener(updateActionListener);
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

        if(createNew){
            maleRadioButton.setSelected(false);
            femaleRadioButton.setSelected(false);
            newCompetitorCreateButton.setVisible(false);
            deleteCompetitorButton.setVisible(false);
            newCompetitorCreateButton.addActionListener(e -> {
                competitorNameField.setText(null);
                competitorDateField.setText(null);
                maleRadioButton.setSelected(false);
                femaleRadioButton.setSelected(false);
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


    public static void main( boolean createNew) {
        GlobalState globalState = GlobalState.getInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("GuiText", globalState.getLocale());

        JFrame frame = new JFrame(bundle.getString("CompetitorEditor.title"));
        frame.setContentPane(new CompetitorEditor(frame, createNew).outerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
