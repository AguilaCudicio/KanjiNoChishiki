package ar.uba.kanji;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class DictionaryActivity extends Activity {

    private HashMap<String, String[]> dicMeaning;
    private HashMap<String, String[]> dicReading;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dicMeaning = new HashMap<>();
        dicReading = new HashMap<>();
        setContentView(R.layout.dictionary);
        createDictionaryMeanings();
        createDictionaryReadings();

        final EditText mEdit   = (EditText)findViewById(R.id.searchmeaningkey);

        final EditText rEdit   = (EditText)findViewById(R.id.searchpronunciationkey);
        KanaText wkj = new KanaText(rEdit, false);
        wkj.bind();


        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(mEdit.getText().toString(),rEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });

        rEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(mEdit.getText().toString(),rEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch(String meaning, String reading) {
        Vector<String> labels = new Vector<String>();
        AssetManager am = getApplicationContext().getAssets();

        try {
            InputStream is = am.open("lista.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading label file!" , e);
        }

        final List<Integer> list = getResultList(meaning);
        final List<Integer> list2 = getResultListReadings(reading);

        List<Integer> common;

        if ( list.isEmpty() && !list2.isEmpty()) {
            common = new ArrayList<>(list2);
        }
        else if ( list2.isEmpty() && !list.isEmpty() ) {
            common = new ArrayList<>(list);
        }
        else {
            common = new ArrayList<>(list);
            common.retainAll(list2);
        }


        Set<Integer> aux = new HashSet<Integer>(common);
        List<Integer> result = new ArrayList<Integer>(aux);


        ArrayList<Kanji> arrayOfKanji = getArrayKanjis(result,labels);

        KanjiAdapter adapter = new KanjiAdapter(this, arrayOfKanji, false);

        ListView listView = (ListView) findViewById(R.id.listviewresult);
        listView.setAdapter(adapter);

    }



    private ArrayList<Kanji> getArrayKanjis (List<Integer> list, Vector<String> labels) {
        ArrayList<Kanji> arrayOfKanji = new ArrayList<Kanji>();
        for (int i=0; i<list.size(); i++){

            String[] parts = labels.get(list.get(i)).split(",");
            String description=parts[5].substring(0,parts[5].length()-1).replaceAll("\\}",",").replaceAll("\\{","");
            String readings=parts[4].replaceAll(" ", ", ");
            arrayOfKanji.add(new Kanji(parts[0],description,readings,parts[3],parts[2], parts[1],1.0f));
        }
        return arrayOfKanji;
    }
    private  List<Integer>  getResultListReadings(String stringToSearch) {

        String c = stringToSearch;
        if ( stringToSearch.contains("\n") ) {
            c = stringToSearch.substring(0,stringToSearch.length()-1);
        }

        List<Integer> result = new ArrayList<Integer>();

        if (dicReading.containsKey(c)) {
                String[] sres = dicReading.get(c);
                for (int j=0; j<sres.length;j++){
                    Integer integ = new Integer(Integer.parseInt(sres[j]));
                    result.add(integ);
                }
        }

        return result;

    }

    private  List<Integer>  getResultList(String stringToSearch) {

        String c = stringToSearch;
        if ( stringToSearch.contains("\n") ) {
            c = stringToSearch.substring(0,stringToSearch.length()-1);
        }

        List<Integer> result = new ArrayList<Integer>();

        List<Integer> possibleResults = new ArrayList<Integer>() ;
        List<List<Integer>> resultsContainingWords =  new ArrayList<>();

        String[] wordsToSearch = c.split(" ");


        for (int i=0; i<wordsToSearch.length;i++){
            String word =wordsToSearch[i];
            if (dicMeaning.containsKey(word)) {
                List<Integer> res = new ArrayList<Integer>();
                String[] sres = dicMeaning.get(word);
                for (int j=0; j<sres.length;j++){
                    Integer integ = new Integer(Integer.parseInt(sres[j]));
                    res.add(integ);
                    possibleResults.add(integ);
                    result.add(integ);
                }
                resultsContainingWords.add (res);
            }
        }

        for (int n= 0; n<possibleResults.size();n++){
            for (int x=0;x<resultsContainingWords.size();x++){
                List<Integer> r  =resultsContainingWords.get(x);
                if (!r.contains(possibleResults.get(n))){
                    result.remove(possibleResults.get(n));
                }
            }
        }

        //Remove duplicates
        HashSet<Integer> hashSet = new HashSet<Integer>();
        hashSet.addAll(result);
        result.clear();
        result.addAll(hashSet);
        return result;

    }


    private void createDictionaryMeanings() {
        AssetManager am = getApplicationContext().getAssets();
        try {
          InputStream is = am.open("dictionary.txt");
          BufferedReader r = new BufferedReader(new InputStreamReader(is));
          String line;
          while ((line = r.readLine()) != null) {
                  String[] id =  line.split(":::");
                  String[] lineNumbers = id[1].split(" ");
                  dicMeaning.put(id[0], lineNumbers);
            }
        }

        catch (Exception e){
            Toast.makeText(DictionaryActivity.this, "There seems to be a problem reading the dictionary file", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void createDictionaryReadings() {
        AssetManager am = getApplicationContext().getAssets();
        try {
            InputStream is = am.open("readings.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = r.readLine()) != null) {
                String[] id =  line.split(":::");
                String[] lineNumbers = id[1].split(" ");
                dicReading.put(id[0], lineNumbers);
            }
        }

        catch (Exception e){
            Toast.makeText(DictionaryActivity.this, "There seems to be a problem reading the readings file", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
