package ar.uba.kanji;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

public class ActivityWithPermission  extends Activity {


        public static final int PERMISSIONS_REQUEST = 1;

        private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
        private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        private static final String PERMISSION_READ = Manifest.permission.READ_EXTERNAL_STORAGE;



        protected boolean hasPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(PERMISSION_READ) == PackageManager.PERMISSION_GRANTED;
            } else {
                return true;
            }
        }

        protected void requestPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) || shouldShowRequestPermissionRationale(PERMISSION_STORAGE)
                        || shouldShowRequestPermissionRationale(PERMISSION_READ) ) {
                    Toast.makeText(ActivityWithPermission.this, "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[] {PERMISSION_CAMERA, PERMISSION_STORAGE, PERMISSION_READ}, PERMISSIONS_REQUEST);
            }
        }

}
