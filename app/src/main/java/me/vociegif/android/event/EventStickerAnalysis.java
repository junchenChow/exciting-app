package me.vociegif.android.event;

import android.graphics.Bitmap;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventStickerAnalysis {
    public static final int STICKER_LOAD_FAILED = 1;
    public static final int STICKER_LOAD_FINISH = 2;

    private Bitmap bitmap;
    private int succeed;
    private String url;

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    private int param;

    public EventStickerAnalysis(){}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getSucceed() {
        return succeed;
    }

    public void setSucceed(int succeed) {
        this.succeed = succeed;
    }

}
