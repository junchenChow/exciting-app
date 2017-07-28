package me.vociegif.android.helper.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.vociegif.android.App;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class FileToPhotoUtils {

    private static FileToPhotoUtils instance = null;
    private static final String APP_DIR = "Moho";
    private static final String TEMP_DIR = "Moho/.TEMP";
    public static final String TEMP_NAME = ".TEMP/VOICE_IMG";
    public static final String IMAGE_TYPE_JPG = ".jpg";
    public static final String IMAGE_TYPE_GIF = ".gif";

    public static FileToPhotoUtils getInstance() {
        if (instance == null) {
            synchronized (FileToPhotoUtils.class) {
                if (instance == null) {
                    instance = new FileToPhotoUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 保存图像到本地
     *
     * @param bm
     * @return
     */
    public static String saveBitmapToLocal(Bitmap bm, File file) {
        String path = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            path = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }

    /**
     * 保存图像到本地
     *
     * @param bm
     * @return
     */
    public static String saveStickersBgToPath(Bitmap bm, File tempFile) {
        String path = null;
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            path = tempFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }

    /**
     * @param prefix
     * @param extension
     * @return
     * @throws IOException
     */
    public File createTempFile(String prefix, String extension)
            throws IOException {
        File file = new File(getAppDirPath() + ".TEMP/" + prefix
                + System.currentTimeMillis() + extension);
        file.createNewFile();
        return file;
    }


    /**
     * @param prefix
     * @param extension
     * @return
     * @throws IOException
     */
    public File createTempFile(String prefix, int index, String extension )
            throws IOException {
        File file = new File(getAppDirPath() + ".TEMP/" + prefix
                + index + extension);
        file.createNewFile();
        return file;
    }

    /**
     * 得到当前应用程序内容目录,外部存储空间不可用时返回null
     *
     * @return
     */
    public String getAppDirPath() {
        String path = null;
        if (getLocalPath() != null) {
            path = getLocalPath() + APP_DIR + "/";
        }
        return path;
    }

    /**
     * 得到当前app的目录
     *
     * @return
     */
    private static String getLocalPath() {
        String sdPath = null;
        sdPath = App.getDefault().getFilesDir().getAbsolutePath() + "/";
        return sdPath;
    }

    /**
     * 检查sd卡是否就绪并且可读写
     *
     * @return
     */
    public boolean isSDCanWrite() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)
                && Environment.getExternalStorageDirectory().canWrite()
                && Environment.getExternalStorageDirectory().canRead()) {
            return true;
        } else {
            return false;
        }
    }

    private FileToPhotoUtils() {
        // 创建应用内容目录
        if (isSDCanWrite()) {
            creatSDDir(APP_DIR);
            creatSDDir(TEMP_DIR);
        }
    }

    /**
     * 在SD卡根目录上创建目录
     *
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(getLocalPath() + dirName);
        dir.mkdirs();
        return dir;
    }


}
