package audio;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import util.Assets;

/**
 * Plays the game's music and sound effects with Java Sound (WAV files in
 * assets/audio/).
 * Everything is silent while the Sound switch in Settings is off.
 */
public class SoundManager {

    // Short sounds. Each play starts a new clip, so sounds can overlap.
    public enum Sfx {
        CLICK("click"), CORRECT("correct_ping"), WRONG("wrong_buzz"),
        STREAK("streak"), WIN("win"), LOSS("loss");

        private final String file;

        Sfx(String file) {
            this.file = file;
        }
    }

    // Looping background tracks. Only one plays at a time.
    public enum Music {
        MAIN("main_theme"), GAME("game_theme");

        private final String file;

        Music(String file) {
            this.file = file;
        }
    }

    private record Data(AudioFormat format, byte[] bytes) {
    }

    private static final Map<Sfx, Data> sfx = new EnumMap<>(Sfx.class);
    private static final Map<Music, Clip> tracks = new EnumMap<>(Music.class);

    private static boolean enabled = true;
    private static boolean paused;
    private static Music wanted; // the track that should play, null before the first one
    private static Clip current;

    private SoundManager() {
    }

    // Loads every sound once so nothing stutters later. Call at startup.
    public static void preload() {
        for (Sfx s : Sfx.values()) {
            data(s);
        }
        for (Music m : Music.values()) {
            track(m);
        }
    }

    public static void play(Sfx s) {
        Data data = data(s);
        if (!enabled || data == null) {
            return;
        }
        try {
            Clip clip = open(data);
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (Exception e) {
            System.err.println("Cannot play " + s + ": " + e.getMessage());
        }
    }

    public static void click() {
        play(Sfx.CLICK);
    }

    // Switches to a track and plays it from the start. Does nothing if it is
    // already playing.
    public static void playMusic(Music m) {
        if (m == wanted) {
            return;
        }
        if (current != null) {
            current.stop();
            current.setFramePosition(0);
        }
        wanted = m;
        paused = false;
        current = track(m);
        startMusic();
    }

    // Freezes the current track where it is.
    public static void pauseMusic() {
        paused = true;
        if (current != null) {
            current.stop();
        }
    }

    // Continues the track where pauseMusic() stopped it.
    public static void resumeMusic() {
        paused = false;
        startMusic();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    // Sound switch: off silences effects and music, on brings the music back.
    public static void setEnabled(boolean on) {
        enabled = on;
        if (on) {
            startMusic();
        } else if (current != null) {
            current.stop();
        }
    }

    private static void startMusic() {
        if (current != null && enabled && !paused) {
            current.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private static Data data(Sfx s) {
        if (!sfx.containsKey(s)) {
            sfx.put(s, load(s.file));
        }
        return sfx.get(s);
    }

    private static Clip track(Music m) {
        if (!tracks.containsKey(m)) {
            Clip clip = null;
            Data data = load(m.file);
            if (data != null) {
                try {
                    clip = open(data);
                } catch (Exception e) {
                    System.err.println("Cannot open " + m + ": " + e.getMessage());
                }
            }
            tracks.put(m, clip);
        }
        return tracks.get(m);
    }

    private static Clip open(Data data) throws Exception {
        Clip clip = AudioSystem.getClip();
        clip.open(data.format(), data.bytes(), 0, data.bytes().length);
        return clip;
    }

    // Reads assets/audio/<name>.wav. Returns null if it is missing or unreadable.
    private static Data load(String name) {
        try (InputStream in = Assets.open("audio/" + name + ".wav")) {
            if (in == null) {
                System.err.println("Sound not found: " + name + ".wav");
                return null;
            }
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(in))) {
                return new Data(audio.getFormat(), audio.readAllBytes());
            }
        } catch (Exception e) {
            System.err.println("Cannot load " + name + ".wav: " + e.getMessage());
            return null;
        }
    }
}