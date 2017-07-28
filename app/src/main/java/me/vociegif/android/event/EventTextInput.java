package me.vociegif.android.event;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class EventTextInput {
    private String comment;

    public EventTextInput(String comment){
        this.comment = comment;
    }


    public String getComment() {
        return comment;
    }
}
