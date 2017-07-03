package ar.uba.kanji;


import android.content.ClipData;
import android.content.Context;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class ClipboardHelper {

    public static void addToClipboard(String add, Context c) {
        String textToPaste= "";
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            ClipData clip = clipboard.getPrimaryClip();
            if (clip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))  textToPaste = clip.getItemAt(0).getText().toString();
        }
        android.content.ClipData clip = android.content.ClipData.newPlainText("symbol",textToPaste+add);
        clipboard.setPrimaryClip(clip);
    }

    public static void copyToClipboard(String add, Context c) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("symbol",add);
        clipboard.setPrimaryClip(clip);
    }

    public static String getFromClipboard(Context c){
        String textToPaste= "";
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            ClipData clip = clipboard.getPrimaryClip();
            if (clip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))  textToPaste = clip.getItemAt(0).getText().toString();
        }
        return textToPaste;
    }
}
