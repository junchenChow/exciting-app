package me.vociegif.android.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import me.vociegif.android.App;
import me.vociegif.android.mvp.di.ContextLevel;
import me.vociegif.android.mvp.vo.StickerContentFull;
import me.vociegif.android.mvp.vo.StickerFont;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.inject.Inject;


/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class MoHoSharedPreferences {
    private static final String MOHO_CACHE_DIR_DIRECTORY = "moho_voice_cache_directory";
    private SharedPreferences mSharedPreferences;
    private static MoHoSharedPreferences mDefaultInstance;
    private static final String SPLASH_URL = "splash_url";

    @Inject
    public MoHoSharedPreferences(@ContextLevel(ContextLevel.APPLICATION) Context context) {
        mSharedPreferences = context.getSharedPreferences(MOHO_CACHE_DIR_DIRECTORY, Context.MODE_PRIVATE);
    }

    public static MoHoSharedPreferences getDefault() {
        if (mDefaultInstance == null) {
            synchronized (MoHoSharedPreferences.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new MoHoSharedPreferences(App.getDefault());
                }
            }
        }
        return mDefaultInstance;
    }

    public void saveSticker(String key, StickerContentFull sticker) {
        Gson gson = new Gson();
        saveStickerToJson(key, gson.toJson(sticker));
    }

    private void saveStickerToJson(String key, String value) {
        try {
            value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public StickerContentFull getStickerContentFullById(String key) {
        String json = "";
        try {
            json = URLDecoder.decode(mSharedPreferences.getString(key, ""), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(json, StickerContentFull.class);
    }

    public void saveStickerFont(String key, StickerFont sticker) {
        Gson gson = new Gson();
        saveStickerToJson(key, gson.toJson(sticker));
    }


    public StickerFont getStickerFontById(String key) {
        String json = "";
        try {
            json = URLDecoder.decode(mSharedPreferences.getString(key, ""), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(json, StickerFont.class);
    }

    public void saveSplashImagePath(String url){
        mSharedPreferences.edit().putString(SPLASH_URL, url).apply();
    }

    public String getSplashImagePath(){
        return mSharedPreferences.getString(SPLASH_URL, "");
    }
}
