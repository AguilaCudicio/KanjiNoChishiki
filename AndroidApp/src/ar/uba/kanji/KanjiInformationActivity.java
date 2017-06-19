package ar.uba.kanji;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class KanjiInformationActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kanji_information);

        Intent intent = getIntent();
        String  symbol = intent.getStringExtra("symbol");
        TextView kanjiName = (TextView) this.findViewById(R.id.symbol);
        kanjiName.setText(symbol);
        String  readings = intent.getStringExtra("readings");
        TextView kanjireadings = (TextView) this.findViewById(R.id.firstLine);
        kanjireadings.setText(readings);

        String meaning = intent.getStringExtra("meaning");
        TextView kanjimeaning = (TextView) this.findViewById(R.id.title);
        kanjimeaning.setText(meaning);
        String strokes = intent.getStringExtra("strokes")+" strokes";
        TextView strokeskanji = (TextView) this.findViewById(R.id.strokes);
        strokeskanji.setText(strokes);

        String jlpt = "JLPT level "+intent.getStringExtra("jlpt");
        TextView kanjijlpt = (TextView) this.findViewById(R.id.jlpt);
        kanjijlpt.setText(jlpt);

        String grades = "Taught in grade  "+intent.getStringExtra("grade");
        TextView kanjigrade = (TextView) this.findViewById(R.id.grades);
        kanjigrade.setText(grades);

    }
}
