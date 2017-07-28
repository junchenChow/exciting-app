package me.vociegif.android.mvp.vo;

import java.io.Serializable;

public class StickerContentSimple implements Serializable {

    protected static final long serialVersionUID = 1L;
    protected int seriesid;
    protected int stickerid;
    protected int type;
    protected String resource_url;
    protected int width;
    protected int height;



    public int getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(int seriesid) {
        this.seriesid = seriesid;
    }

    public int getStickerid() {
        return stickerid;
    }

    public void setStickerid(int stickerid) {
        this.stickerid = stickerid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
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
}
