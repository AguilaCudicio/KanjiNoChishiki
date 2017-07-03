package ar.uba.kanji;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class KanjiAdapter extends ArrayAdapter<Kanji> {
    Context c;
    public KanjiAdapter(Context context, ArrayList<Kanji> users) {

        super(context, 0, users);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Kanji kanji = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_list_adapter, parent, false);
        }


        ImageButton copyplusbutton = (ImageButton) convertView.findViewById(R.id.icon3);

        copyplusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kanji = getItem(position).symbol;
                ClipboardHelper.addToClipboard(kanji,c);
            }
        });

        ImageButton copybutton = (ImageButton) convertView.findViewById(R.id.icon1);

        copybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kanji = getItem(position).symbol;
                ClipboardHelper.copyToClipboard(kanji,c);
            }
        });

        ImageButton dictionarybutton = (ImageButton) convertView.findViewById(R.id.icon2);

        dictionarybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kanji = getItem(position).symbol;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jisho.org/search/"+kanji));
                c.startActivity(browserIntent);
            }
        });

        ImageButton dictionaryplusbutton = (ImageButton) convertView.findViewById(R.id.icon4);

        dictionaryplusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kanji = getItem(position).symbol;
                String textToPaste = ClipboardHelper.getFromClipboard(c);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jisho.org/search/"+textToPaste+kanji));
                c.startActivity(browserIntent);
            }
        });


        TextView kanjiSymbol = (TextView) convertView.findViewById(R.id.icon);
        TextView kanjiName = (TextView) convertView.findViewById(R.id.firstLine);
        String description = kanji.description;
        if (description.length()>25) description=description.substring(0,21)+"...";
        kanjiName.setText(description);
        kanjiSymbol.setText(kanji.symbol);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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