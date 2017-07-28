package me.vociegif.android.event;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventServerError {
    private String errorMsg ;

    public EventServerError(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
