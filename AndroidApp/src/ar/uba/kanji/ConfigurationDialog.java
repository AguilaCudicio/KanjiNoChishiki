package ar.uba.kanji;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class ConfigurationDialog extends DialogFragment {
    final CharSequence items[] = { "Restric to jōyō kanji", "Don't restrict the search" };

    Context c;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        c= super.getActivity().getApplicationContext();

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences( c );
        int conf = mPrefs.getInt("config",-1);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle("Configuration")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()  {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setSingleChoiceItems(items, conf,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences( c );
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.putInt( "config", item );
                                editor.commit();

                            }

        });

        return builder.create();
    }
}