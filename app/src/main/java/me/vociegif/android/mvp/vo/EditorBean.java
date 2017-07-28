package me.vociegif.android.mvp.vo;

import java.io.Serializable;

/**
 * Created by 时辰 on 16/5/17.
 */
public class EditorBean implements Serializable {
    private int id;
    private String stickerid;
    private String name;
    private String color;
    private String image;
    private String text;
    private String url;
    private boolean isSelector;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }

    public String getStickerid() {
        return stickerid;
    }

    public void setStickerid(String stickerid) {
        this.stickerid = stickerid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
