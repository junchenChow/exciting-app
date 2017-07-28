package me.vociegif.android.mvp.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 作品上传类
 *
 * @author Administrator
 */
public class StickerContent implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private StickerHeader head; //整图
    private List<StickerCreator> adapts; //改编时服务器返回的数据
    private String text = ""; // 话题
    private List<StickerFile> scenes; // 格子里面的内容
    private List<StickerPaint> stickerlist; //外面的stickers


    public List<StickerPaint> getStickerlist() {
        return stickerlist;
    }

    public void setStickerlist(List<StickerPaint> stickerlist) {
        this.stickerlist = stickerlist;
    }

    public StickerHeader getHead() {
        return head;
    }

    public void setHead(StickerHeader head) {
        this.head = head;
    }

    public List<StickerCreator> getAdapts() {
        return adapts;
    }

    public void setAdapts(List<StickerCreator> adapts) {
        this.adapts = adapts;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<StickerFile> getScenes() {
        return scenes;
    }

    public void setScenes(List<StickerFile> scenes) {
        this.scenes = scenes;
    }

}
