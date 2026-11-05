package ui;

import java.util.function.DoubleConsumer;
import javax.swing.Timer;

/* Counts down in seconds and reports the time left. Pausing keeps the time that
 * is left. Runs on the Swing thread, so the callbacks can touch the UI.
 */
public class CountdownTimer {

    private static final int TICK_MS = 20;

    private final Timer timer;
    private final DoubleConsumer onTick;
    private final Runnable onFinish;

    private long endNanos; // when the countdown reaches 0, while running
    private double remaining; // seconds left, while paused
    private boolean running;
    private boolean paused;

    /**
     * @param onTick   called about every 20 ms with the seconds left
     * @param onFinish called once when the time reaches 0
     */
    public CountdownTimer(DoubleConsumer onTick, Runnable onFinish) {
        this.onTick = onTick;
        this.onFinish = onFinish;
        this.timer = new Timer(TICK_MS, e -> tick());
    }

    // Starts a new countdown from the given seconds.
    public void start(double seconds) {
        paused = false;
        remaining = seconds;
        onTick.accept(remaining);
        run();
    }

    // Freezes the countdown and keeps the time that is left.
    public void pause() {
        if (running) {
            remaining = Math.max(0, secondsLeft());
            timer.stop();
            running = false;
            paused = true;
        }
    }

    // Continues after pause(). Does nothing after stop().
    public void resume() {
        if (!paused) {
            return;
        }
        paused = false;
        if (remaining > 0) {
            run();
        } else {
            finish(); // time ran out at the moment of the pause
        }
    }

    // Ends the countdown. It cannot be resumed.
    public void stop() {
        timer.stop();
        running = false;
        paused = false;
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
        finish();
    }

    private void finish() {
        stop();
        onTick.accept(0);
        onFinish.run();
    }
}
