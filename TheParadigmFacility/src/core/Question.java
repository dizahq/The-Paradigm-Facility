<<<<<<< HEAD
=======
package core;

import java.util.ArrayList;

public class Question {
    private final int workday; // 1 to 7
    private final String type; // "theory" or "programming"
    private final String question;
    private final String code; // empty when the question has no code snippet
    private final ArrayList<Answer> answers = new ArrayList<>();

    // The first answer given is the correct one. GamePanel shuffles the order for
    // display.
    public Question(int workday, String type, String question, String code,
            String correct, String wrong1, String wrong2, String wrong3) {
        this.workday = workday;
        this.type = type;
        this.question = question;
        this.code = code;
        answers.add(new Answer(correct, true));
        answers.add(new Answer(wrong1, false));
        answers.add(new Answer(wrong2, false));
        answers.add(new Answer(wrong3, false));
    }

    public int getWorkday() {
        return workday;
    }

    public String getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public String getCode() {
        return code;
    }

    public boolean hasCode() {
        return !code.isEmpty();
    }

    public ArrayList<Answer> getAnswer() {
        return answers;
    }
}
>>>>>>> b2dcba9edb52465a74fbcae6e64fecf9d962717e
