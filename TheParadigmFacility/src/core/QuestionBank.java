package core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import util.Assets;

public class QuestionBank {
    private static final String QUESTION_FILE = "Questions.csv";
    private static final int COLUMNS = 8;

    private final List<Question> questions = new ArrayList<>();

    public QuestionBank() {
        load();
    }

    // A new list with the questions of one workday (1 to 7).
    public List<Question> getQuestions(int workday) {
        List<Question> result = new ArrayList<>();
        for (Question q : questions) {
            if (q.getWorkday() == workday) {
                result.add(q);
            }
        }
        return result;
    }

    private void load() {
        String text;
        try (InputStream in = Assets.openData(QUESTION_FILE)) {
            if (in == null) {
                System.err.println(QUESTION_FILE + " not found");
                return;
            }
            text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Could not read " + QUESTION_FILE + ": " + e.getMessage());
            return;
        }
        if (text.startsWith("\uFEFF")) {
            text = text.substring(1); // byte order mark added by Excel
        }

        List<List<String>> rows = parse(text);
        for (int i = 1; i < rows.size(); i++) { // row 0 is the header
            List<String> r = rows.get(i);
            if (r.size() < COLUMNS) {
                System.err.println(QUESTION_FILE + ": skipped row " + (i + 1) + " (needs " + COLUMNS + " columns)");
                continue;
            }
            try {
                questions.add(new Question(
                        Integer.parseInt(r.get(0).trim()),
                        r.get(1).trim(),
                        r.get(2).trim(),
                        r.get(4).trim(), r.get(5).trim(), r.get(6).trim(), r.get(7).trim()));
            } catch (NumberFormatException e) {
                System.err.println(QUESTION_FILE + ": skipped row " + (i + 1) + " (workday is not a number)");
            }
        }
    }

    // Reads CSV text.
    private static List<List<String>> parse(String text) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < text.length() && text.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                row.add(field.toString());
                field.setLength(0);
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                    i++;
                }
                row.add(field.toString());
                field.setLength(0);
                if (!(row.size() == 1 && row.get(0).isEmpty())) { // skip blank lines
                    rows.add(row);
                }
                row = new ArrayList<>();
            } else {
                field.append(c);
            }
        }
        if (field.length() > 0 || !row.isEmpty()) { // last line without a newline
            row.add(field.toString());
            rows.add(row);
        }
        return rows;
    }
}
