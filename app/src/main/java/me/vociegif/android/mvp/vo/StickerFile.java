package me.vociegif.android.mvp.vo;

import java.io.Serializable;
import java.util.List;

public class StickerFile implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String imgname;   //格子里面的背景name
    private String imgname_url;
    private int width;
    private int height;
    private List<StickerPaint> sticker; //格子里面的贴纸信息

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<StickerPaint> getSticker() {
        return sticker;
    }

    public void setSticker(List<StickerPaint> sticker) {
        this.sticker = sticker;
    }

    public String getImgname_url() {
        return imgname_url;
    }

    public void setImgname_url(String imgname_url) {
        this.imgname_url = imgname_url;
    }

}
