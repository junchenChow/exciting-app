package me.vociegif.android.gif.gif_vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class GifBean implements Serializable {
    private List<GifMinutes> bitmaps;

    public List<GifMinutes> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(List<GifMinutes> bitmaps) {
        this.bitmaps = bitmaps;
    }
}
