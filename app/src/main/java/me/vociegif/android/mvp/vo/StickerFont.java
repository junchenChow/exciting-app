package me.vociegif.android.mvp.vo;

import java.io.Serializable;

/**
 * Created by 时辰 on 16/5/25.
 */
public class StickerFont implements Serializable {
    private int id;
    private String url;

    public StickerFont(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
