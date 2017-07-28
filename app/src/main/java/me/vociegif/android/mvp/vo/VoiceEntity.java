package me.vociegif.android.mvp.vo;

import java.io.Serializable;

/**
 * Created by 时辰 on 16/6/8.
 */
public class VoiceEntity implements Serializable {
    private String text;
    private int code;
    private String version;
    private String operatingSystem;

    public VoiceEntity(String text, int code) {
        this.text = text;
        this.code = code;
    }

    public VoiceEntity(int code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
