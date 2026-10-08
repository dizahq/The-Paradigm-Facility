import core.ScoreManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import ui.InfoPanel;
import ui.MainMenuPanel;
import ui.NamePanel;
import ui.PausePanel;
import ui.ScreenManager;
import ui.SettingsPanel;
import ui.TitlePanel;

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
        TITLE, MAIN_MENU, OPTIONS, GAME
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
                                    goTo(Screen.GAME);
                                },
                                screens::hideOverlay)),
                        () -> goTo(Screen.OPTIONS),
                        TheParadigmFacility::quit));

            case OPTIONS -> showSettings(false);

            case GAME -> {
                gameScreen = new JPanel(); // Temporary stand-in until GamePanel exists
                screens.show(gameScreen);
                showPause();
            }
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
                screens::hideOverlay,
                () -> {
                    screens.hideOverlay();
                    goTo(Screen.MAIN_MENU);
                },
                () -> {
                    screens.hideOverlay();
                    showSettings(true);
                },
                TheParadigmFacility::quit));
    }

    private static void quit() {
        screens.dispose();
        frame.dispose();
        System.exit(0);
    }
}