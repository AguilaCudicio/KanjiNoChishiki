package ar.uba.kanji;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ClassifyImageActivity  extends Activity {


    private static final int PERMISSIONS_REQUEST = 5;

    private static final String PERMISSION_READ = Manifest.permission.READ_EXTERNAL_STORAGE;

    private Classifier classifier;
    private static final int INPUT_SIZE = 64;
    private static final int IMAGE_MEAN = 0;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "InputI";
    private static final String OUTPUT_NAME = "finalresult";

    private static final String MODEL_FILE = "file:///android_asset/optimizedmodel.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/lista.txt";

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        classifier =
                TensorFlowImageClassifier.create(
                        getAssets(),
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME);

        if (!hasPermission()) {
            requestPermission();
        }

        Intent intent = getIntent();
        Uri resultUri = Uri.parse(intent.getStringExtra("imageUri"));

        try {
            Bitmap cBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            Bitmap read = Bitmap.createScaledBitmap(cBitmap, INPUT_SIZE, INPUT_SIZE, false);
            final List<Classifier.Recognition> results = classifier.recognizeImage(read);

            ArrayList<Kanji> arrayOfKanji = new ArrayList<Kanji>();
            for (final Classifier.Recognition recog : results) {
                String[] parts = recog.getTitle().split(",");
                Float probability = recog.getConfidence();
                if(parts.length>4){
                    arrayOfKanji.add(new Kanji(parts[0],parts[5],parts[4],parts[3],parts[2], parts[1],probability));
                }
                else {
                    arrayOfKanji.add(new Kanji(parts[0].substring(0,1),"","","","","", probability));
                }
            }

            KanjiAdapter adapter = new KanjiAdapter(this, arrayOfKanji);

            ListView listView = (ListView) findViewById(R.id.listviewresult);
            listView.setAdapter(adapter);

        }
        catch (Exception e) {
            Log.v("OMG","excepcion");
        }
    }


    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    requestPermission();
                }
            }
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_READ) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( shouldShowRequestPermissionRationale(PERMISSION_READ) ) {
                Toast.makeText(ClassifyImageActivity.this, "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[] {PERMISSION_READ}, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ClassifierActivity.class);
        startActivity(intent);
        finish();
    }



}
