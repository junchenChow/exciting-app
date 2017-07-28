package me.vociegif.android.event;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventShare {
    boolean isShare;

    public EventShare(boolean isShare) {
        this.isShare = isShare;
    }

    public boolean isShare() {
        return isShare;
    }
}
