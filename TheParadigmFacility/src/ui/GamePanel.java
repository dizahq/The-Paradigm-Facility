package ui;

import audio.SoundManager;
import core.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
import ui.QuestionContent.Kind;
import util.Assets;

/**
 * The main game screen: pause button, score, bugs, timer, question and four
 * answers.
 * It shows a GameState and sends the player's answers to it. The workday must
 * already be started before this screen is created.
 *
 * The layout is picked per question from its content (see QuestionContent):
 * SIMPLE - question on top, 2x2 grid of answers.
 * CODE_QUESTION - prompt, code panel on the left, answers stacked on the right.
 * CODE_CHOICES - prompt and expected output on top, 2x2 grid of code boxes.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel {

    private enum Phase {
        ASKING, SHOWING_RESULT, DONE
    }

    private static final String PAUSE_BTN = "interface/pausebtn.png";
    private static final String WHITE_BUG = "interface/whiteBug.png";
    private static final String RED_BUG = "interface/redBug.png";

    private static final int GLOW_RADIUS = 6;
    private static final int RESULT_MS = 1000; // how long the result of an answer stays on screen
    private static final int CODE_EXTRA_SECONDS = 30; // extra reading time for questions with code

    // Positions are in pixels of the 1280x720 window
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

    // Code layouts: everything below the prompt line
    private static final int CONTENT_X = QUESTION_X;
    private static final int CONTENT_W = 1134;
    private static final int PROMPT_H = 56; // one or two lines
    private static final int BODY_Y = 291; // code panel / answer column start (CODE_QUESTION)
    private static final int BODY_BOTTOM = 696;
    private static final int BODY_GAP = 24; // between the code panel and the answers
    private static final int STACK_GAP = 14; // between stacked answers
    private static final int ANSWER_COL_NARROW = 340; // case A: outputs are short
    private static final int ANSWER_COL_WIDE = 440; // case C: statements, or text answers
    private static final int PANEL_PAD_X = 16;
    private static final int PANEL_PAD_Y = 12;
    private static final int OUTPUT_W = 370; // expected-output chip (CODE_CHOICES)
    private static final int CODE_GRID_Y = 297;
    private static final int CODE_GRID_GAP = 16;

    // Font sizes in pixels: the biggest size that fits is used, never below the
    // minimum
    private static final int CODE_MAX = 22;
    private static final int CODE_MIN = 11;
    private static final int CHOICE_CODE_MAX = 20;
    private static final int CHOICE_CODE_MIN = 9;

    private static final Color BAR_FILL = new Color(30, 90, 65, 102);
    private static final Color BAR_LINE = new Color(135, 143, 140);
    private static final Color PANEL_FILL = new Color(8, 20, 16, 150);
    private static final Color REVEAL_GREEN = new Color(0x6FE3A0);
    private static final Color SCORE_CORE = new Color(0xFFD969);
    private static final Color SCORE_GLOW = new Color(0xFFBF00);
    private static final Color TIMER_RED = new Color(0xBE0000);
    private static final Color LABEL_GRAY = new Color(0x808985);

    private static final Font SCORE_FONT = Theme.bold(38);
    private static final Font TIMER_FONT = Theme.bold(32);
    private static final Font LABEL_FONT = Theme.bold(16).deriveFont(Map.of(TextAttribute.TRACKING, 0.1f));
    private static final Font CHIP_LABEL_FONT = LABEL_FONT.deriveFont(12f);

    private final GameState game;
    private final Runnable onPause;
    private final Runnable onDayEnd;

    private final ImageButton pauseButton;
    private final ChoiceButton[] choices = new ChoiceButton[4];
    private final CodeChoiceButton[] codeChoices = new CodeChoiceButton[4];
    private final CountdownTimer countdown;
    private final Timer resultTimer;
    private final String levelLabel;

    private Phase phase = Phase.DONE;
    private boolean paused;
    private double secondsLeft;
    private QuestionContent content;
    private TextFit.Fit questionFit; // SIMPLE: the whole question
    private TextFit.Fit promptFit; // code layouts: the first line
    private final Rectangle promptBox = new Rectangle();
    private final Rectangle codePanel = new Rectangle(); // CODE_QUESTION
    private final Rectangle outputChip = new Rectangle(); // CODE_CHOICES, empty without expected output
    private CodeBlock codeBlock;
    private String correctText; // what fills the blank once the result is shown
    private boolean revealed;
    private String glowText; // the score text the glow image was made for
    private BufferedImage glowImage;

    // game a game with a workday already started
    // onPause called when the pause button is clicked (the timer is already paused)
    // onDayEnd called after the result of the last answer was shown
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
            choices[i] = new ChoiceButton(clicked -> onAnswerPicked(clicked.answer()));
            add(choices[i]);
            codeChoices[i] = new CodeChoiceButton(clicked -> onAnswerPicked(clicked.answer()));
            add(codeChoices[i]);
        }

        countdown = new CountdownTimer(this::onTick, this::onTimeUp);
        resultTimer = new Timer(RESULT_MS, e -> nextQuestion());
        resultTimer.setRepeats(false);

        showQuestion();
    }

    // Freezes the timer and the result delay. The player cannot answer until
    // resume().
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

    // Continues after pause().
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

    // Stops all timers. Call this when the screen is left for good.
    public void dispose() {
        countdown.stop();
        resultTimer.stop();
        phase = Phase.DONE;
    }

    private void showQuestion() {
        Question question = game.currentQuestion();
        List<Answer> answers = game.currentAnswers();
        List<String> texts = answers.stream().map(GamePanel::textOf).toList();

        content = QuestionContent.of(question.getQuestion(), texts);
        revealed = false;
        correctText = answers.stream().filter(Answer::isCorrect).map(a -> QuestionContent.normalize(textOf(a)))
                .findFirst().orElse(null);

        boolean simple = content.kind() == Kind.SIMPLE;
        for (int i = 0; i < choices.length; i++) {
            choices[i].setVisible(simple);
            codeChoices[i].setVisible(!simple);
        }

        if (simple) {
            questionFit = TextFit.fit(this, question.getQuestion(), true, 34, 18, 1.65, QUESTION_W, QUESTION_H);
            for (int i = 0; i < choices.length; i++) {
                choices[i].show(answers.get(i));
            }
        } else {
            showCodeQuestion(answers, texts);
        }

        phase = Phase.ASKING;
        int seconds = game.day().timeLimit();
        countdown.start(simple ? seconds : seconds + CODE_EXTRA_SECONDS);

        repaint();
    }

    private void showCodeQuestion(List<Answer> answers, List<String> texts) {
        boolean codeInQuestion = content.kind() == Kind.CODE_QUESTION;
        layoutAnswers(); // the boxes need their final size before the fonts are fitted

        promptFit = TextFit.fit(this, content.prompt(), true, 26, 16, 1.4, promptBox.width, promptBox.height);
        for (int i = 0; i < codeChoices.length; i++) {
            // short answers may wrap, code answers keep their lines
            codeChoices[i].show(answers.get(i), QuestionContent.normalize(texts.get(i)), content.monoAnswers(),
                    codeInQuestion);
        }

        // one font size for all four answers
        int max = codeInQuestion ? CODE_MAX : CHOICE_CODE_MAX;
        int min = codeInQuestion ? CODE_MIN : CHOICE_CODE_MIN;
        int size = max;
        for (CodeChoiceButton button : codeChoices) {
            size = Math.min(size, button.bestSize(max, min));
        }
        for (CodeChoiceButton button : codeChoices) {
            button.setFontSize(size);
        }

        codeBlock = null;
        if (codeInQuestion) {
            codeBlock = CodeBlock.fit(this, CodeFont::get, content.code(), content.hasBlank() ? correctText : null,
                    CODE_MAX, CODE_MIN, codePanel.width - 2 * PANEL_PAD_X, codePanel.height - 2 * PANEL_PAD_Y);
        }
    }

    private void onAnswerPicked(Answer picked) {
        if (phase != Phase.ASKING || paused) {
            return;
        }
        countdown.stop();
        playResultSound(game.submitAnswer(picked));
        showResult(picked);
    }

    private void onTimeUp() {
        if (phase != Phase.ASKING) {
            return;
        }
        playResultSound(game.timeOut());
        showResult(null);
    }

    private void playResultSound(GameState.Result result) {
        SoundManager.play(switch (result) {
            case CORRECT -> SoundManager.Sfx.CORRECT;
            case STREAK_REWARD -> SoundManager.Sfx.STREAK;
            case WRONG, TIMEOUT -> SoundManager.Sfx.WRONG;
        });
    }

    /*
     * Shows the result: the correct answer turns green and a wrong pick turns red
     * (picked is null on a timeout). A blank in the code is filled in. The next
     * question follows after RESULT_MS.
     */
    private void showResult(Answer picked) {
        if (content.kind() == Kind.SIMPLE) {
            for (ChoiceButton button : choices) {
                if (button.answer().isCorrect()) {
                    button.mark(ChoiceButton.State.CORRECT);
                } else if (button.answer() == picked) {
                    button.mark(ChoiceButton.State.WRONG);
                }
                button.lock();
            }
        } else {
            for (CodeChoiceButton button : codeChoices) {
                if (button.answer().isCorrect()) {
                    button.mark(CodeChoiceButton.State.CORRECT);
                } else if (button.answer() == picked) {
                    button.mark(CodeChoiceButton.State.WRONG);
                }
                button.lock();
            }
        }
        revealed = true;
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

    private static String textOf(Answer answer) {
        return answer.getAnswer();
    }

    @Override
    public void doLayout() {
        Dimension p = pauseButton.getPreferredSize();
        pauseButton.setBounds(PAUSE_X - 10, PAUSE_Y - 6, p.width, p.height);
        layoutAnswers();
    }

    // Places the answer buttons and the code panel for the current question.
    private void layoutAnswers() {
        if (content == null) {
            return;
        }
        switch (content.kind()) {
            case SIMPLE -> {
                for (int i = 0; i < choices.length; i++) {
                    int x = GRID_X + (i % 2) * (ChoiceButton.WIDTH + COL_GAP);
                    int y = GRID_Y + (i / 2) * (ChoiceButton.HEIGHT + ROW_GAP);
                    choices[i].setBounds(x, y, ChoiceButton.WIDTH, ChoiceButton.HEIGHT);
                }
            }
            case CODE_QUESTION -> {
                // code on the left, answers in a column on the right
                boolean wide = !content.monoAnswers() || content.hasBlank();
                int columnW = wide ? ANSWER_COL_WIDE : ANSWER_COL_NARROW;
                int bodyH = BODY_BOTTOM - BODY_Y;
                int panelW = CONTENT_W - BODY_GAP - columnW;
                promptBox.setBounds(CONTENT_X, QUESTION_Y, CONTENT_W, PROMPT_H);
                codePanel.setBounds(CONTENT_X, BODY_Y, panelW, bodyH);
                outputChip.setBounds(0, 0, 0, 0);
                int h = (bodyH - 3 * STACK_GAP) / 4;
                for (int i = 0; i < codeChoices.length; i++) {
                    codeChoices[i].setBounds(CONTENT_X + panelW + BODY_GAP, BODY_Y + i * (h + STACK_GAP), columnW, h);
                }
            }
            case CODE_CHOICES -> {
                // prompt with the expected output next to it, then a 2x2 grid of code boxes
                boolean hasOutput = content.expectedOutput() != null && !content.expectedOutput().isEmpty();
                int promptW = hasOutput ? CONTENT_W - OUTPUT_W - BODY_GAP : CONTENT_W;
                promptBox.setBounds(CONTENT_X, QUESTION_Y, promptW, PROMPT_H);
                if (hasOutput) {
                    outputChip.setBounds(CONTENT_X + CONTENT_W - OUTPUT_W, QUESTION_Y, OUTPUT_W, PROMPT_H);
                } else {
                    outputChip.setBounds(0, 0, 0, 0);
                }
                codePanel.setBounds(0, 0, 0, 0);
                int w = (CONTENT_W - CODE_GRID_GAP) / 2;
                int h = (BODY_BOTTOM - CODE_GRID_Y - CODE_GRID_GAP) / 2;
                for (int i = 0; i < codeChoices.length; i++) {
                    int x = CONTENT_X + (i % 2) * (w + CODE_GRID_GAP);
                    int y = CODE_GRID_Y + (i / 2) * (h + CODE_GRID_GAP);
                    codeChoices[i].setBounds(x, y, w, h);
                }
            }
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

        // timer, top right below the bar
        g2.setFont(TIMER_FONT);
        g2.setColor(TIMER_RED);
        String time = String.format(Locale.US, "%.2f s", secondsLeft);
        g2.drawString(time, TIMER_RIGHT - g2.getFontMetrics().stringWidth(time), TIMER_BASELINE);

        // level label and question
        g2.setFont(LABEL_FONT);
        g2.setColor(LABEL_GRAY);
        g2.drawString(levelLabel, LABEL_X, LABEL_BASELINE);
        switch (content.kind()) {
            case SIMPLE -> TextFit.draw(g2, questionFit, Color.WHITE, QUESTION_X, QUESTION_Y, QUESTION_W, QUESTION_H,
                    false);
            case CODE_QUESTION -> {
                drawPrompt(g2);
                drawCodePanel(g2);
            }
            case CODE_CHOICES -> {
                drawPrompt(g2);
                drawOutputChip(g2);
            }
        }

        g2.dispose();
    }

    private void drawPrompt(Graphics2D g2) {
        TextFit.draw(g2, promptFit, Color.WHITE, promptBox.x, promptBox.y, promptBox.width, promptBox.height, false);
    }

    /**
     * The code in a dark rounded panel. A blank shows the correct answer once the
     * result is on screen.
     */
    private void drawCodePanel(Graphics2D g2) {
        g2.setColor(PANEL_FILL);
        g2.fillRoundRect(codePanel.x, codePanel.y, codePanel.width, codePanel.height, 18, 18);
        g2.setColor(BAR_LINE);
        g2.drawRoundRect(codePanel.x, codePanel.y, codePanel.width - 1, codePanel.height - 1, 18, 18);
        if (codeBlock != null) {
            codeBlock.paint(g2, codePanel.x + PANEL_PAD_X, codePanel.y + PANEL_PAD_Y, CodeBlock.TEXT,
                    revealed ? correctText : null, REVEAL_GREEN);
        }
    }

    // The "OUTPUT" label and the expected output in monospace, top right.
    private void drawOutputChip(Graphics2D g2) {
        if (outputChip.isEmpty()) {
            return;
        }
        Rectangle r = outputChip;
        g2.setColor(PANEL_FILL);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g2.setColor(BAR_LINE);
        g2.drawRoundRect(r.x, r.y, r.width - 1, r.height - 1, 14, 14);

        g2.setFont(CHIP_LABEL_FONT);
        g2.setColor(LABEL_GRAY);
        g2.drawString("OUTPUT", r.x + 14, r.y + 19);

        String output = content.expectedOutput();
        Font font = CodeFont.get(22);
        FontMetrics fm = getFontMetrics(font);
        for (int size = 22; size > 11 && fm.stringWidth(output) > r.width - 28; size--) {
            font = CodeFont.get(size);
            fm = getFontMetrics(font);
        }
        g2.setFont(font);
        g2.setColor(CodeBlock.TEXT);
        g2.drawString(output, r.x + 14, r.y + 46);
    }

    // The score in the middle of the top bar, with a glow.
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

    // The text in the glow color, blurred and brightened. Made once per score.
    private BufferedImage makeGlow(String text) {
        FontMetrics fm = getFontMetrics(SCORE_FONT);
        int pad = GLOW_RADIUS * 2;
        BufferedImage image = new BufferedImage(fm.stringWidth(text) + 2 * pad, fm.getHeight() + 2 * pad,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();
        // start with invisible pixels in the glow color so the blur does not darken it
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

    // Three ants. Each bug turns one ant red, starting from the right.
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