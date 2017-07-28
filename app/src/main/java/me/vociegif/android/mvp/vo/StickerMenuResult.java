package me.vociegif.android.mvp.vo;

import java.util.List;

public class StickerMenuResult {

    private StickerDialogue dialogue; // 脑洞对白的贴纸包Id， 贴纸id
    private List<StickerSeriesSimple> recommend;  // 推荐列表
    private List<StickerSeriesSimple> favorite; //收藏列表

    public StickerDialogue getDialogue() {
        return dialogue;
    }

    public void setDialogue(StickerDialogue dialogue) {
        this.dialogue = dialogue;
    }

    public List<StickerSeriesSimple> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<StickerSeriesSimple> recommend) {
        this.recommend = recommend;
    }

    public List<StickerSeriesSimple> getFavorite() {
        return favorite;
    }

    public void setFavorite(List<StickerSeriesSimple> favorite) {
        this.favorite = favorite;
    }

    public class StickerSeriesSimple {
        private int id;
        private String name;
        private String thumb_url;
        private int sort;

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

        public String getThumb_url() {
            return thumb_url;
        }

        public void setThumb_url(String thumb_url) {
            this.thumb_url = thumb_url;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
    }
}




