package me.vociegif.android.helper.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DecodeImageUtils {

    public static Bitmap decodeImage(byte[] data, Context context, Matrix matrix) {
        // 1.计算出来屏幕的宽高.

        int windowWidth = Utils.getDisplayMetrics((Activity) context).widthPixels;
        int windowHeight = Utils.getDisplayMetrics((Activity) context).heightPixels;
        // 2. 计算图片的宽高.
        Options opts = new Options();
        // 设置 不去真正的解析位图 不把他加载到内存 只是获取这个图片的宽高信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        int bitmapHeight = opts.outHeight;
        int bitmapWidth = opts.outWidth;
        if (bitmapHeight > windowHeight || bitmapWidth > windowWidth) {
            int scaleX = bitmapWidth / windowWidth;
            int scaleY = bitmapHeight / windowHeight;
            if (scaleX > scaleY) {// 按照水平方向的比例缩放
                opts.inSampleSize = scaleX;
            } else {// 按照竖直方向的比例缩放
                opts.inSampleSize = scaleY;
            }
        } else {// 如果图片比手机屏幕小 不去缩放了.
            opts.inSampleSize = 1;
        }
        // 让位图工厂真正的去解析图片
        opts.inJustDecodeBounds = false;
        Bitmap oldBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix,
                true);

        return newBitmap;
    }

    // 生成贴纸图片
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            Options opts = new Options();
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeByteArray(b, 0, b.length, opts);
        } else {
            return null;
        }
    }

    public static File byte2File(byte[] buf, String filePath, String fileName)
    {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try
        {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory())
            {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
            return file;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
