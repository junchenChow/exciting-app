package me.vociegif.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import me.vociegif.android.helper.ConfigParamsKeeper;

import java.util.HashMap;
import java.util.List;

public class ConfigApplication extends MultiDexApplication implements ActivityLifecycleCallbacks {

    public final static int ACTIVITY_ONCREATE = 1;
    public final static int ACTIVITY_ONSTART = 2;
    public final static int ACTIVITY_ONRESUME = 3;
    public final static int ACTIVITY_ONPAUSE = 4;
    public final static int ACTIVITY_ONSTOP = 5;
    public final static int ACTIVITY_ONSAVEINSTANCESTATE = 6;
    public final static int ACTIVITY_ONDESTROY = 7;
    private ConfigParamsKeeper configkeeper;
    private HashMap<String, Integer> activityHashMap;
    private String mainactivity, topactivity;

    public static final String APP_ID = "2882303761517405098";
    public static final String APP_KEY = "5251740595098";
    public static final String TAG = "me.vociegif.android";

//    private static MiPushMessageReceiver.ReciverHandler handler = null;



    @Override
    public void onCreate() {
        super.onCreate();
        configkeeper = new ConfigParamsKeeper(this.getApplicationContext());

        // 注册push服务，注册成功后会向MessageReceiver发送广播，
        // 可以从MessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
//        if (shouldInit()) {
//            MiPushClient.registerPush(this, APP_ID, APP_KEY);
//        }

//        if (handler == null)
//            handler = new MiPushMessageReceiver.ReciverHandler();

    }

    /**
     * 启动activity生命周期监测
     *
     * @param
     */
    protected void enableActivityLifeCallback(String mainactivity) {
        this.mainactivity = mainactivity;
        activityHashMap = new HashMap<String, Integer>();
        this.registerActivityLifecycleCallbacks(this);
    }

    protected void disenableActivityLifeCallback() {
        this.unregisterActivityLifecycleCallbacks(this);
    }

    protected boolean hasKey(String key) {
        return configkeeper.hasKey(key);
    }

    /**
     * @key 配置变量名
     */
    public int getIntValue(String key) {
        return configkeeper.getIntValue(key);
    }

    public int getIntValue(String key, int defaultvalue) {
        return configkeeper.getIntValue(key, defaultvalue);
    }

    /***
     * 设置配置值
     *
     * @param key   配置变量名
     * @param value 配置数值
     */
    public void setValue(String key, int value) {
        configkeeper.setValue(key, value);
    }

    /**
     * @key 配置变量名
     */
    public long getLongValue(String key) {
        return configkeeper.getLongValue(key);
    }

    /***
     * 设置配置值
     *
     * @param key   配置变量名
     * @param value 配置数值
     */
    public void setValue(String key, long value) {
        configkeeper.setValue(key, value);
    }

    /**
     * @key 配置变量名
     */
    public float getFloatValue(String key) {
        return configkeeper.getFloatValue(key);
    }

    /***
     * 设置配置值
     *
     * @param key   配置变量名
     * @param value 配置数值
     */
    public void setValue(String key, float value) {
        configkeeper.setValue(key, value);
    }

    /**
     * @key 配置变量名
     */
    public boolean getBooleanValue(String key) {
        return configkeeper.getBooleanValue(key);
    }

    /***
     * 设置配置值
     *
     * @param key   配置变量名
     * @param value 配置数值
     */
    public void setValue(String key, boolean value) {
        configkeeper.setValue(key, value);
    }

    /***
     * 设置配置值
     *
     * @param key   配置变量名
     * @param value 配置数值
     */
    public void setValue(String key, String value) {
        configkeeper.setValue(key, value);
    }

    /**
     * @key 配置变量名
     */
    public String getStringValue(String key) {
        return configkeeper.getStringValue(key);
    }

    private void setLife(Activity activity, int life) {
        activityHashMap.put(activity.getClass().getCanonicalName(), life);
        onActivityLifeChange(activityHashMap);
        if (life == ACTIVITY_ONRESUME) {
            this.topactivity = activity.getClass().getCanonicalName();
        } else if (life == ACTIVITY_ONSTOP) {
            int lastlife = activityHashMap.get(this.topactivity);
            switch (lastlife) {
                case ACTIVITY_ONSTOP:
                    // 程序进入后台

                    break;
                case ACTIVITY_ONDESTROY:
                    // 程序退出

                    break;
                default:
                    break;
            }
            if (!isRunningForeground()) {
                onApplicationGoback();
            }
        } else if (life == ACTIVITY_ONDESTROY) {
            // 程序退出事件
            // Logger.d("there is a life destroy "
            // + activity.getClass().getCanonicalName());
            if (!isRunningForeground()) {
                onApplicationExit();
            }
        }
    }

    public boolean isRunningForeground() {
        String packageName = getPackageName(this);
        String topActivityClassName = getTopActivityName(this);
        System.out.println("packageName=" + packageName + ",topActivityClassName=" + topActivityClassName);
        if (packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName)) {
            System.out.println("---> isRunningForeGround");
            return true;
        } else {
            System.out.println("---> isRunningBackGround");
            return false;
        }
    }

    public String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }

    public String getPackageName(Context context) {
        String packageName = context.getPackageName();
        return packageName;
    }

    /**
     * 生命周期发生变化
     *
     * @param activityHashMap
     */
    protected void onActivityLifeChange(HashMap<String, Integer> activityHashMap) {

    }

    /**
     * 程序进入后台
     */
    protected void onApplicationGoback() {

    }

    /**
     * 程序退出
     */
    protected void onApplicationExit() {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.v("onActivityCreated", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONCREATE);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.v("onActivityStarted", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONSTART);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.v("onActivityResumed", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONRESUME);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.v("onActivityPaused", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONPAUSE);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.v("onActivityStopped", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONSTOP);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.v("onAtySaveInstanceState", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONSAVEINSTANCESTATE);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.v("onActivityDestroyed", activity.getClass().getCanonicalName());
        setLife(activity, ACTIVITY_ONDESTROY);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

//    public static MiPushMessageReceiver.ReciverHandler getHandler() {
//        return handler;
//    }

}
