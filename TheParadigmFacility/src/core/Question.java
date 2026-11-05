package core;

import java.util.ArrayList;
import java.util.List;

// A multiple-choice question with one correct answer and three wrong ones. 
public class Question {
    private final int workday; // 1 to 7
    private final String type; // "theory" or "programming"
    private final String question;
    private final List<Answer> answers = new ArrayList<>();

    public Question(int workday, String type, String question,
            String correct, String wrong1, String wrong2, String wrong3) {
        this.workday = workday;
        this.type = type;
        this.question = question;
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

    // The four answers, correct one first. GameState shuffles them.
    public List<Answer> getAnswers() {
        return answers;
    }
}
