package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GameState {

    public static final int MAX_BUGS = 3;
    public static final int STREAK_FOR_REWARD = 5;

    // The question order of the most recent game, per workday. Static because
    // every new playthrough (and every retry) creates or reuses a GameState.
    private static final Map<Integer, List<Question>> LAST_ORDER = new HashMap<>();

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
    private int lastCorrectIndex = -1; // position of the correct answer on the previous question

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
        // a copy, so the bank's own list stays untouched
        List<Question> list = new ArrayList<>(bank.getQuestions(number));
        if (list.isEmpty()) {
            throw new IllegalStateException("No questions found for workday " + number);
        }
        // no question in the same position as in the previous game of this workday
        shuffleAwayFrom(list, LAST_ORDER.get(number));
        LAST_ORDER.put(number, new ArrayList<>(list));

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
        lastCorrectIndex = -1;
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
            avoidRepeatedPosition();
        } else {
            answers = List.of();
        }
    }

    // The correct answer never appears in the same position as on the previous
    // question. If the shuffle repeats the position, the correct answer swaps
    // places with a randomly chosen other answer.
    private void avoidRepeatedPosition() {
        int pos = -1;
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).isCorrect()) {
                pos = i;
                break;
            }
        }
        if (pos < 0) {
            return; // no correct answer found, nothing to arrange
        }

        if (pos == lastCorrectIndex && answers.size() > 1) {
            int other = ThreadLocalRandom.current().nextInt(answers.size() - 1);
            if (other >= pos) {
                other++; // skip the current position
            }
            Collections.swap(answers, pos, other);
            pos = other;
        }
        lastCorrectIndex = pos;
    }

    // Shuffles the list so that no question is in the same position as in the
    // previous order. Reshuffling works almost every time (about 1 shuffle in 3
    // qualifies). If it somehow never does, the previous order is rotated by a
    // random amount, which always moves every question.
    private static void shuffleAwayFrom(List<Question> list, List<Question> previous) {
        Collections.shuffle(list);
        int n = list.size();
        if (previous == null || previous.size() != n || n < 2) {
            return; // first game of this workday, or the question file changed
        }

        for (int attempt = 0; attempt < 200 && samePlace(list, previous); attempt++) {
            Collections.shuffle(list);
        }

        if (samePlace(list, previous)) {
            int shift = 1 + ThreadLocalRandom.current().nextInt(n - 1);
            for (int i = 0; i < n; i++) {
                list.set(i, previous.get((i + shift) % n));
            }
        }
    }

    private static boolean samePlace(List<Question> a, List<Question> b) {
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i) == b.get(i)) {
                return true;
            }
        }
        return false;
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