package ui;

import java.util.function.DoubleConsumer;
import javax.swing.Timer;

/**
 * Counts down in seconds and reports the time left. Pause and resume keep the
 * time that is
 * left. Runs on the Swing thread, so the callbacks can touch the UI.
 */
public class CountdownTimer {

    private static final int TICK_MS = 20;

    private final Timer timer;
    private final DoubleConsumer onTick;
    private final Runnable onFinish;

    private long endNanos; // when the countdown reaches 0, while running
    private double remaining; // seconds left, while paused or stopped
    private boolean running;

    /**
     * @param onTick   called about every 20 ms with the seconds left
     * @param onFinish called once when the time reaches 0
     */
    public CountdownTimer(DoubleConsumer onTick, Runnable onFinish) {
        this.onTick = onTick;
        this.onFinish = onFinish;
        this.timer = new Timer(TICK_MS, e -> tick());
    }

    /** Starts a new countdown from the given seconds. */
    public void start(double seconds) {
        remaining = seconds;
        onTick.accept(remaining);
        run();
    }

    /** Freezes the countdown and keeps the time that is left. */
    public void pause() {
        if (running) {
            remaining = secondsLeft();
            timer.stop();
            running = false;
        }
    }

    /**
     * Continues after pause(). Does nothing after stop() or when the time is up.
     */
    public void resume() {
        if (!running && remaining > 0) {
            run();
        }
    }

    /** Ends the countdown. It cannot be resumed. */
    public void stop() {
        timer.stop();
        running = false;
        remaining = 0;
    }

    private void run() {
        endNanos = System.nanoTime() + (long) (remaining * 1_000_000_000.0);
        running = true;
        timer.start();
    }

    private double secondsLeft() {
        return (endNanos - System.nanoTime()) / 1_000_000_000.0;
    }

    private void tick() {
        if (!running) {
            return;
        }
        double left = secondsLeft();
        if (left > 0) {
            onTick.accept(left);
            return;
        }
        stop();
        onTick.accept(0);
        onFinish.run();
    }
}