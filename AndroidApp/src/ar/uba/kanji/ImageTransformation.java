package ar.uba.kanji;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class ImageTransformation {

    public static Boolean needsToBeInverted(float[] floatValues){
        double size = Math.sqrt( floatValues.length);
        double averageInBorder = 0;
        double averageInEntireImage = 0;
        for (int i = 0; i < floatValues.length; ++i) {
            averageInEntireImage += floatValues[i];
            //first line of the image
            if (i<size) {
                averageInBorder += floatValues[i];
            }
            // last line of the image
            else if (i>( floatValues.length-size )) {
                averageInBorder += floatValues[i];
            }
            else {
                if (i%size == 0 || (i-1)%size == 0 )  {
                    averageInBorder += floatValues[i];
                }
            }

        }
        averageInBorder = averageInBorder / (size*4 - 4);
        averageInEntireImage = averageInEntireImage / floatValues.length;

        if (averageInBorder < averageInEntireImage)  return true;
        else return false;
    }

    public static float[] invertImageColor(float[] floatValues){
        float[] result = new float[floatValues.length];
        for (int i = 0; i < floatValues.length; ++i) {
            result[i] =  Math.abs(255-floatValues[i]);
        }
        return result;
    }

    public static float[] getGrayScaleTransformation(int[] intValues,int imageMean,float imageStd, int inputSize) {
        float[] floatValues = new float[inputSize * inputSize];
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            float r= (((val >> 16) & 0xFF) - imageMean) / imageStd;
            float g = (((val >> 8) & 0xFF) - imageMean) / imageStd;
            float b = ((val & 0xFF) - imageMean) / imageStd;
            floatValues[i ] =  0.2989f * r + 0.5870f * g + 0.1140f * b;
        }

        return floatValues;
    }

    public static Bitmap adjustContrast(Bitmap src, double value)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }
}
