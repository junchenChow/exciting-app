package me.vociegif.android.event;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventStickerTool {
    boolean isSelector;

    public EventStickerTool(boolean isSelector) {
        this.isSelector = isSelector;
    }

    public boolean isSelector() {
        return isSelector;
    }
}
