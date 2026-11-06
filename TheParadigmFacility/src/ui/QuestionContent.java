package ui;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Splits the raw question text (and the answers) into what the game screen
 * needs, and decides which layout fits.
 *
 * SIMPLE text question, text answers: question on top, 2x2 grid.
 * CODE_QUESTION code in the question, short answers (case A: output, case C:
 * the blank).
 * Code on the left, the 4 answers stacked on the right.
 * CODE_CHOICES code in the answers (case B). Short prompt on top, 2x2 grid of
 * code boxes.
 *
 * The kind is read from the content itself, so Question does not need to know
 * about it.
 * Convention in the CSV: the first line of the question is the prompt, the rest
 * is code
 * (case A/C) or the expected output (case B).
 */
public record QuestionContent(Kind kind, String prompt, List<String> code, String expectedOutput,
        boolean monoAnswers, boolean hasBlank) {

    public enum Kind {
        SIMPLE, CODE_QUESTION, CODE_CHOICES
    }

    // A run of 3 or more underscores marks the blank in a "fill in the code"
    // question.
    static final Pattern BLANK = Pattern.compile("_{3,}");

    private static final Pattern SNIPPET_PROMPT = Pattern.compile("(?i)\\bsnippets?\\b");
    private static final Pattern OUTPUT_PREFIX = Pattern.compile("(?i)^\\s*(?:expected\\s+)?output\\s*:\\s*");

    public static QuestionContent of(String rawQuestion, List<String> rawAnswers) {
        String question = normalize(rawQuestion);
        List<String> lines = lines(question);
        String prompt = lines.get(0);
        boolean hasBlank = BLANK.matcher(question).find();

        boolean multiLineAnswer = rawAnswers.stream().anyMatch(a -> normalize(a).indexOf('\n') >= 0);
        // one-line code answers ("main = print (2^3)") are caught by the prompt: "Which
        // snippet ..."
        boolean snippetPrompt = lines.size() <= 2 && !hasBlank && SNIPPET_PROMPT.matcher(prompt).find();

        if (multiLineAnswer || snippetPrompt) {
            String output = lines.size() > 1
                    ? OUTPUT_PREFIX.matcher(String.join(" ", lines.subList(1, lines.size()))).replaceFirst("").strip()
                    : null;
            return new QuestionContent(Kind.CODE_CHOICES, prompt, List.of(), output, true, false);
        }
        if (lines.size() > 1) {
            boolean mono = hasBlank || prompt.toLowerCase(Locale.ROOT).contains("output");
            return new QuestionContent(Kind.CODE_QUESTION, prompt, lines.subList(1, lines.size()), null, mono,
                    hasBlank);
        }
        return new QuestionContent(Kind.SIMPLE, prompt, List.of(), null, false, false);
    }

    // \r\n to \n, tabs to 4 spaces, no trailing whitespace.
    public static String normalize(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n').replace("\t", "    ").stripTrailing();
    }

    static List<String> lines(String normalized) {
        return List.of(normalized.split("\n", -1));
    }
}
