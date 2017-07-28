package me.vociegif.android.event;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventGetVoiceResponse {
    public String text;

    public EventGetVoiceResponse(String strComment) {
        this.text = strComment;
    }

    public String getText() {
        return text;
    }

}
