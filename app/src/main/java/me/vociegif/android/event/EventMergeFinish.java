package me.vociegif.android.event;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventMergeFinish {
    private String gifPath;

    public EventMergeFinish(String gifPath) {
        this.gifPath = gifPath;
    }

    public String getGifPath() {
        return gifPath;
    }
}
