package me.vociegif.android.helper.utils;

/**
 *
 */

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import me.vociegif.android.helper.ConstantsPath;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FileUtil {

    //    public final static String DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/moho/";
    public final static String DIR_GIF = ".Gif/";
    public final static String DIR_FRAME = ".FrameZip/";
    public final static String DIR_STICKERS = ".Stickers/";
    public final static String STICKER_BG_NAME = "stickersBackground";


    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File file = null;

        if (uniqueName != null)
            file = new File(cachePath + File.separator + uniqueName);
        else
            file = new File(cachePath);

        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static String getAppFilePath(String uniqueName) {
        String cachePath = ConstantsPath.getStickerFileDir() + uniqueName;
        try {
            createFolder(cachePath);
            Log.i("createPaths", cachePath);
            return cachePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将源文件的数据写入到目标文件中，
     * 不会检查源文件是否存在，
     * 若目标文件存在则直接写入，
     * 否则创建目标文件后再进行写入。
     *
     * @param srcPath
     * @param desPath
     */
    public static void copyFile(String srcPath, String desPath) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(srcPath);
            out = new FileOutputStream(desPath);
            byte[] bt = new byte[1024];
            int count;
            while ((count = in.read(bt)) > 0) {
                out.write(bt, 0, count);
            }
            in.close();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 复制文件，若文件存在则替换该文件。
     *
     * @param srcPath
     * @param desPath
     * @throws Exception
     */
    public static void copyAndReplaceFile(String srcPath, String desPath) throws Exception {
        srcPath = separatorReplace(srcPath);
        desPath = separatorReplace(desPath);
        File srcFile = new File(srcPath);
        if (!srcFile.isFile()) {
            throw new Exception("source file not found!");
        }
        copyFile(srcPath, desPath);
    }

    /**
     * 复制文件，若文件已存在则不进行替换。
     *
     * @param srcPath
     * @param desPath
     * @throws Exception
     */
    public static void copyAndNotReplaceFile(String srcPath, String desPath) throws Exception {
        srcPath = separatorReplace(srcPath);
        desPath = separatorReplace(desPath);
        File srcFile = new File(srcPath);
        File desFile = new File(desPath);
        if (!srcFile.isFile()) {
            throw new Exception("source file not found!");
        }
        if (desFile.isFile()) {
            return;
        }
        copyFile(srcPath, desPath);
    }

    /**
     * 移动文件，若文件存在则替换该文件。
     *
     * @param srcPath
     * @param desPath
     * @throws Exception
     */
    public static void moveAndReplaceFile(String srcPath, String desPath) throws Exception {
        srcPath = separatorReplace(srcPath);
        desPath = separatorReplace(desPath);
        copyAndReplaceFile(srcPath, desPath);
        deleteFile(srcPath);
    }

    /**
     * 移动文件，若文件存在则不进行替换。
     *
     * @param srcPath
     * @param desPath
     * @throws Exception
     */
    public static void moveAndNotReplaceFile(String srcPath, String desPath) throws Exception {
        srcPath = separatorReplace(srcPath);
        desPath = separatorReplace(desPath);
        copyAndNotReplaceFile(srcPath, desPath);
        deleteFile(srcPath);
    }

    /**
     * 复制并合并文件夹，
     * 不会替换目标文件夹中已经存在的文件或文件夹。
     *
     * @param srcPath
     * @param desPath
     * @throws Exception
     */
    public static void copyAndMergerFolder(String srcPath, String desPath) throws Exception {
        srcPath = separatorReplace(srcPath);
        desPath = separatorReplace(desPath);
        File folder = getFolder(srcPath);
        createFolder(desPath);
        File[] files = folder.listFiles();
        for (File file : files) {
            String src = file.getAbsolutePath();
            String des = desPath + File.separator + file.getName();
            if (file.isFile()) {
                copyAndNotReplaceFile(src, des);
            } else if (file.isDirectory()) {
                copyAndMergerFolder(src, des);
            }
        }
    }

    /**
     * 复制并替换文件夹，
     * 将目标文件夹完全替换成源文件夹，
     * 目标文件夹原有的资料会丢失。
     *
     * @param srcPath
     * @param desPath
     * @throws Exception
     */
    public static void copyAndReplaceFolder(String srcPath, String desPath) throws Exception {
        srcPath = separatorReplace(srcPath);
        desPath = separatorReplace(desPath);
        File folder = getFolder(srcPath);
        createNewFolder(desPath);
        File[] files = folder.listFiles();
        for (File file : files) {
            String src = file.getAbsolutePath();
            String des = desPath + File.separator + file.getName();
            if (file.isFile()) {
                copyAndReplaceFile(src, des);
            } else if (file.isDirectory()) {
                copyAndReplaceFolder(src, des);
            }
        }
    }

    /**
     * 合并文件夹后，将源文件夹删除。
     *
     * @param srcPath
     * @param desPath
     * @throws Exception
     */
    public static void moveAndMergerFolder(String srcPath, String desPath) throws Exception {
        srcPath = separatorReplace(srcPath);
        desPath = separatorReplace(desPath);
        copyAndMergerFolder(srcPath, desPath);
        deleteFolder(srcPath);
    }

    /**
     * 替换文件夹后，将源文件夹删除。
     *
     * @param srcPath
     * @param desPath
     * @throws Exception
     */
    public static void moveAndReplaceFolder(String srcPath, String desPath) throws Exception {
        srcPath = separatorReplace(srcPath);
        desPath = separatorReplace(desPath);
        copyAndReplaceFolder(srcPath, desPath);
        deleteFolder(srcPath);
    }

    /**
     * 创建文件夹，如果文件夹存在则不进行创建。
     *
     * @param path
     * @throws Exception
     */
    public static void createFolder(String path) throws Exception {
        path = separatorReplace(path);
        File folder = new File(path);
        if (folder.isDirectory()) {
            return;
        } else if (folder.isFile()) {
            deleteFile(path);
        }
        folder.mkdirs();
    }

    /**
     * 创建一个新的文件夹，如果文件夹存在，则删除后再创建。
     *
     * @param path
     * @throws Exception
     */
    public static void createNewFolder(String path) throws Exception {
        path = separatorReplace(path);
        File folder = new File(path);
        if (folder.isDirectory()) {
            deleteFolder(path);
        } else if (folder.isFile()) {
            deleteFile(path);
        }
        folder.mkdirs();
    }

    /**
     * 创建一个文件，如果文件存在则不进行创建。
     *
     * @param path
     * @throws Exception
     */
    public static File createFile(String path) {
        path = separatorReplace(path);
        File file = new File(path);
        try {
            if (file.isFile()) {
                return file;
            } else if (file.isDirectory()) {
                deleteFolder(path);
            }
            return createFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 创建一个新文件，如果存在同名的文件或文件夹将会删除该文件或文件夹，
     * 如果父目录不存在则创建父目录。
     *
     * @param path
     * @throws Exception
     */
    public static File createNewFile(String path) throws Exception {
        path = separatorReplace(path);
        File file = new File(path);
        if (file.isFile()) {
            deleteFile(path);
        } else if (file.isDirectory()) {
            deleteFolder(path);
        }
        return createFile(file);
    }

    /**
     * 分隔符替换
     * window下测试通过
     *
     * @param path
     * @return
     */
    public static String separatorReplace(String path) {
        return path.replace("\\", "/");
    }

    /**
     * 创建文件及其父目录。
     *
     * @param file
     * @throws Exception
     */
    public static File createFile(File file) throws Exception {
        createParentFolder(file);
        if (!file.createNewFile()) {
            throw new Exception("create file failure!");
        }
        return file;
    }

    /**
     * 创建父目录
     *
     * @param file
     * @throws Exception
     */
    private static void createParentFolder(File file) throws Exception {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new Exception("create parent directory failure!");
            }
        }
    }

    /**
     * 根据文件路径删除文件，如果路径指向的文件不存在或删除失败则抛出异常。
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static void deleteFile(String path) throws Exception {
        path = separatorReplace(path);
        File file = getFile(path);
        if (!file.delete()) {
            throw new Exception("delete file failure");
        }
    }

    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    /**
     * 删除指定目录中指定前缀和后缀的文件。
     *
     * @param dir
     * @param prefix
     * @param suffix
     * @throws Exception
     */
    public static void deleteFile(String dir, String prefix, String suffix) throws Exception {
        dir = separatorReplace(dir);
        File directory = getFolder(dir);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.startsWith(prefix) && fileName.endsWith(suffix)) {
                    deleteFile(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 根据路径删除文件夹，如果路径指向的目录不存在则抛出异常，
     * 若存在则先遍历删除子项目后再删除文件夹本身。
     *
     * @param path
     * @throws Exception
     */
    public static void deleteFolder(String path) throws Exception {
        path = separatorReplace(path);
        File folder = getFolder(path);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteFolder(file.getAbsolutePath());
            } else if (file.isFile()) {
                deleteFile(file.getAbsolutePath());
            }
        }
        folder.delete();
    }

    /**
     * 查找目标文件夹下的目标文件
     *
     * @param dir
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static File searchFile(String dir, String fileName) throws FileNotFoundException {
        dir = separatorReplace(dir);
        File f = null;
        File folder = getFolder(dir);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                f = searchFile(file.getAbsolutePath(), fileName);
                if (f != null) {
                    break;
                }
            } else if (file.isFile()) {
                if (file.getName().equals(fileName)) {
                    f = file;
                    break;
                }
            }
        }
        return f;
    }

    /**
     * 获得文件类型。
     *
     * @param path：文件路径
     * @return
     * @throws FileNotFoundException
     */
    public static String getFileType(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        File file = getFile(path);
        String fileName = file.getName();
        String[] strs = fileName.split("\\.");
        if (strs.length < 2) {
            return "unknownType";
        }
        return strs[strs.length - 1];
    }

    /**
     * 根据文件路径，获得该路径指向的文件的大小。
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static long getFileSize(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        File file = getFile(path);
        return file.length();
    }

    /**
     * 根据文件夹路径，获得该路径指向的文件夹的大小。
     * 遍历该文件夹及其子目录的文件，将这些文件的大小进行累加，得出的就是文件夹的大小。
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static long getFolderSize(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        long size = 0;
        File folder = getFolder(path);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                size += getFolderSize(file.getAbsolutePath());
            } else if (file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    /**
     * 通过路径获得文件，
     * 若不存在则抛异常，
     * 若存在则返回该文件。
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static File getFile(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        File file = new File(path);
        Log.i("filePath", file.getAbsolutePath());
        if (!file.isFile()) {
            throw new FileNotFoundException("file not found!");
        }
        return file;
    }

    /**
     * 通过路径获得文件夹，
     * 若不存在则抛异常，
     * 若存在则返回该文件夹。
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static File getFolder(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        File folder = new File(path);
        if (!folder.isDirectory()) {
            throw new FileNotFoundException("folder not found!");
        }
        return folder;
    }

    /**
     * 获得文件最后更改时间。
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static Date getFileLastModified(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        File file = getFile(path);
        return new Date(file.lastModified());
    }

    /**
     * 获得文件夹最后更改时间。
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static Date getFolderLastModified(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        File folder = getFolder(path);
        return new Date(folder.lastModified());
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return 返回文件扩展名
     */
    public static String getExtensionName(String filename) throws FileNotFoundException {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 检查SD卡是否存在
     *
     * @return
     */
    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    public static void inputStreamToFile(InputStream ins, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int bytesRead;
        byte[] buffer = new byte[1024];
        try {
            while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
                if (os != null) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检索指定文件夹下的所有文件
     *
     * @param folderName 文件夹名
     * @return
     */
    public static List<String> getFileNamesFromFolder(String folderName) {
        List<String> list = new ArrayList<String>();
        File file = new File(folderName);
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            if (null != fileArray && 0 != fileArray.length) {
                for (int i = 0; i < fileArray.length; i++) {
                    list.add(fileArray[i].getAbsolutePath());
                }
            }
        }
        return list;
    }

    /**
     * 获取文件夹大小
     *
     * @param directory
     * @return
     */
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    /**
     * 获取SD卡可用空间
     *
     * @return
     */
    public static long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize / 1024 / 1024;
        //availableBlocks * blockSize 单位为字节byte
        //(availableBlocks * blockSize)/1024      KIB 单位
        //(availableBlocks * blockSize)/1024 /1024  MIB单位
    }

    public static void saveBitmap(String path, Bitmap bitmap) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询图片信息
     *
     * @param imageUri
     * @param activity
     * @return
     */
    public static File convertImageUriToFile(Uri imageUri, Activity activity) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
            cursor = activity.getContentResolver().query(imageUri,
                    proj, // Which columns to return
                    null,       // WHERE clause; which rows to return (all rows)
                    null,       // WHERE clause selection arguments (none)
                    null);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int orientation_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
            if (cursor.moveToFirst()) {
                String orientation = cursor.getString(orientation_ColumnIndex);
                return new File(cursor.getString(file_ColumnIndex));
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void savaPicExif(Context context, String fileName) {
        try {
            ExifInterface exifInterface = new ExifInterface(fileName);
            exifInterface.setAttribute(ExifInterface.TAG_DATETIME, System.currentTimeMillis() + "");
//            if (CommonUtil.checkNetState(context) && BaseActivity.location != null && BaseActivity.location.getLatitude() != 0 && BaseActivity.location.getLongitude() != 0) {
//                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, BaseActivity.location.getLatitude() + "");
//                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, BaseActivity.location.getLongitude() + "");
//            }
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片文件的宽高,宽高封装在Point的X和Y中
     *
     * @param filePath 文件路径
     */
    public static Point getPicWH(String filePath) {
        Point point = new Point();
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
            point.x = options.outWidth;
            point.y = options.outHeight;
            options.inJustDecodeBounds = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return point;
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }


    private void saveBitmap(Context context, Bitmap bitmap, String bitName) throws IOException {
        String name = "/VoiceApp/Stickers" + bitName;
        String path = getDiskCacheDir(context, name).getAbsolutePath();
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readRawResource(Context context, int fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getResources().openRawResource(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }
}

