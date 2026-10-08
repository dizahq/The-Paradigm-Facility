import core.ScoreManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import ui.MainMenuPanel;
import ui.NamePanel;
import ui.ScreenManager;
import ui.SettingsPanel;
import ui.TitlePanel;

public class TheParadigmFacility {

    private static JFrame frame;
    private static ScreenManager screens;
    private static ScoreManager scoreManager;
    private static String playerName;
    private static boolean soundOn = true;

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

            case OPTIONS -> {
                screens.show(new SettingsPanel(
                        soundOn,
                        on -> soundOn = on,
                        () -> System.out.println("Help popup not built yet"),
                        () -> System.out.println("Admin popup not built yet"),
                        () -> System.out.println("About popup not built yet"),
                        () -> goTo(Screen.MAIN_MENU)));
            }

            case GAME -> {
                System.out.println("Game screen not built yet, player name: " + playerName);
            }
        }
    }

    private static void quit() {
        screens.dispose();
        frame.dispose();
        System.exit(0);
    }
}