package ui;

import core.QuestionBank;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.Random;

public class GamePanel extends JPanel {
    public GamePanel(){
        QuestionBank questionBank = new QuestionBank();
        Random random = new Random();

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel questionLabel = new JLabel();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(questionLabel,c);
        c.gridwidth = 1;
        c.gridy = 1;

        JButton answerA = new JButton();
        add(answerA,c);
        c.gridx = 1;

        JButton answerB = new JButton();
        add(answerB,c);
        c.gridy = 2;
        c.gridx = 0;

        JButton answerC = new JButton();
        add(answerC,c);
        c.gridx = 1;

        JButton answerD = new JButton();
        add(answerD,c);
    }
}
