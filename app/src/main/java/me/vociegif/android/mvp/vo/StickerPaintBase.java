package me.vociegif.android.mvp.vo;

import java.io.Serializable;

/**
 * 贴纸的坐标信息
 *
 * @author Administrator
 */
public class StickerPaintBase implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int stickerid;
    private int seriesid;
    private float posX;
    private float posY;
    private float scale;
    private float rotate;
    private int depth;
    private boolean lockon;
    private boolean isFlip;
    private float width;
    private float height;

    public int getStickerid() {
        return stickerid;
    }

    public void setStickerid(int stickerid) {
        this.stickerid = stickerid;
    }

    public int getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(int seriesid) {
        this.seriesid = seriesid;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isLockon() {
        return lockon;
    }

    public void setLockon(boolean lockon) {
        this.lockon = lockon;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public boolean isFlip() {
        return isFlip;
    }

    public void setFlip(boolean isFlip) {
        this.isFlip = isFlip;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

}
