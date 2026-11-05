package ui;

<<<<<<< HEAD
public class GamePanel {

}
=======
import core.Answer;
import core.GameState;
import core.Question;
import core.Workday;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.Timer;
import util.Assets;

/**
 * The main game screen: pause button, score, bugs, timer, question and 4
 * answers.
 * It shows the state of a GameState and sends the player's answers to it.
 * The workday must already be started (GameState.startDay) before this screen
 * is created.
 */
public class GamePanel extends JPanel {

    private enum Phase {
        ASKING, SHOWING_RESULT, DONE
    }

    private static final String PAUSE_BTN = "interface/pauseBtn.png";
    private static final String WHITE_BUG = "interface/whiteBug.png";
    private static final String RED_BUG = "interface/redBug.png";

    private static final int GLOW_RADIUS = 6; // how far the score's glow spreads
    private static final int RESULT_MS = 1000; // how long the result of an answer stays on screen

    // Positions are pixels of the 1280x720 window, measured from the mockup
    private static final int BAR_HEIGHT = 116;
    private static final int PAUSE_SIZE = 57;
    private static final int PAUSE_X = 50;
    private static final int PAUSE_Y = 28;
    private static final int SCORE_CENTER_X = 640;
    private static final int SCORE_BASELINE = 74;
    private static final int ANT_WIDTH = 59;
    private static final int ANT_STEP = 63;
    private static final int ANTS_X = 1039;
    private static final int ANTS_Y = 38;
    private static final int TIMER_RIGHT = 1233;
    private static final int TIMER_BASELINE = 175;
    private static final int LABEL_X = 78;
    private static final int LABEL_BASELINE = 206;
    private static final int QUESTION_X = 73;
    private static final int QUESTION_Y = 225;
    private static final int QUESTION_W = 1050;
    private static final int QUESTION_H = 171;
    private static final int GRID_X = 157;
    private static final int GRID_Y = 416;
    private static final int COL_GAP = 70;
    private static final int ROW_GAP = 17;

    private static final Color BAR_FILL = new Color(30, 90, 65, 102);
    private static final Color BAR_LINE = new Color(135, 143, 140);
    private static final Color SCORE_CORE = new Color(0xFFD969);
    private static final Color SCORE_GLOW = new Color(0xFFBF00);
    private static final Color TIMER_RED = new Color(0xBE0000);
    private static final Color LABEL_GRAY = new Color(0x808985);

    private static final Font SCORE_FONT = Theme.bold(38);
    private static final Font TIMER_FONT = Theme.bold(32);
    private static final Font LABEL_FONT = Theme.bold(16).deriveFont(Map.of(TextAttribute.TRACKING, 0.1f));

    private final GameState game;
    private final Runnable onPause;
    private final Runnable onDayEnd;

    private final ImageButton pauseButton;
    private final ChoiceButton[] choices = new ChoiceButton[4];
    private final CountdownTimer countdown;
    private final Timer resultTimer;
    private final String levelLabel;

    private Phase phase = Phase.DONE;
    private boolean paused;
    private double secondsLeft;
    private TextFit.Fit questionFit;
    private String glowText; // the score text the glow image was made for
    private BufferedImage glowImage;

    /**
     * @param game     a game with a workday already started
     * @param onPause  called when the pause button is clicked (the timer is already
     *                 paused)
     * @param onDayEnd called after the last answer's result was shown: the day is
     *                 cleared or the game is over
     */
    public GamePanel(GameState game, Runnable onPause, Runnable onDayEnd) {
        if (game.currentQuestion() == null) {
            throw new IllegalStateException("Start a workday before opening the game screen");
        }
        this.game = game;
        this.onPause = onPause;
        this.onDayEnd = onDayEnd;

        Workday day = game.day();
        this.levelLabel = "LEVEL " + day.number() + ": " + day.title().toUpperCase(Locale.ROOT);

        setOpaque(false);
        setLayout(null); // children are placed in doLayout()

        pauseButton = new ImageButton(PAUSE_BTN, PAUSE_SIZE, this::pauseClicked);
        add(pauseButton);
        for (int i = 0; i < choices.length; i++) {
            choices[i] = new ChoiceButton(this::onChoice);
            add(choices[i]);
        }

        countdown = new CountdownTimer(this::onTick, this::onTimeUp);
        resultTimer = new Timer(RESULT_MS, e -> nextQuestion());
        resultTimer.setRepeats(false);

        showQuestion();
    }

