package core;

import java.util.ArrayList;

public class Question {
    private String question;
    private ArrayList<Answer> answers = new ArrayList<>();
    private int level;
    private int layout;
    public Question(String question, String a, String b, String c, String d, int level, int layout){
        this.question = question;
        answers.add(new Answer(a, true));
        answers.add(new Answer(b, false));
        answers.add(new Answer(c, false));
        answers.add(new Answer(d, false));
        this.level = level;
        this.layout = layout;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<Answer> getAnswer() {
        return answers;
    }

    public int getLevel() {
        return level;
    }

    public int getLayout() {
        return layout;
    }
}