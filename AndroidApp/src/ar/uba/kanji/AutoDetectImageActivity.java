package ar.uba.kanji;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AutoDetectImageActivity extends Activity {

    private double threshold = 20;

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
        setContentView(R.layout.autodetect_image);

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
            Bitmap a =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            Bitmap cBitmap=a.copy(Bitmap.Config.ARGB_8888, true);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;

            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(  this );
            int conf = mPrefs.getInt("config",-1);
            classifier.setRestrictSearch(conf==0);


            int windSize = a.getWidth();
            if ( a.getHeight() < a.getWidth())  windSize = a.getHeight();

            int step = windSize/10;

            List<Integer> res = new ArrayList<>();

            for ( int width = windSize; width> windSize/4 ; width=width/2) {
                int height = width;
                for (int x=0; x<=(a.getWidth()-width); x= x+step) {
                    for (int y=0; y<=(a.getHeight()-height); y= y+step) {
                        Bitmap b = Bitmap.createBitmap(a, x, y, width, height);
                        Bitmap read = Bitmap.createScaledBitmap(b, INPUT_SIZE, INPUT_SIZE, true);

                        final List<Classifier.Recognition> results = classifier.recognizeImage(read);

                        for (final Classifier.Recognition recog : results) {
                            Float probability = recog.getConfidence();
                            if (probability > threshold) {
                                res.add(Integer.valueOf(x));
                                res.add(Integer.valueOf(y));
                                res.add(Integer.valueOf(width));
                                res.add(Integer.valueOf(height));
                            }
                        }
                    }
                }


            }

            Canvas canvas = new Canvas(cBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            for (int j=0; j<res.size();j=j+4) {
                Log.v("KANJII",res.get(j).toString());

            }

            float leftx = res.get(0);
            float topy = res.get(0);
            float rightx =res.get(200);
            float bottomy = res.get(200);
            canvas.drawRect(leftx, topy, rightx, bottomy, paint);


            ImageView iView = (ImageView)findViewById(R.id.imageView );

            iView.setImageBitmap(cBitmap);

            Log.v("KANJII","yay");
        }
        catch (Exception e) {
            Log.v("KANJII",e.toString());
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
                Toast.makeText(AutoDetectImageActivity.this, "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
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
