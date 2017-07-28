package me.vociegif.android.mvp.vo;

import java.io.Serializable;

public class StickerCreator implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String contentid;
    private String creator;
    private String createtime;

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

}
