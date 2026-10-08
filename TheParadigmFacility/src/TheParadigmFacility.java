import core.ScoreManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import ui.InfoPanel;
import ui.LeaderBoard;
import ui.MainMenuPanel;
import ui.NamePanel;
import ui.PausePanel;
import ui.ScreenManager;
import ui.SettingsPanel;
import ui.TitlePanel;
import ui.WorkdaySelectPanel;

public class TheParadigmFacility {

    private static JFrame frame;
    private static ScreenManager screens;
    private static ScoreManager scoreManager;
    private static String playerName;
    private static boolean soundOn = true;
    private static JComponent gameScreen;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TheParadigmFacility::start);
    }

    private static void start() {
        scoreManager = new ScoreManager();

        frame = new JFrame("The Paradigm Facility");
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setUndecorated(true);

        screens = new ScreenManager();
        screens.setPreferredSize(new Dimension(1280, 720));

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
        TITLE, MAIN_MENU, OPTIONS, GAME, WORKDAYS, LEADERBOARD;
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
                                    playerName = name;
                                    scoreManager.recordPlayer(name); // <-- this is the key fix
                                    goTo(Screen.WORKDAYS);
                                },
                                screens::hideOverlay)),
                        () -> goTo(Screen.OPTIONS),
                        // () -> goTo(Screen.LEADERBOARD),
                        TheParadigmFacility::quit));

            case OPTIONS -> showSettings(false);

            case GAME -> {
                gameScreen = new JPanel(); // Temporary stand-in until GamePanel exists
                screens.show(gameScreen);
                showPause();
            }

            case WORKDAYS ->
                screens.show(new WorkdaySelectPanel(
                        workdayItems(),
                        day -> goTo(Screen.GAME), // day is 0 to 6; GamePanel will use it later
                        () -> goTo(Screen.MAIN_MENU),
                        () -> goTo(Screen.LEADERBOARD)));

            case LEADERBOARD ->
                screens.show(new LeaderBoard(
                        scoreManager.getTop(5),
                        () -> goTo(Screen.WORKDAYS)));
        }
    }

    private static void showSettings(boolean fromGame) {
        screens.show(new SettingsPanel(
                soundOn,
                on -> soundOn = on,
                () -> screens.showOverlay(InfoPanel.help(screens::hideOverlay)),
                () -> screens.showOverlay(InfoPanel.credits(screens::hideOverlay)),
                () -> screens.showOverlay(InfoPanel.about(screens::hideOverlay)),
                () -> {
                    if (fromGame) {
                        screens.show(gameScreen);
                        showPause();
                    } else {
                        goTo(Screen.MAIN_MENU);
                    }
                },
                fromGame));
    }

    private static void showPause() {
        screens.showOverlay(new PausePanel(
                // Temporary: continue goes to the main menu until GamePanel exists
                // screens::hideOverlay, // for continue if gamePanel exists
                () -> {
                    screens.hideOverlay();
                    goTo(Screen.MAIN_MENU);
                },
                () -> {
                    screens.hideOverlay();
                    goTo(Screen.MAIN_MENU);
                }, // main menu
                () -> {
                    screens.hideOverlay();
                    showSettings(true);
                }, // settings
                TheParadigmFacility::quit)); // exit
    }

    // TEMPORARY: will transfer to Workday later
    private static List<WorkdaySelectPanel.Item> workdayItems() {
        String[] titles = {
                "Introduction to Programming Paradigm",
                "Procedural Programming",
                "Functional Programming",
                "Object-Oriented Programming",
                "Imperative vs Declarative Programming",
                "Event-Driven Programming",
                "Component Mappings Between Programming Paradigms" };
        String[] subtitles = {
                "What a \u201cparadigm\u201d even is",
                "Step-by-step, top to bottom",
                "Pure functions, no side effects",
                "Classes, objects, inheritance, etc.",
                "", "", "" };
        int[] totals = { 15, 34, 39, 34, 49, 29, 15 };

        List<WorkdaySelectPanel.Item> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            items.add(new WorkdaySelectPanel.Item(titles[i], subtitles[i], 0, totals[i], i == 0));
        }
        return items;
    }

    private static void quit() {
        screens.dispose();
        frame.dispose();
        System.exit(0);
    }
}