package ar.uba.kanji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class KanjiAdapter extends ArrayAdapter<Kanji> {
    public KanjiAdapter(Context context, ArrayList<Kanji> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Kanji kanji = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_list_adapter, parent, false);
        }
        TextView kanjiSymbol = (TextView) convertView.findViewById(R.id.icon);
        TextView kanjiName = (TextView) convertView.findViewById(R.id.firstLine);
        kanjiName.setText(kanji.description);
        kanjiSymbol.setText(kanji.symbol);
        return convertView;
    }
}
