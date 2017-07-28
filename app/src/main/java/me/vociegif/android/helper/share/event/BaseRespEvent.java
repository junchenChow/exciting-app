package me.vociegif.android.helper.share.event;


import com.tencent.mm.sdk.modelbase.BaseResp;

/**
 * Created by design on 2016/3/15.
 */
public class BaseRespEvent {

    private BaseResp baseResp;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BaseResp getBaseResp() {
        return baseResp;
    }

    public void setBaseResp(BaseResp baseResp) {
        this.baseResp = baseResp;
    }
}
