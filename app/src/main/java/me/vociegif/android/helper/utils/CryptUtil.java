package me.vociegif.android.helper.utils;

import android.annotation.SuppressLint;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
@SuppressLint("NewApi")
public class CryptUtil {

    public static byte[] decompressData(String encdata) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InflaterOutputStream zos = new InflaterOutputStream(bos);
        byte[] bs = decryptBASE64(encdata);
        zos.write(bs);
        zos.close();
        return bos.toByteArray();
    }

    public static String decompressNetData(String encdata) throws Exception {
        return new String(decompressData(encdata));
    }

    public static String compressData(String data) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DeflaterOutputStream zos = new DeflaterOutputStream(bos);
            zos.write(data.getBytes());
            zos.close();
            return encryptBASE64(bos.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            return "ZIP_ERR";
        }
    }

    public static String MD51(String s2, String s) {
        char hexDigits[] = s2.toCharArray();
        StringBuilder newStr = new StringBuilder();
        int leng = 2 * hexDigits.length;
        String ss = s.substring(0, leng);
        String ss1 = s.substring(leng, s.length());
        char[] c = ss.toCharArray();
        StringBuilder s1 = new StringBuilder();
        for (int i = 0; i < ss.length(); i++) {
            if (i % 2 == 0) {
                s1.append(c[i]);
            }
        }
        newStr.append(s1).append(ss1);
        return newStr.toString();
    }

    public static String MD5(String s1, String s) {
        char hexDigits[] = s1.toCharArray();
        StringBuilder newStr = new StringBuilder();
        char[] c = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            if (i < hexDigits.length) {
                newStr.append(String.valueOf(String.valueOf(c[i]) + hexDigits[i]));
            } else {
                newStr.append(String.valueOf(c[i]));
            }
        }
        return newStr.toString();
    }

    public static String encryptMD5(String string) {
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    private static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    private static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }
}
