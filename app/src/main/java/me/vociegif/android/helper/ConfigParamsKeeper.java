package me.vociegif.android.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigParamsKeeper {
    /**
     * 存储配置名称
     */
    public final static String KEEPERNAME = "configparams";
    private Context context;

    public ConfigParamsKeeper(Context context) {
        this.context = context;
    }

    /**
     * key 配置变量名
     */
    public int getIntValue(String key) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    /***
     * 设置配置值
     *
     * param key   配置变量名
     * param value 配置数值
     */
    public void setValue(String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * key 配置变量名
     */
    public float getFloatValue(String key) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        return preferences.getFloat(key, 0);
    }

    public long getLongValue(String key) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }

    /***
     * 设置配置值
     *
     * param key   配置变量名
     * param value 配置数值
     */
    public void setValue(String key, float value) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void setValue(String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * key 配置变量名
     */
    public boolean getBooleanValue(String key) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    /***
     * 设置配置值
     *
     * param key   配置变量名
     * param value 配置数值
     */
    public void setValue(String key, boolean flag) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(key, flag);
        editor.apply();
    }

    /***
     * 设置配置值
     *
     * param key   配置变量名
     * param value 配置数值
     */
    public void setValue(String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * key 配置变量名
     */
    public String getStringValue(String key) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public boolean hasKey(String key) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        return preferences.contains(key);
    }

    public int getIntValue(String key, int defaultvalue) {
        SharedPreferences preferences = context.getSharedPreferences(KEEPERNAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, defaultvalue);
    }

}