package me.vociegif.android.mvp.vo;

import java.io.Serializable;
import java.util.List;

public class StickerList implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<StickerPaint> stickers;
    private List<StickerPaint> stickersList;

    public List<StickerPaint> getStickersList() {
        return stickersList;
    }

    public void setStickersList(List<StickerPaint> stickersList) {
        this.stickersList = stickersList;
    }

    public StickerList(List<StickerPaint> stickers) {
        this.stickers = stickers;
    }

    public StickerList(){

    }

    public List<StickerPaint> getStickers() {
        return stickers;
    }

    public void setStickers(List<StickerPaint> stickers) {
        this.stickers = stickers;
    }

}
