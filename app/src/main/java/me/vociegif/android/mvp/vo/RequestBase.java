package me.vociegif.android.mvp.vo;

import java.util.ArrayList;
import java.util.List;

public class RequestBase {

    private List<Object> data;
    private String uid;
    private String verify;

    public List<Object> getData() {
        return data;
    }

    public void setData(Object data) {
        List<Object> dataS = new ArrayList<Object>();
        dataS.add(data);
        this.data = dataS;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

}
