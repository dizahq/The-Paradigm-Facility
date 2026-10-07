package core;

import java.io.*;
import java.util.*;

/**
 * Manages player scores persisted in a file.
 * Format: CSV with name,score per line
 */
public class ScoreManager {
    
    private static final String SCORES_FILE = "TheParadigmFacility/scores.csv";
    private Map<String, Integer> scores = new LinkedHashMap<>();
    
    public ScoreManager() {
        loadScores();
    }
    
    /**
     * Load all scores from file.
     * Creates file if it doesn't exist.
     */
    private void loadScores() {
        File file = new File(SCORES_FILE);
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create scores file: " + e.getMessage());
            }
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        String name = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        scores.put(name, score);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid score format: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading scores file: " + e.getMessage());
        }
    }
    
    /**
     * Save all scores to file.
     */
    private void saveScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error writing scores file: " + e.getMessage());
        }
    }
    
    /**
     * Record a new player with initial score of 0.
     * If player exists, does nothing.
     */
    public void recordPlayer(String name) {
        if (!scores.containsKey(name)) {
            scores.put(name, 0);
            saveScores();
        }
    }
    
    /**
     * Update a player's score.
     */
    public void setScore(String name, int score) {
        scores.put(name, score);
        saveScores();
    }
    
    /**
     * Get a player's score, or 0 if not found.
     */
    public int getScore(String name) {
        return scores.getOrDefault(name, 0);
    }
    
    /**
     * Get all scores sorted by name.
     */
    public Map<String, Integer> getAllScores() {
        return new LinkedHashMap<>(scores);
    }
    
    /**
     * Get scores sorted by score descending (leaderboard).
     */
    public List<Map.Entry<String, Integer>> getLeaderboard() {
        return scores.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .toList();
    }
}
