package core;

public class Question {
    private String question;
    private Answer[] answer = new Answer[4];
    private int level;
    private int layout;
    public Question(String question, String a, String b, String c, String d, int level, int layout){
        this.question = question;
        this.answer[0] = new Answer(a, true);
        this.answer[1] = new Answer(b, false);
        this.answer[2] = new Answer(c, false);
        this.answer[3] = new Answer(d, false);
        this.level = level;
        this.layout = layout;
    }

    public String getQuestion() {
        return question;
    }

    public Answer[] getAnswer() {
        return answer;
    }

    public int getLevel() {
        return level;
    }

    public int getLayout() {
        return layout;
    }
}