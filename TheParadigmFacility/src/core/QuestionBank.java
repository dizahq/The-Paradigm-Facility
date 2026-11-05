package core;

<<<<<<< HEAD
public class QuestionBank {

}
=======
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import util.Assets;

// Loads data/Questions.csv. Columns:
// workday,type,question,code,correct,wrong1,wrong2,wrong3
public class QuestionBank {
    private static final String QUESTION_FILE = "Questions.csv";
    private static final int COLUMNS = 8;

    private final ArrayList<Question> questions = new ArrayList<>();

    public QuestionBank() {
        load();
    }

    // Every question, all workdays
    public ArrayList<Question> getQuestions() {
        return new ArrayList<>(questions);
    }

    // A fresh list of the questions for one workday (1 to 7)
    public ArrayList<Question> getQuestions(int workday) {
        ArrayList<Question> result = new ArrayList<>();
        for (Question q : questions) {
            if (q.getWorkday() == workday) {
                result.add(q);
            }
        }
        return result;
    }

    private void load() {
        String text;
        try {
            text = new String(Files.readAllBytes(Assets.dataFile(QUESTION_FILE).toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Could not read " + QUESTION_FILE + ": " + e.getMessage());
            return;
        }
        if (text.startsWith("\uFEFF")) {
            text = text.substring(1); // Excel's "CSV UTF-8" adds this marker
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
                        r.get(3).replace("\r\n", "\n").stripTrailing(),
                        r.get(4).trim(), r.get(5).trim(), r.get(6).trim(), r.get(7).trim()));
            } catch (NumberFormatException e) {
                System.err.println(QUESTION_FILE + ": skipped row " + (i + 1) + " (workday is not a number)");
            }
        }
    }

    // Reads CSV text: "quoted, fields", "" for a literal quote, line breaks inside
    // quotes
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
        if (field.length() > 0 || !row.isEmpty()) { // last line without a trailing newline
            row.add(field.toString());
            rows.add(row);
        }
        return rows;
    }
}
>>>>>>> b2dcba9edb52465a74fbcae6e64fecf9d962717e
