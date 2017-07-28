package me.vociegif.android.helper.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import Decoder.BASE64Decoder;

/**
 * Created by 时辰 on 16/6/22.
 */
public class ZipUtil {


    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     *
     * @throws Exception
     */
    public static boolean upZipFile(File zipFile, String folderPath) {
        //public static void upZipFile() throws Exception{
        try {
            ZipFile zfile = new ZipFile(zipFile);
            Enumeration zList = zfile.entries();
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                if (ze.isDirectory()) {
                    Log.d("upZipFile", "ze.getName() = " + ze.getName());
                    String dirstr = folderPath + ze.getName();
                    //dirstr.trim();
                    dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                    Log.d("upZipFile", "str = " + dirstr);
                    File f = new File(dirstr);
                    f.mkdir();
                    continue;
                }
                Log.d("upZipFile", "ze.getName() = " + ze.getName());
                OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
                InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
                int readLen;
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
                is.close();
                os.close();
            }
            zfile.close();
            Log.d("upZipFile", "finishssssssssssssssssssss");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        String lastDir = baseDir;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                lastDir += (dirs[i] + "/");
                File dir = new File(lastDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                    Log.d("getRealFileName", "create dir = " + (lastDir + "/" + dirs[i]));
                }
            }
            File ret = new File(lastDir, dirs[dirs.length - 1]);
            Log.d("upZipFile", "2ret = " + ret);
            return ret;
        } else {
            return new File(baseDir, absFileName);
        }
    }


    public static String parserZipUrl(String name, String zipPath, String filePath) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(zipPath));
            StringBuilder builder = new StringBuilder();
            byte[] buffer = new byte[1024 * 1024];
            int length = -1;
            while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                builder.append(new String(buffer, 0, length));
            }
            String stickerContent = builder.toString();
            String md5 = CryptUtil.encryptMD5(stickerContent);
            String strResult = CryptUtil.MD51(md5, stickerContent);
            byte[] bytes = (new BASE64Decoder()).decodeBuffer(strResult);

            return DecodeImageUtils.byte2File(bytes, filePath, name + ".zip").getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
