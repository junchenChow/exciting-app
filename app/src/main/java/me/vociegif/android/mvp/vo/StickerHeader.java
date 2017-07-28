package me.vociegif.android.mvp.vo;

import java.io.Serializable;

/**
 * 文件头
 *
 * @author Administrator
 */
public class StickerHeader implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String uid;
    private String sound;
    private String full;  // 整图
    private String thumb;
    private String full_url;
    private String thumb_url;
    private int stickerWeight;
    private int stickerMode;

    public int getStickerWeight() {
        return stickerWeight;
    }

    public void setStickerWeight(int stickerWeight) {
        this.stickerWeight = stickerWeight;
    }

    public int getStickerMode() {
        return stickerMode;
    }

    public void setStickerMode(int stickerMode) {
        this.stickerMode = stickerMode;
    }

    public StickerHeader() {
    }

    public StickerHeader(String full) {
        this.full = full;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getFull_url() {
        return full_url;
    }

    public void setFull_url(String full_url) {
        this.full_url = full_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

}
