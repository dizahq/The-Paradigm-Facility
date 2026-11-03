package ui;

import core.Answer;
import core.AnswerButton;
import core.Question;
import core.QuestionBank;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    private final ArrayList<Question> remaining;
    private final Random random = new Random();
    private final JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
    private final AnswerButton[] answerButtons = {
            new AnswerButton(), new AnswerButton(), new AnswerButton(), new AnswerButton() };
    private final Timer nextQuestionTimer;
    private final Runnable onFinish;
    private boolean locked; // true while the result of an answer is showing

    // onFinish runs when every question has been answered
    public GamePanel(Runnable onFinish) {
        this.onFinish = onFinish;
        setLayout(new GridBagLayout());
        buildLayout();

        remaining = new ArrayList<>(new QuestionBank().getQuestions()); // copy, so the bank stays intact

        nextQuestionTimer = new Timer(1000, e -> showNextQuestion());
        nextQuestionTimer.setRepeats(false);

        for (AnswerButton button : answerButtons) {
            button.addActionListener(e -> onAnswer(button));
        }

        if (remaining.isEmpty()) {
            questionLabel.setText("No questions found (check data/SampleQ.csv)");
            return;
        }
        showNextQuestion();
    }

    private void buildLayout() {
        GridBagConstraints c = new GridBagConstraints();

        questionLabel.setPreferredSize(new Dimension(600, 100));
        questionLabel.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(questionLabel, c);

        c.gridwidth = 1;
        for (int i = 0; i < answerButtons.length; i++) {
            c.gridx = i % 2;
            c.gridy = 1 + i / 2;
            answerButtons[i].setPreferredSize(new Dimension(300, 200));
            add(answerButtons[i], c);
        }
    }

    private void showNextQuestion() {
        if (remaining.isEmpty()) {
            onFinish.run(); // level clear
            return;
        }
        locked = false;
        for (AnswerButton button : answerButtons) {
            button.setBackground(null);
        }

        Question question = remaining.remove(random.nextInt(remaining.size()));
        ArrayList<Answer> answers = new ArrayList<>(question.getAnswer());
        Collections.shuffle(answers);

        questionLabel.setText(question.getQuestion());
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setAnswer(answers.get(i));
        }
    }

    private void onAnswer(AnswerButton clicked) {
        if (locked) {
            return; // ignore clicks while the result is showing
        }
        locked = true;

        if (clicked.getAnswer().isCorrect()) {
            clicked.setBackground(Color.GREEN);
        } else {
            clicked.setBackground(Color.RED);
            for (AnswerButton button : answerButtons) { // also show the right one
                if (button.getAnswer().isCorrect()) {
                    button.setBackground(Color.GREEN);
                }
            }
        }
        nextQuestionTimer.start();
    }
}