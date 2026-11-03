package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import util.Assets;

public class QuestionBank {
    private final ArrayList<Question> questions = new ArrayList<>();

    public QuestionBank() {
        storeQuestions();
    }

    public void storeQuestions() {
        try (BufferedReader br = new BufferedReader(new FileReader(Assets.dataFile("SampleQ.csv")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) {
                    continue; // skip blank or malformed lines
                }
                questions.add(new Question(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                        parts[3].trim(), parts[4].trim(), 0, 0));
            }
        } catch (IOException e) {
            System.err.println("Error while reading SampleQ.csv: " + e.getMessage());
        }
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }
}