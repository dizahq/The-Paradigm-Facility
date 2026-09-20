package ui;

import java.io.File;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

/**
 * Looping, muted JavaFX video background, usable as a Swing component.
 * Created once and kept under every screen.
 */
public class BackgroundLoop extends JFXPanel {

    private static final String DEFAULT_VIDEO_PATH =
            "TheParadigmFacility/assets/background/background.mp4";

    private MediaPlayer player; // only touched on the JavaFX thread

    public BackgroundLoop() {
        this(DEFAULT_VIDEO_PATH);
    }

    public BackgroundLoop(String videoPath) {
        File file = new File(videoPath);
        if (!file.exists()) {
            System.err.println("Video not found at: " + file.getAbsolutePath());
        }

        Platform.setImplicitExit(false);

        Platform.runLater(() -> {
            Media media = new Media(file.toURI().toString());
            player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setMute(true);

            media.setOnError(() -> System.err.println("Media error: " + media.getError()));
            player.setOnError(() -> System.err.println("Player error: " + player.getError()));

            MediaView view = new MediaView(player);
            view.setPreserveRatio(false);

            StackPane root = new StackPane(view);
            Scene scene = new Scene(root, Color.BLACK);

            view.fitWidthProperty().bind(scene.widthProperty());
            view.fitHeightProperty().bind(scene.heightProperty());

            SwingUtilities.invokeLater(() -> setScene(scene));
            player.play();
        });
    }

    public void play()  { Platform.runLater(() -> { if (player != null) player.play(); }); }
    public void pause() { Platform.runLater(() -> { if (player != null) player.pause(); }); }

    public void dispose() {
        Platform.runLater(() -> {
            if (player != null) {
                player.stop();
                player.dispose();
            }
        });
    }
}