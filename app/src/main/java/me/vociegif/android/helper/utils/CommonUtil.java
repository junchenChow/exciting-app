package me.vociegif.android.helper.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.buhuavoice.app.R;
import me.vociegif.android.App;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by life on 14-7-29.
 */
public class CommonUtil {

    private static long lastClickTime;

    private static long TIME = 500;
    private static long exitTime = 0;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    static boolean isFastShake(int shakeTimer) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < shakeTimer) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    public static boolean tryClickBackAgain(int keyCode, KeyEvent event, Context context) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.show("再按一次退出 " + context.getResources().getString(R.string.app_name));
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return false;
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }

    /**
     * 获取运行时内存
     *
     * @return
     */
    public static long getMaxMemory() {
//        Log.i("memory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + "");
        return Runtime.getRuntime().totalMemory() / 1024;
    }

    public static DisplayMetrics metric;

    /**
     * 得到屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (metric == null) {
            metric = new DisplayMetrics();
        }
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * 得到屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (metric == null) {
            metric = new DisplayMetrics();
        }
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取屏幕dpi
     *
     * @return
     */
    public static int getScreenDpi(Activity context) {
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.densityDpi;
    }

    /**
     * 获取屏幕dpi
     *
     * @return
     */
    public static float getScreenDensity(Activity context) {
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.density;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 查询手机内非系统应用
     *
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            // 判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * 获取版本号和版本次数
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context, int type) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            if (type == 1) {
                return String.valueOf(pi.versionCode);
            } else {
                return pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getVersion() {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = App.getDefault().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(App.getDefault().getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "1.0";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 通过Service的类名来判断是否启动某个服务
     */
    public static boolean messageServiceIsStart(
            List<ActivityManager.RunningServiceInfo> mServiceList,
            String className) {
        for (int i = 0; i < mServiceList.size(); i++) {
            if (className.equals(mServiceList.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取本机号码
     */
    public static String getSelfNumber(Context context) {
        try {
            TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return phoneMgr.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName：应用包名
     * @return
     */
    private boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


    public static String getUrl(String baseUrl, HashMap<String, String> params) {
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            baseUrl += sb.toString();
        }
        return baseUrl + " ";
    }


    /**
     * 获取设备序列号
     *
     * @return Str
     */
    public static String getDeviceUUID() {
        final TelephonyManager tm = (TelephonyManager) App.getDefault().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(App.getDefault().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;

    }


    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     *               <p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *               、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
     *               <p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p>
     *               <p>电信的号段：133、153、180（未启用）、177,189</p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobile(String mobile) {
//        String regex = "(\\+\\d+)?1[34578]\\d{9}$";
        String regex = "(\\+\\d+)?1\\d{10}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 验证用户名
     *
     * @param userName 用户名
     * @return
     */
    public static boolean isUserNmaeRule(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return false;
        }
        String reges = "^(?!_)(?!.*?_$)(?!-)(?!.*?-$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
//      String regex = " ^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}+$";
        return Pattern.matches(reges, userName);
    }

    /**
     * @param mContext
     * @return
     */
    public static boolean isServiceRunning(Context mContext, Class classz) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(classz.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static boolean isActivityRunning(Context context, Class classz) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(classz.getName()) || info.baseActivity.getPackageName().equals(classz.getName())) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    public boolean appIsRunning(Context ctx, String packageName) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        if (runningAppProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo.processName.startsWith(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测某Activity是否在当前Task的栈顶
     */
    public static boolean isTopActivity(Context context, Class classz) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(classz.getName());
    }


    public static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext(packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                                | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }

    public static Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        // MainActivity完整名
        String mainAct = null;
        // 根据包名寻找MainActivity
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        List<ResolveInfo> list = pkgMag.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    /**
     * 跳转第三方应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean openPackage(Context context, String packageName) {
        Context pkgContext = getPackageContext(context, packageName);
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        if (pkgContext != null && intent != null) {
            pkgContext.startActivity(intent);
            return true;
        }
        return false;
    }


    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取android系统版本号
     */
    public static String getOSVersion() {
        String release = android.os.Build.VERSION.RELEASE; // android系统版本号
        release = "android" + release;
        return release;
    }

}