    // ---- public controls ----

    /**
     * Freezes the timer (and the result delay). The player cannot answer until
     * resume().
     */
    public void pause() {
        if (paused) {
            return;
        }
        paused = true;
        if (phase == Phase.ASKING) {
            countdown.pause();
        } else if (phase == Phase.SHOWING_RESULT) {
            resultTimer.stop();
        }
    }

    /** Continues after pause(). */
    public void resume() {
        if (!paused) {
            return;
        }
        paused = false;
        if (phase == Phase.ASKING) {
            countdown.resume();
        } else if (phase == Phase.SHOWING_RESULT) {
            resultTimer.restart();
        }
    }

    /** Stops all timers. Call it when the screen is left for good. */
    public void dispose() {
        countdown.stop();
        resultTimer.stop();
        phase = Phase.DONE;
    }

    // ---- game flow ----

    private void showQuestion() {
        Question question = game.currentQuestion();
        if (question.hasCode()) {
            System.err.println("GamePanel: this question has code, but the game screen has no code area yet: "
                    + question.getQuestion());
        }
        questionFit = TextFit.fit(this, question.getQuestion(), true, 34, 18, 1.65, QUESTION_W, QUESTION_H);

        List<Answer> answers = game.currentAnswers();
        for (int i = 0; i < choices.length; i++) {
            choices[i].show(answers.get(i));
        }

        phase = Phase.ASKING;
        countdown.start(game.day().timeLimit());
        repaint();
    }

    private void onChoice(ChoiceButton clicked) {
        if (phase != Phase.ASKING || paused) {
            return;
        }
        countdown.stop();
        game.submitAnswer(clicked.answer());
        showResult(clicked);
    }

    private void onTimeUp() {
        if (phase != Phase.ASKING) {
            return;
        }
        game.timeOut();
        showResult(null);
    }

    /**
     * Correct answer turns green, the player's wrong pick turns red. Then the next
     * question after RESULT_MS.
     */
    private void showResult(ChoiceButton picked) {
        for (ChoiceButton button : choices) {
            if (button.answer().isCorrect()) {
                button.mark(ChoiceButton.State.CORRECT);
            } else if (button == picked) {
                button.mark(ChoiceButton.State.WRONG);
            }
            button.lock();
        }
        phase = Phase.SHOWING_RESULT;
        resultTimer.start();
        repaint();
    }

    private void nextQuestion() {
        if (game.isGameOver() || game.isDayCleared()) {
            phase = Phase.DONE;
            onDayEnd.run();
        } else {
            showQuestion();
        }
    }

    private void pauseClicked() {
        if (phase == Phase.DONE) {
            return;
        }
        pause();
        onPause.run();
    }

    private void onTick(double seconds) {
        secondsLeft = seconds;
        repaint(TIMER_RIGHT - 180, TIMER_BASELINE - 44, 200, 60);
    }

    // ---- layout and painting ----

