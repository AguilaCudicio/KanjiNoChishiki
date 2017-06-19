package ar.uba.kanji;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class KanjiAdapter extends ArrayAdapter<Kanji> {
    Context c;
    public KanjiAdapter(Context context, ArrayList<Kanji> users) {

        super(context, 0, users);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Kanji kanji = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_list_adapter, parent, false);
        }
        TextView kanjiSymbol = (TextView) convertView.findViewById(R.id.icon);
        TextView kanjiName = (TextView) convertView.findViewById(R.id.firstLine);
        kanjiName.setText(kanji.description);
        kanjiSymbol.setText(kanji.symbol);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int position = (Integer) view.getTag();
                Kanji kanji = getItem(position);
                Intent intent = new Intent(c, KanjiInformationActivity.class);
                intent.putExtra("symbol",kanji.symbol);
                intent.putExtra("meaning",kanji.description);
                intent.putExtra("strokes",kanji.strokes);
                intent.putExtra("jlpt",kanji.jlpt);
                intent.putExtra("grade",kanji.grade);
                intent.putExtra("readings",kanji.readings);
                c.startActivity(intent);
        }
    });
     return convertView;

    }
}