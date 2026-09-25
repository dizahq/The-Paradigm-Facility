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
// import ui.GamePanel;

public class TheParadigmFacility {

    private static JFrame frame;
    private static ScreenManager screens;
    private static String playerName;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TheParadigmFacility::start);
    }

    private static void start() {
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

    /** Every screen in the game. Add one entry per new panel. */
    private enum Screen { TITLE, MAIN_MENU, OPTIONS, GAME }

    /** Single place that decides what each screen shows and swaps to it. */
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
                                    goTo(Screen.GAME);
                                },
                                screens::hideOverlay)),
                        () -> System.out.println("Options not built yet"),
                        TheParadigmFacility::quit));

            case OPTIONS -> {
                // screens.show(new OptionsPanel(() -> goTo(Screen.MAIN_MENU)));
                System.out.println("Options screen not built yet");
            }

            case GAME -> {
                // screens.show(new GamePanel(playerName, ...));
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