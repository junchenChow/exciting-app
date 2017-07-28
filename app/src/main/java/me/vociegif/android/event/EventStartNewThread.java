package me.vociegif.android.event;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventStartNewThread {
    private boolean isCreate;
    private String photoPath;

    public EventStartNewThread(boolean isCreate) {
        this.isCreate = isCreate;
    }

    public EventStartNewThread(boolean isCreate, String photoPath) {
        this.isCreate = isCreate;
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isCreate() {
        return isCreate;
    }
}

