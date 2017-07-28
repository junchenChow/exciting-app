package me.vociegif.android.event;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventStartMainThread {
    private String path;
    private boolean isCreate;
    public EventStartMainThread(String path) {
        this.path = path;
    }

    public EventStartMainThread(String path, boolean isCreate) {
        this.path = path;
        this.isCreate = isCreate;
    }


    public String getPath() {
        return path;
    }

    public boolean isCreate() {
        return isCreate;
    }
}
