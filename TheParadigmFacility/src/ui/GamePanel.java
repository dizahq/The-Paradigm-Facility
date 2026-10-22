package ui;

import core.Answer;
import core.AnswerButton;
import core.Question;
import core.QuestionBank;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GamePanel extends JPanel {
    GridBagConstraints c = new GridBagConstraints();
    QuestionBank questionBank = new QuestionBank();
    ArrayList<Question> selectedQuestions;
    Random random = new Random();
    JLabel questionLabel = new JLabel();
    AnswerButton answerA = new AnswerButton();
    AnswerButton answerB = new AnswerButton();
    AnswerButton answerC = new AnswerButton();
    AnswerButton answerD = new AnswerButton();
    AnswerButton[] answerButtons = {answerA, answerB, answerC, answerD};
    Timer nextQuestionTimer = new Timer(1000, _ -> getRandomQuestion());
    public GamePanel(){
        setLayout(new GridBagLayout());

        startGame();

        for(AnswerButton button : answerButtons){
            button.addActionListener(_ -> {
                if(selectedQuestions.isEmpty()){
                    // level clear
                    System.exit(0); // for now
                }else {
                    if(button.getAnswer().isCorrect()){
                        button.setBackground(Color.GREEN);
                    }else button.setBackground(Color.RED);

                    nextQuestionTimer.start();
                }
            });
        }
    }
    public void startGame(){
        System.out.println("Starting game...");
        selectedQuestions = questionBank.getQuestions();

        getRandomQuestion();
    }
    public void getRandomQuestion(){
        resetStates();

        int listSize = selectedQuestions.size();
        int randomIndex = random.nextInt(listSize);
        Question currentQuestion = selectedQuestions.get(randomIndex);

        displayQuestion(currentQuestion);
        selectedQuestions.remove(randomIndex);
    }
    public void resetStates(){
        nextQuestionTimer.stop();
        for(AnswerButton buttons : answerButtons){
            buttons.setBackground(null);
        }
    }
    public void displayQuestion(Question question){
        ArrayList<Answer> answers = question.getAnswer();
        Collections.shuffle(answers);

        questionLabel.setText(question.getQuestion());
        questionLabel.setPreferredSize(new Dimension(600, 100));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        add(questionLabel,c);
        c.gridwidth = 1; c.gridy = 1;

        answerA.setAnswer(answers.getFirst());
        answerA.setPreferredSize(new Dimension(300, 200));
        add(answerA,c);
        c.gridx = 1;

        answerB.setAnswer(answers.get(1));
        answerB.setPreferredSize(new Dimension(300, 200));
        add(answerB,c);
        c.gridy = 2; c.gridx = 0;

        answerC.setAnswer(answers.get(2));
        answerC.setPreferredSize(new Dimension(300, 200));
        add(answerC,c);
        c.gridx = 1;

        answerD.setAnswer(answers.get(3));
        answerD.setPreferredSize(new Dimension(300, 200));
        add(answerD,c);
    }
}
