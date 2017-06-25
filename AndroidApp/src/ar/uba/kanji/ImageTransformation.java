package ar.uba.kanji;

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
}
