package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import util.Assets;

// Keeps the top 5 scores of all playthroughs in scoreboard.csv (name,score per line). 
public class ScoreManager {

    public static final int MAX_ENTRIES = 5;

    // One leaderboard line.
    public record Entry(String name, int score) {
    }

    private final File file = Assets.saveFile("scoreboard.csv");
    private final List<Entry> entries = new ArrayList<>();

    public ScoreManager() {
        load();
    }

    public void record(String name, int score) {
        if (name == null || name.isBlank()) {
            return;
        }
        entries.add(new Entry(name.trim(), score));
        entries.sort(Comparator.comparingInt(Entry::score).reversed()); // stable sort
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
        save();
    }

    // Saved scores, highest first.
    public List<Entry> getTop() {
        return new ArrayList<>(entries);
    }

    private void load() {
        if (!file.isFile()) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int comma = line.lastIndexOf(',');
                if (comma <= 0) {
                    continue;
                }
                try {
                    entries.add(new Entry(line.substring(0, comma).trim(),
                            Integer.parseInt(line.substring(comma + 1).trim())));
                } catch (NumberFormatException e) {
                    // skip a damaged line
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read scores: " + e.getMessage());
        }
        entries.sort(Comparator.comparingInt(Entry::score).reversed());
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            for (Entry e : entries) {
                writer.write(e.name() + "," + e.score());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not save scores: " + e.getMessage());
        }
    }
}
