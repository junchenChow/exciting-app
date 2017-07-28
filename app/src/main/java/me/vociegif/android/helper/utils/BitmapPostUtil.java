package me.vociegif.android.helper.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by 时辰 on 16/6/29.
 */
public class BitmapPostUtil {
    /**
     * 图片旋转
     *
     * @param bmp    要旋转的图片
     * @param degree 图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    /**
     * 图片缩放
     *
     * @param bm
     * @param scale 值小于则为缩小，否则为放大
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bm, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }

    /**
     * 图片缩放
     *
     * @param bm
     * @param w  缩小或放大成的宽
     * @param h  缩小或放大成的高
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bm, int w, int h) {
        Bitmap BitmapOrg = bm;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
    }

    public static Bitmap rotateWithScale(Bitmap bm, float scale, float degree){
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }

}
