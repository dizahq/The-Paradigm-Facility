import core.GameState;
import core.QuestionBank;
import core.ScoreManager;
import core.Workday;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import ui.*;

public class TheParadigmFacility {

    private static JFrame frame;
    private static ScreenManager screens;
    private static ScoreManager scoreManager;
    private static QuestionBank questionBank;
    private static GameState game; // the current playthrough, created when the player enters a name
    private static GamePanel gamePanel;
    private static boolean soundOn = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TheParadigmFacility::start);
    }

    private static void start() {
        scoreManager = new ScoreManager();
        questionBank = new QuestionBank();
        checkQuestionCounts();

        frame = new JFrame("The Paradigm Facility");
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setUndecorated(true);

        screens = new ScreenManager();
        screens.setPreferredSize(new Dimension(1280, 720));
        screens.setOpaque(true);
        screens.setBackground(Color.BLACK);

        frame.setContentPane(screens);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });

        frame.setVisible(true);
        goTo(Screen.TITLE);
    }

    private enum Screen {
        TITLE, MAIN_MENU, OPTIONS, GAME, WORKDAYS, LEADERBOARD, FAILED, WIN
    }

    private static void goTo(Screen screen) {
        switch (screen) {
            case TITLE ->
                screens.show(new TitlePanel(() -> goTo(Screen.MAIN_MENU)));

            case MAIN_MENU ->
                screens.show(new MainMenuPanel(
                        () -> screens.showOverlay(new NamePanel(
                                name -> {
                                    screens.hideOverlay();
                                    scoreManager.recordPlayer(name);
                                    game = new GameState(name, questionBank); // a new playthrough
                                    goTo(Screen.WORKDAYS);
                                },
                                screens::hideOverlay)),
                        () -> goTo(Screen.OPTIONS),
                        TheParadigmFacility::quit));

            case OPTIONS -> showSettings(false);

            case GAME -> {
                gamePanel = new GamePanel(game, TheParadigmFacility::showPause, TheParadigmFacility::endOfDay);
                screens.show(gamePanel);
            }

            case WORKDAYS ->
                screens.show(new WorkdaySelectPanel(
                        workdayItems(),
                        index -> startWorkday(index + 1), // index is 0 to 6
                        () -> goTo(Screen.MAIN_MENU),
                        () -> goTo(Screen.LEADERBOARD)));

            case LEADERBOARD ->
                screens.show(new LeaderBoard(
                        scoreManager.getTop(5),
                        () -> goTo(Screen.WORKDAYS)));

            case FAILED ->
                screens.show(new FailedPanel(
                        () -> goTo(Screen.MAIN_MENU),
                        TheParadigmFacility::quit,
                        () -> goTo(Screen.WORKDAYS)));

            case WIN ->
                screens.show(new WinPanel(
                        () -> goTo(Screen.MAIN_MENU),
                        TheParadigmFacility::quit,
                        () -> goTo(Screen.WORKDAYS)));
        }
    }

    // ---- workdays and the game ----

    /** One card per workday, from the current playthrough. */
    private static List<WorkdaySelectPanel.Item> workdayItems() {
        List<WorkdaySelectPanel.Item> items = new ArrayList<>();
        for (Workday day : Workday.ALL) {
            int number = day.number();
            boolean cleared = number <= game.clearedDays();
            items.add(new WorkdaySelectPanel.Item(
                    day.title(),
                    day.subtitle(),
                    game.correctIn(number),
                    questionBank.getQuestions(number).size(),
                    cleared || number == game.nextDay())); // green cards; only nextDay() can be started
        }
        return items;
    }

    private static void startWorkday(int number) {
        if (number != game.nextDay()) {
            return; // a cleared workday cannot be replayed, a later one is still locked
        }
        try {
            game.startDay(number);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return;
        }
        goTo(Screen.GAME);
    }

    /**
     * The last answer of the workday was shown: the day is cleared or the game is
     * over.
     */
    private static void endOfDay() {
        gamePanel.dispose();

        // TEMPORARY until the Day Cleared, Game Over and Victory screens exist
        if (game.isGameOver()) {
            goTo(Screen.FAILED);
        } else if (game.isVictory()) {
            goTo(Screen.WIN);
        } else {
            goTo(Screen.WORKDAYS);
        }
    }

    /**
     * Warns about workdays whose question count in Questions.csv is not the planned
     * one.
     */
    private static void checkQuestionCounts() {
        for (Workday day : Workday.ALL) {
            int found = questionBank.getQuestions(day.number()).size();
            if (found != day.questionTotal()) {
                System.err.println("Workday " + day.number() + " should have " + day.questionTotal()
                        + " questions but Questions.csv has " + found);
            }
        }
    }

    // ---- settings and pause ----

    private static void showSettings(boolean fromGame) {
        screens.show(new SettingsPanel(
                soundOn,
                on -> soundOn = on,
                () -> screens.showOverlay(InfoPanel.help(screens::hideOverlay)),
                () -> screens.showOverlay(InfoPanel.credits(screens::hideOverlay)),
                () -> screens.showOverlay(InfoPanel.about(screens::hideOverlay)),
                () -> {
                    if (fromGame) {
                        screens.show(gamePanel); // back to the same game, still paused
                        showPause();
                    } else {
                        goTo(Screen.MAIN_MENU);
                    }
                },
                fromGame));
    }

    /** The game screen has already paused its timer when this is called. */
    private static void showPause() {
        screens.showOverlay(new PausePanel(
                () -> { // continue
                    screens.hideOverlay();
                    gamePanel.resume();
                },
                () -> { // main menu: leaving ends the playthrough
                    screens.hideOverlay();
                    gamePanel.dispose();
                    goTo(Screen.MAIN_MENU);
                },
                () -> { // settings
                    screens.hideOverlay();
                    showSettings(true);
                },
                TheParadigmFacility::quit)); // exit
    }

    private static void quit() {
        screens.dispose();
        frame.dispose();
        System.exit(0);
    }
}