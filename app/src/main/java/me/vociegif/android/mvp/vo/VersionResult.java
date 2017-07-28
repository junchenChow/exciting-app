package me.vociegif.android.mvp.vo;

public class VersionResult {

    private int code;
    private int result;
    private VersionUpdateEntity versionchange;
    private String welcomeimage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public VersionUpdateEntity getVersionchange() {
        return versionchange;
    }

    public void setVersionchange(VersionUpdateEntity versionchange) {
        this.versionchange = versionchange;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getWelcomeimage() {
        return welcomeimage;
    }

    public void setWelcomeimage(String welcomeimage) {
        this.welcomeimage = welcomeimage;
    }
}
