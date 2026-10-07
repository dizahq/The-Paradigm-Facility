import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import core.ScoreManager;
import ui.MainMenuPanel;
import ui.NamePanel;
import ui.ScreenManager;
import ui.TitlePanel;

public class TheParadigmFacility {

    private static JFrame frame;
    private static ScreenManager screens;
    private static ScoreManager scoreManager;
    private static String playerName;

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
                        () -> System.out.println("Options not built yet"),
                        TheParadigmFacility::quit));

            case OPTIONS -> {
                System.out.println("Options screen not built yet");
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