package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One playthrough, from entering a name to Game Over or Victory. No UI code
 * here.
 *
 * Rules:
 * - Correct: full points, streak +1. At a streak of 5, one bug is removed and
 * the streak resets to 0.
 * - Wrong: half the points are deducted (score never below 0), +1 bug, streak
 * resets.
 * - Timeout: +1 bug, streak resets, no points lost.
 * - 3 bugs end the game immediately.
 * - A workday is cleared when all its questions are answered. A cleared workday
 * cannot be replayed.
 * - Only the score carries over to the next workday. Bugs and streak restart
 * each day.
 */
public class GameState {

    public static final int MAX_BUGS = 3;
    public static final int STREAK_FOR_REWARD = 5;

    /**
     * What happened to an answer. STREAK_REWARD is a correct answer that also hit
     * the streak of 5.
     */
    public enum Result {
        CORRECT, STREAK_REWARD, WRONG, TIMEOUT;

        public boolean isCorrect() {
            return this == CORRECT || this == STREAK_REWARD;
        }
    }

    private final String playerName;
    private final QuestionBank bank;

    private int score;
    private int bugs;
    private int streak;
    private int clearedDays;
    private final int[] correctByDay = new int[Workday.ALL.size()];

    private Workday day; // the workday being played, null before the first one starts
    private List<Question> questions;
    private List<Answer> answers = List.of(); // answers of the current question, shuffled once per question
    private int index; // position of the current question in the day's list
    private int correctToday;

    public GameState(String playerName, QuestionBank bank) {
        this.playerName = playerName;
        this.bank = bank;
    }

    // ---- starting a workday ----

    /**
     * The workday that can be started now (1 to 7), or 0 if none (game over or all
     * cleared).
     */
    public int nextDay() {
        if (isGameOver() || clearedDays >= Workday.ALL.size()) {
            return 0;
        }
        return clearedDays + 1;
    }

    /**
     * Starts a workday with all its questions in random order. Only nextDay() is
     * allowed.
     * Bugs and streak reset, the score stays.
     */
    public void startDay(int number) {
        if (number != nextDay()) {
            throw new IllegalStateException("Workday " + number + " cannot be started now");
        }
        if (day != null && !isDayCleared()) {
            throw new IllegalStateException("Workday " + number + " is already in progress");
        }
        List<Question> list = bank.getQuestions(number);
        if (list.isEmpty()) {
            throw new IllegalStateException("No questions found for workday " + number);
        }
        Collections.shuffle(list);

        day = Workday.get(number);
        questions = list;
        index = 0;
        correctToday = 0;
        bugs = 0;
        streak = 0;
        prepareAnswers();
    }

    // ---- answering ----

    /**
     * The question to answer now, or null if there is none (no day started, day
     * finished, game over).
     */
    public Question currentQuestion() {
        if (day == null || isGameOver() || index >= questions.size()) {
            return null;
        }
        return questions.get(index);
    }

    /**
     * The answers of the current question in a random order. The order stays the
     * same until the
     * question is answered. Empty if there is no current question.
     */
    public List<Answer> currentAnswers() {
        if (currentQuestion() == null) {
            return List.of();
        }
        return Collections.unmodifiableList(answers);
    }

    /** The player picked one of currentAnswers(). */
    public Result submitAnswer(Answer chosen) {
        requireQuestion();
        if (!answers.contains(chosen)) {
            throw new IllegalArgumentException("That answer does not belong to the current question");
        }
        return chosen.isCorrect() ? answeredCorrectly() : answeredWrong(Result.WRONG);
    }

    /** The time for the current question ran out. */
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

    /**
     * Moves to the next question. Answering the last one (without 3 bugs) clears
     * the workday.
     */
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
            answers = new ArrayList<>(questions.get(index).getAnswer());
            Collections.shuffle(answers);
        } else {
            answers = List.of();
        }
    }

    // ---- state ----

    /** Bugs reached 3. This is checked before the day is cleared. */
    public boolean isGameOver() {
        return bugs >= MAX_BUGS;
    }

    /** The current workday's questions are all answered. */
    public boolean isDayCleared() {
        return day != null && !isGameOver() && index >= questions.size();
    }

    /** Workday 7 is cleared. */
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

    /** The workday being played, or null before the first one starts. */
    public Workday day() {
        return day;
    }

    /** How many workdays are cleared (0 to 7). */
    public int clearedDays() {
        return clearedDays;
    }

    /**
     * Correct answers in a cleared workday (1 to 7), 0 if it is not cleared yet.
     */
    public int correctIn(int number) {
        return correctByDay[number - 1];
    }
}