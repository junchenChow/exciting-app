package me.vociegif.android.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import me.vociegif.android.App;
import com.buhuavoice.app.BuildConfig;


/**
 * 接口信息类
 *
 * @author M.H
 */
public class Constants {
    public static final int LOGIN_PF_WEIBO = 2;
    public static final int LOGIN_PF_WEBCHAT = 3;
    public static final int LOGIN_PF_QQ = 4;

    public static final String ACCOUNT_SHAREPREFER = "logindata";
    public static final String STICKERPREFER = "stickerconfig";
    public static String uid;// 用户Uid
    public static String verify;// 用户验证码
    public static final String STICKER_EDITOR_MENU = "editor_menu";
    public static final String ALL_STICKER = "series";
    public static final String ALL_BG_STICKER_KEY = "bgserieskey";

    /**
     * URL前缀头
     */
    public final static String URLHEADER = "http://";


    public static String setAliyunImageUrl(String image_url) {
        if(TextUtils.isEmpty(image_url))
            return "";

        String url = image_url.replace(BuildConfig.OSS_PATH, BuildConfig.CDN_PATH + "/");
        return URLHEADER + url;
    }

    public static String splitUrl(String image_url)
    {
        return image_url.replace(BuildConfig.OSS_PATH, "");
    }

    public static String setHexUrl(String hexString) {
        return URLHEADER + BuildConfig.API_ROOT_URL + "/content/?" + hexString;
    }

    public static String getAppCannel() {
        String key = "UMENG_CHANNEL";
        String resultData = "";
        try {
            Context context = App.getDefault();
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    public static class requestType {
        public static final int LOGIN = 100;
        public static final int SERVER_ERROR = 1;                //服务器发送错误信息
        public static final int UPDATE = 2;                    //更新数据，尽在s->c中
        public static final int VERIFY_INVALID = 3;                //验证码失效，请重新登录

    }
}
