package me.vociegif.android.helper.share.event;


import me.vociegif.android.helper.share.entity.ShareUserData;

/**
 * Created by design on 2016/3/10.
 */
public class UserInfoEvent {
    private ShareUserData useradata;
    private int platform;
    private String jsonStr;

    public UserInfoEvent() {
    }

    public ShareUserData getUseradata() {
        return useradata;
    }

    public void setUseradata(ShareUserData useradata) {
        this.useradata = useradata;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }
}
