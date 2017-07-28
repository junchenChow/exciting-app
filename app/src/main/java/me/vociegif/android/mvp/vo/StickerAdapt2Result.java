package me.vociegif.android.mvp.vo;


import java.io.Serializable;
import java.util.List;

public class StickerAdapt2Result implements Serializable {

    private List<StickerContentFull> list;
    private List<StickerCreator> adapts;
    private List<StickerFile> scenes;
    private List<StickerPaint> stickerlist;
    private List<EditorBean> fontlist;
    private String text;
    private int scenecount;
    private StickerHeader head;
    private int framecount;

    public List<StickerPaint> getStickerlist() {
        return stickerlist;
    }

    public void setStickerlist(List<StickerPaint> stickerlist) {
        this.stickerlist = stickerlist;
    }

    public int getFramecount() {
        return framecount;
    }

    public void setFramecount(int framecount) {
        this.framecount = framecount;
    }

    public List<EditorBean> getFontlist() {
        return fontlist;
    }

    public void setFontlist(List<EditorBean> fontlist) {
        this.fontlist = fontlist;
    }

    public StickerHeader getHead() {
        return head;
    }

    public void setHead(StickerHeader head) {
        this.head = head;
    }

    public List<StickerPaint> getStickerList() {
        return stickerlist;
    }

    public void setStickerList(List<StickerPaint> stickerlist) {
        this.stickerlist = stickerlist;
    }

    public List<StickerContentFull> getList() {
        return list;
    }

    public void setList(List<StickerContentFull> list) {
        this.list = list;
    }

    public List<StickerCreator> getAdapts() {
        return adapts;
    }

    public void setAdapts(List<StickerCreator> adapts) {
        this.adapts = adapts;
    }

    public List<StickerFile> getScenes() {
        return scenes;
    }

    public void setScenes(List<StickerFile> scenes) {
        this.scenes = scenes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getScenecount() {
        return scenecount;
    }

    public void setScenecount(int scenecount) {
        this.scenecount = scenecount;
    }

}