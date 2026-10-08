package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.Assets;

/** Main menu: title on top, three picture buttons centered below. */
public class MainMenuPanel extends JPanel {

    private static final String ASSETS = "interface/";
    private static final String TITLE_PATH = ASSETS + "title2.png";
    private static final String START_PATH = ASSETS + "startbtn.png";
    private static final String OPTIONS_PATH = ASSETS + "settingsbtn.png";
    private static final String QUIT_PATH = ASSETS + "exitbtn.png";

    // temp leaderboard button for testing
    private static final String LEADERBOARD_PATH = ASSETS + "leaderboardBtn.png";

    private static final int TITLE_WIDTH = 1000; // title size
    private static final int BUTTON_WIDTH = 400; // width of every button
    private static final int TITLE_GAP = 80; // space between title and first button
    private static final int BUTTON_GAP = 10; // space between buttons

    public MainMenuPanel(Runnable onStart, Runnable onOptions, Runnable onQuit, Runnable onLeaderBoard) {
        setOpaque(false);
        setLayout(new GridBagLayout()); // centers the column in the window

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        JLabel title = buildTitle();
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton start = new ImageButton(START_PATH, BUTTON_WIDTH, onStart);
        ImageButton options = new ImageButton(OPTIONS_PATH, BUTTON_WIDTH, onOptions);
        ImageButton quit = new ImageButton(QUIT_PATH, BUTTON_WIDTH, onQuit);
        ImageButton leaderboard = new ImageButton(LEADERBOARD_PATH, BUTTON_WIDTH, onLeaderBoard); // temporary for
                                                                                                  // testing
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        options.setAlignmentX(Component.CENTER_ALIGNMENT);
        quit.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboard.setAlignmentX(Component.CENTER_ALIGNMENT);

        column.add(title);
        column.add(Box.createVerticalStrut(TITLE_GAP));
        column.add(start);
        column.add(Box.createVerticalStrut(BUTTON_GAP));
        column.add(options);
        column.add(Box.createVerticalStrut(BUTTON_GAP));
        column.add(quit);
        column.add(Box.createVerticalStrut(BUTTON_GAP));
        column.add(leaderboard);

        add(column);
    }

    private JLabel buildTitle() {
        BufferedImage img = Assets.scaled(TITLE_PATH, TITLE_WIDTH);
        if (img != null) {
            return new JLabel(new ImageIcon(img));
        }
        JLabel fallback = new JLabel("PARADIGM FACILITY");
        fallback.setFont(Theme.bold(56));
        fallback.setForeground(Color.WHITE);
        return fallback;
    }
}