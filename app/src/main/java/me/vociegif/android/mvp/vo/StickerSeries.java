package me.vociegif.android.mvp.vo;

import java.util.List;

/**
 * 单个贴纸包的信息
 *
 * @author design
 */
public class StickerSeries {

    private SeriesDetailHeader stickerHeader; // 贴纸包的信息

    private List<StickerContentFull> list; // 单个贴纸信息

    public SeriesDetailHeader getStickerHeader() {
        return stickerHeader;
    }

    public void setStickerHeader(SeriesDetailHeader stickerHeader) {
        this.stickerHeader = stickerHeader;
    }

    public List<StickerContentFull> getList() {
        return list;
    }

    public void setList(List<StickerContentFull> list) {
        this.list = list;
    }


}

