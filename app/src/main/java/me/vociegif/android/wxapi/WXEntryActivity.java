package me.vociegif.android.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.buhuavoice.app.BuildConfig;
import me.vociegif.android.helper.share.event.BaseRespEvent;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    private BaseRespEvent mBaseEvent = new BaseRespEvent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {
        mBaseEvent.setBaseResp(baseResp);
        EventBus.getDefault().post(mBaseEvent);
        finish();
    }

}
