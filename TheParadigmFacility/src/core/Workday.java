package core;

import java.util.List;

/**
 * One workday: its topic and its difficulty.
 * Points and time per question follow the table in the proposal.
 */
public record Workday(int number, String title, String subtitle, int questionTotal) {

    /**
     * All 7 workdays, in order. questionTotal is how many questions the finished
     * game should have
     * for that day. It is only used to warn about a Questions.csv that is not
     * complete yet.
     */
    public static final List<Workday> ALL = List.of(
            new Workday(1, "Introduction to Programming Paradigm",
                    "What a \u201cparadigm\u201d even is", 15),
            new Workday(2, "Procedural Programming",
                    "Step-by-step, top to bottom", 34),
            new Workday(3, "Functional Programming",
                    "Pure functions, no side effects", 39),
            new Workday(4, "Object-Oriented Programming",
                    "Classes, objects, inheritance, etc.", 34),
            new Workday(5, "Imperative vs Declarative Programming", "", 49),
            new Workday(6, "Event-Driven Programming", "", 29),
            new Workday(7, "Component Mappings Between Programming Paradigms", "", 15));

    /** The workday with this number (1 to 7). */
    public static Workday get(int number) {
        return ALL.get(number - 1);
    }

    /**
     * Points per correct answer: 10 on day 1, plus 20 each day (10, 30, 50 ...
     * 130).
     */
    public int points() {
        return 10 + 20 * (number - 1);
    }

    /** Seconds per question: 30 on day 1, minus 3 each day (30, 27, 24 ... 12). */
    public int timeLimit() { return 30 - 3 * (number - 1); }
}