package ar.uba.kanji;


public class Kanji {
    public String symbol;
    public String description;
    public String readings;
    public String jlpt;
    public String grade;
    public String strokes;

    public Kanji(String symbol, String description, String readings, String jlpt, String grade, String strokes) {
        this.symbol = symbol;
        this.description = description;
        this.readings = readings;
        this.jlpt = jlpt;
        this.grade = grade;
        this.strokes = strokes;
    }
}