    @Override
    public void doLayout() {
        Dimension p = pauseButton.getPreferredSize();
        // the button image sits 10 px from the left and 6 px from the top of the
        // component
        pauseButton.setBounds(PAUSE_X - 10, PAUSE_Y - 6, p.width, p.height);

        for (int i = 0; i < choices.length; i++) {
            int x = GRID_X + (i % 2) * (ChoiceButton.WIDTH + COL_GAP);
            int y = GRID_Y + (i / 2) * (ChoiceButton.HEIGHT + ROW_GAP);
            choices[i].setBounds(x, y, ChoiceButton.WIDTH, ChoiceButton.HEIGHT);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // top bar
        g2.setColor(BAR_FILL);
        g2.fillRect(0, 0, getWidth(), BAR_HEIGHT);
        g2.setColor(BAR_LINE);
        g2.fillRect(0, BAR_HEIGHT - 2, getWidth(), 2);

        drawScore(g2);
        drawBugs(g2);

        // timer, top right under the bar
        g2.setFont(TIMER_FONT);
        g2.setColor(TIMER_RED);
        String time = String.format(Locale.US, "%.2f s", secondsLeft);
        g2.drawString(time, TIMER_RIGHT - g2.getFontMetrics().stringWidth(time), TIMER_BASELINE);

        // level label and question
        g2.setFont(LABEL_FONT);
        g2.setColor(LABEL_GRAY);
        g2.drawString(levelLabel, LABEL_X, LABEL_BASELINE);
        TextFit.draw(g2, questionFit, Color.WHITE, QUESTION_X, QUESTION_Y, QUESTION_W, QUESTION_H, false);

        g2.dispose();
    }

    /** "10 PTS" in the middle of the bar, with a glow. */
    private void drawScore(Graphics2D g2) {
        String text = String.format(Locale.US, "%,d", game.score()).replace(',', ' ') + " PTS";
        if (!text.equals(glowText)) {
            glowImage = makeGlow(text);
            glowText = text;
        }

        g2.setFont(SCORE_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int x = SCORE_CENTER_X - fm.stringWidth(text) / 2;
        int pad = GLOW_RADIUS * 2;
        g2.drawImage(glowImage, x - pad, SCORE_BASELINE - fm.getAscent() - pad, null);
        g2.setColor(SCORE_CORE);
        g2.drawString(text, x, SCORE_BASELINE);
    }

    /**
     * The text in the glow color, blurred and brightened. It is made once per score
     * and reused.
     */
    private BufferedImage makeGlow(String text) {
        FontMetrics fm = getFontMetrics(SCORE_FONT);
        int pad = GLOW_RADIUS * 2;
        BufferedImage image = new BufferedImage(fm.stringWidth(text) + 2 * pad, fm.getHeight() + 2 * pad,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();
        // start with invisible pixels that already have the glow color, so blurring
        // never darkens it
        g.setComposite(AlphaComposite.Src);
        g.setColor(new Color(SCORE_GLOW.getRed(), SCORE_GLOW.getGreen(), SCORE_GLOW.getBlue(), 0));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(SCORE_FONT);
        g.setColor(SCORE_GLOW);
        g.drawString(text, pad, pad + fm.getAscent());
        g.dispose();

        int size = 2 * GLOW_RADIUS + 1;
        float[] weights = new float[size * size];
        Arrays.fill(weights, 1f / weights.length);
        ConvolveOp blur = new ConvolveOp(new Kernel(size, size, weights), ConvolveOp.EDGE_NO_OP, null);
        image = blur.filter(blur.filter(image, null), null);

        // the blur makes the text faint, so the opacity is raised again
        return new RescaleOp(new float[] { 1f, 1f, 1f, 3f }, new float[4], null).filter(image, null);
    }

    /** 3 ants. Each bug turns one ant red, starting from the right. */
    private void drawBugs(Graphics2D g2) {
        BufferedImage white = Assets.scaled(WHITE_BUG, ANT_WIDTH);
        BufferedImage red = Assets.scaled(RED_BUG, ANT_WIDTH);
        for (int i = 0; i < GameState.MAX_BUGS; i++) {
            BufferedImage ant = i >= GameState.MAX_BUGS - game.bugs() ? red : white;
            if (ant != null) {
                g2.drawImage(ant, ANTS_X + i * ANT_STEP, ANTS_Y, null);
            }
        }
    }
}
>>>>>>> b2dcba9edb52465a74fbcae6e64fecf9d962717e
