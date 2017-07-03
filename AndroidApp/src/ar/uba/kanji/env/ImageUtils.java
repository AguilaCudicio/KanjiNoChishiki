
package ar.uba.kanji.env;
import android.graphics.Matrix;

/**
 * Utility class for manipulating images.
 **/
public class ImageUtils {


  // This value is 2 ^ 18 - 1, and is used to clamp the RGB values before their ranges
  // are normalized to eight bits.
  static final int kMaxChannelValue = 262143;

  public static void convertYUV420ToARGB8888(
      byte[] yData,
      byte[] uData,
      byte[] vData,
      int width,
      int height,
      int yRowStride,
      int uvRowStride,
      int uvPixelStride,
      int[] out) {

    int i = 0;
    for (int y = 0; y < height; y++) {
      int pY = yRowStride * y;
      int uv_row_start = uvRowStride * (y >> 1);
      int pUV = uv_row_start;
      int pV = uv_row_start;

      for (int x = 0; x < width; x++) {
        int uv_offset = pUV + (x >> 1) * uvPixelStride;
        out[i++] =
            YUV2RGB(
                convertByteToInt(yData, pY + x),
                convertByteToInt(uData, uv_offset),
                convertByteToInt(vData, uv_offset));
      }
    }
  }

  private static int convertByteToInt(byte[] arr, int pos) {
    return arr[pos] & 0xFF;
  }

  private static int YUV2RGB(int nY, int nU, int nV) {
    nY -= 16;
    nU -= 128;
    nV -= 128;
    if (nY < 0) nY = 0;

    // This is the floating point equivalent. We do the conversion in integer
    // because some Android devices do not have floating point in hardware.
    // nR = (int)(1.164 * nY + 2.018 * nU);
    // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
    // nB = (int)(1.164 * nY + 1.596 * nV);

    final int foo = 1192 * nY;
    int nR = foo + 1634 * nV;
    int nG = foo - 833 * nV - 400 * nU;
    int nB = foo + 2066 * nU;

    nR = Math.min(kMaxChannelValue, Math.max(0, nR));
    nG = Math.min(kMaxChannelValue, Math.max(0, nG));
    nB = Math.min(kMaxChannelValue, Math.max(0, nB));

    return 0xff000000 | ((nR << 6) & 0x00ff0000) | ((nG >> 2) & 0x0000FF00) | ((nB >> 10) & 0xff);
  }

}
