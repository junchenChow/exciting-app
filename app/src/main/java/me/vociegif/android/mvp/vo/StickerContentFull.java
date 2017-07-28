package me.vociegif.android.mvp.vo;

import java.io.Serializable;
import java.util.List;


public class StickerContentFull extends StickerContentSimple implements Serializable {
    protected String thumb_url;
    protected String icon_url;
    protected String name;
    private int clickType;
    private List<Integer> textRange;
    private String id;
    private int icontype;
    private String author;
    private String zip_url;
    private String gif_url;
    private List<String> pngname;
    private List<Integer> frametime;

    public String getZip_url() {
        return zip_url;
    }

    public void setZip_url(String zip_url) {
        this.zip_url = zip_url;
    }

    public String getGif_url() {
        return gif_url;
    }

    public void setGif_url(String gif_url) {
        this.gif_url = gif_url;
    }

    public List<String> getPngname() {
        return pngname;
    }

    public void setPngname(List<String> pngname) {
        this.pngname = pngname;
    }

    public List<Integer> getFrametime() {
        return frametime;
    }

    public void setFrametime(List<Integer> frametime) {
        this.frametime = frametime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getIcontype() {
        return icontype;
    }

    public int getClickType() {
        return clickType;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }

    public void setIcontype(int icontype) {
        this.icontype = icontype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Integer> getTextRange() {
        return textRange;
    }

    public void setTextRange(List<Integer> locs) {
        this.textRange = locs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}