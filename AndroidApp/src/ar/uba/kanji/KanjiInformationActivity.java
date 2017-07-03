package ar.uba.kanji;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class KanjiInformationActivity extends Activity {

    String symbol;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kanji_information);

        Intent intent = getIntent();
        symbol = intent.getStringExtra("symbol");
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


        String jlptLevel= intent.getStringExtra("jlpt");
        String jlpt = "JLPT level "+jlptLevel;
        if (Integer.parseInt(jlptLevel)<1)  jlpt = "Not taught in JLPT";

        TextView kanjijlpt = (TextView) this.findViewById(R.id.jlpt);
        kanjijlpt.setText(jlpt);

        String gradeNumber= intent.getStringExtra("grade");
        String grades = "Taught in grade  "+ gradeNumber;
        if (Integer.parseInt(gradeNumber)<1) grades = "Not taught in school";
        TextView kanjigrade = (TextView) this.findViewById(R.id.grades);
        kanjigrade.setText(grades);

    }

   public void copy(View view){
       ClipboardHelper.copyToClipboard(symbol,this.getApplicationContext());
   }

   public void findInDictionary(View view){
       Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jisho.org/search/"+symbol));
       startActivity(browserIntent);
   }

   public void  findInDictionaryPlus(View v) {
       String textToPaste = ClipboardHelper.getFromClipboard(this.getApplicationContext());
       Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jisho.org/search/"+textToPaste+symbol));
       startActivity(browserIntent);
   }

   public void copyPlus(View v) {
       ClipboardHelper.addToClipboard(symbol,this.getApplicationContext());
    }


}
