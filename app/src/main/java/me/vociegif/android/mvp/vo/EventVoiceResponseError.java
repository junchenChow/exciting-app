package me.vociegif.android.mvp.vo;

/**
 * Created by zhoujunchen on 16/7/25.
 */

public class EventVoiceResponseError {
    private boolean isShakeComplete ;

    public EventVoiceResponseError(boolean isShakeComplete) {
        this.isShakeComplete = isShakeComplete;
    }

    public boolean isShakeComplete() {
        return isShakeComplete;
    }
}
