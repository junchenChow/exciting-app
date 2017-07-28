package me.vociegif.android.helper;

import android.os.Environment;

import me.vociegif.android.App;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConstantsPath {

    public static final int COMMONBUDDLESTICKER = 1;

    public static final int COMMONTEXTSTICKER = 2;

    public static final int STICKER_TYPE_FOREGROUND = 3;

    public static final String FOOTER_URL = "gateway.php";

    public static final String VOICE_URL = "gateway_smart2.php";
    /**
     * DP
     */
    public static final float DEFAULTINPUTTEXTSIZE = 16;
    public static final float DEFAULTRECTSIZE = 2;

    /**
     * 默认风格颜色
     */
    public static final int COMMONCOLOR = 0xff725DE0;

    /**
     * voiceapp 请求code
     */
    public static final int VERSION_CHECK = 200;              //检查版本信息
    public static final int SMART_EMOTION = 201;              //智能表情

    /***********************************************************************************************/
    /**
     * 获取贴纸在本地的目录
     *
     * @return
     */
    public static String getCachePath() {
        return App.getDefault().getExternalCacheDir().getAbsolutePath();
    }

    public static String getStickerFileDir() {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = App.getDefault().getExternalFilesDir(null).getAbsolutePath();
        } else {
            cachePath = App.getDefault().getFilesDirPath();
        }
        return cachePath + "/";
    }


    public static String getDCIMPath() {
        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/buhuaComic/";
        File file = new File(dcimPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString().trim()
                + ".jpg";

        return dcimPath + fileName;
    }

    public static String getGLYPath(String footer) {
        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/buhuaComic/";
        File file = new File(dcimPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString().trim()
                + footer;

        return dcimPath + fileName;
    }
}
