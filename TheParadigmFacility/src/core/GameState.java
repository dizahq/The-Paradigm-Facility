package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameState {

    public static final int MAX_BUGS = 3;
    public static final int STREAK_FOR_REWARD = 5;

    public enum Result {
        CORRECT, STREAK_REWARD, WRONG, TIMEOUT
    }

    private final String playerName;
    private final QuestionBank bank;
    private final int[] correctByDay = new int[Workday.ALL.size()];

    private int score;
    private int scoreAtDayStart;
    private int bugs;
    private int streak;
    private int clearedDays;

    private Workday day; // null before the first workday starts
    private List<Question> questions = List.of();
    private List<Answer> answers = List.of(); // shuffled once per question
    private int index; // position of the current question
    private int correctToday;

    public GameState(String playerName, QuestionBank bank) {
        this.playerName = playerName;
        this.bank = bank;
    }

    // Starting a workday.

    // The workday that can be started now (1 to 7), or 0 when all are cleared.
    public int nextDay() {
        return clearedDays >= Workday.ALL.size() ? 0 : clearedDays + 1;
    }

    public void startDay(int number) {
        if (number != nextDay()) {
            throw new IllegalStateException("Workday " + number + " cannot be started now");
        }
        if (day != null && !isGameOver() && !isDayCleared()) {
            throw new IllegalStateException("Workday " + number + " is already in progress");
        }
        List<Question> list = bank.getQuestions(number);
        if (list.isEmpty()) {
            throw new IllegalStateException("No questions found for workday " + number);
        }
        Collections.shuffle(list);

        if (isGameOver()) {
            score = scoreAtDayStart;
        }
        scoreAtDayStart = score;
        day = Workday.get(number);
        questions = list;
        index = 0;
        correctToday = 0;
        bugs = 0;
        streak = 0;
        prepareAnswers();
    }

    // Answering

    // The question to answer now, or null if there is none.
    public Question currentQuestion() {
        if (day == null || isGameOver() || index >= questions.size()) {
            return null;
        }
        return questions.get(index);
    }

    // The answers of the current question in random order, or an empty list.
    public List<Answer> currentAnswers() {
        if (currentQuestion() == null) {
            return List.of();
        }
        return Collections.unmodifiableList(answers);
    }

    // The player picked one of currentAnswers().
    public Result submitAnswer(Answer chosen) {
        requireQuestion();
        if (!answers.contains(chosen)) {
            throw new IllegalArgumentException("That answer does not belong to the current question");
        }
        return chosen.isCorrect() ? answeredCorrectly() : answeredWrong(Result.WRONG);
    }

    // The time for the current question ran out.
    public Result timeOut() {
        requireQuestion();
        return answeredWrong(Result.TIMEOUT);
    }

    private void requireQuestion() {
        if (currentQuestion() == null) {
            throw new IllegalStateException("There is no question to answer");
        }
    }

    private Result answeredCorrectly() {
        score += day.points();
        correctToday++;
        streak++;

        Result result = Result.CORRECT;
        if (streak == STREAK_FOR_REWARD) {
            bugs = Math.max(0, bugs - 1);
            streak = 0;
            result = Result.STREAK_REWARD;
        }
        return next(result);
    }

    private Result answeredWrong(Result result) {
        if (result == Result.WRONG) {
            score = Math.max(0, score - day.points() / 2);
        }
        bugs++;
        streak = 0;
        return next(result);
    }

    // Answering the last question without reaching 3 bugs clears the workday.
    private Result next(Result result) {
        index++;
        if (!isGameOver() && index >= questions.size()) {
            clearedDays++;
            correctByDay[day.number() - 1] = correctToday;
        }
        prepareAnswers();
        return result;
    }

    private void prepareAnswers() {
        if (index < questions.size()) {
            answers = new ArrayList<>(questions.get(index).getAnswers());
            Collections.shuffle(answers);
        } else {
            answers = List.of();
        }
    }

    // State

    // Bugs reached 3.
    public boolean isGameOver() {
        return bugs >= MAX_BUGS;
    }

    // All questions of the current workday are answered.
    public boolean isDayCleared() {
        return day != null && !isGameOver() && index >= questions.size();
    }

    public boolean isVictory() {
        return isDayCleared() && day.number() == Workday.ALL.size();
    }

    public String playerName() {
        return playerName;
    }

    public int score() {
        return score;
    }

    public int bugs() {
        return bugs;
    }

    public int streak() {
        return streak;
    }

    public Workday day() {
        return day;
    }

    public int clearedDays() {
        return clearedDays;
    }

    // Correct answers in a cleared workday (1 to 7), 0 if it is not cleared yet.
    public int correctIn(int number) {
        return correctByDay[number - 1];
    }
}
