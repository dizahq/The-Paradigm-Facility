import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ui.ScreenManager;
import ui.TitlePanel;

public class TheParadigmFacility {

    private static ScreenManager screens;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TheParadigmFacility::start);
    }

    private static void start() {
        JFrame frame = new JFrame("The Paradigm Facility");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.getContentPane().setBackground(Color.BLACK);

        screens = new ScreenManager();
        screens.setPreferredSize(new Dimension(1280, 720));
        screens.show(new TitlePanel(TheParadigmFacility::onStart));

        frame.setContentPane(screens);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                screens.dispose();
                frame.dispose();
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }

    private static void onStart() {
        System.out.println("Start pressed");
        // screens.show(new OnboardingPanel(...));  <- next panel goes here
    }
}