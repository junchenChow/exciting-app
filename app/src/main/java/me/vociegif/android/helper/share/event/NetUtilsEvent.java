package me.vociegif.android.helper.share.event;


import me.vociegif.android.helper.share.entity.ShareUserData;

/**
 * Created by design on 2016/3/16.
 */
public class NetUtilsEvent {
    private int mediaType;
    private int result;
    private String msg;
    private ShareUserData useradata;

    public ShareUserData getUseradata() {
        return useradata;
    }

    public void setUseradata(ShareUserData useradata) {
        this.useradata = useradata;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
