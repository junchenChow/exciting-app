package me.vociegif.android.helper.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.glidebitmappool.GlideBitmapFactory;
import me.vociegif.android.App;
import com.buhuavoice.app.BuildConfig;
import me.vociegif.android.helper.share.entity.ShareException;
import me.vociegif.android.helper.share.event.BaseRespEvent;
import me.vociegif.android.helper.share.event.NetUtilsEvent;
import me.vociegif.android.helper.share.event.UserInfoEvent;
import me.vociegif.android.helper.share.utils.MyNetUtils;
import me.vociegif.android.helper.share.utils.ShareConstant;
import me.vociegif.android.helper.utils.Util;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ShareWechat extends BaseShare {

    private IWXAPI api;
    private String code;
    private int platform;

    public ShareWechat() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        platform = ShareConstant.SHARE_MEDIA.WECHAT;
    }


    @Override
    public void auth(Activity activity) {
        ShareType = ShareConstant.AUTH;
        api = WXAPIFactory.createWXAPI(activity, BuildConfig.WECHAT_APP_ID, true);
        api.registerApp(BuildConfig.WECHAT_APP_ID);

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "mengwo_wx_login";

        api.sendReq(req);
    }

    @Override
    public void login(Activity activity) {
        ShareType = ShareConstant.GET_INFO;
        if (TextUtils.isEmpty(code))
            return;
        MyNetUtils.getToken(code);
    }



    @Override
    public void share(Activity activity, Bitmap shareBitmap, Bitmap thumbBitmap, int platform) {
        ShareType = ShareConstant.SHARE;
        this.platform = platform;

        if (activity == null)
            return;

        thumbBitmap = GlideBitmapFactory.decodeFile(savePath);

        if (api == null) {
            api = WXAPIFactory.createWXAPI(activity, BuildConfig.WECHAT_APP_ID, false);
            api.registerApp(BuildConfig.WECHAT_APP_ID);
        }

        WXMediaMessage msg = new WXMediaMessage();
        msg.thumbData = Util.bmpToByteArray(thumbBitmap, true);

        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(savePath);
        msg.mediaObject = imgObj;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "img" + System.currentTimeMillis();
        req.message = msg;

        if (platform == ShareConstant.SHARE_MEDIA.WECHAT) {
            req.scene = SendMessageToWX.Req.WXSceneSession;

        } else if (platform == ShareConstant.SHARE_MEDIA.WECHAT_CIRCLE) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        api.sendReq(req);

        if (thumbBitmap != null) {
            thumbBitmap.recycle();
        }

    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BaseRespEvent event) {
        if (ShareType == ShareConstant.SHARE) {
            this.onDestroy();
        }

        BaseResp baseResp = event.getBaseResp();
        if (baseResp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
            shareListener.onCancel(ShareType, platform);
            return;
        }

        if (ShareType == ShareConstant.AUTH) {
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            if (TextUtils.isEmpty(resp.code)) {
                return;
            }
            this.code = resp.code;
        }

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                shareListener.onComplete(ShareType, platform, event);
                if (ShareType == ShareConstant.SHARE) {
                    MyNetUtils.shareCount(App.getDefault(), contentId);
                }

                break;

            case BaseResp.ErrCode.ERR_USER_CANCEL:
                shareListener.onCancel(ShareType, platform);
                break;

            default:
                ShareException exception = new ShareException("被拒绝或者失败啦");
                shareListener.onException(ShareType, platform, exception);
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NetUtilsEvent event) {
        if (shareListener == null)
            return;

        if (event.getMediaType() != ShareConstant.SHARE_MEDIA.WECHAT)
            return;

        switch (event.getResult()) {
            case MyNetUtils.NETWORK_RESULT_FAIL:
                ShareException exception = new ShareException("NETWORK_RESULT_FAIL");
                shareListener.onException(ShareConstant.GET_INFO, ShareConstant.SHARE_MEDIA.WECHAT, exception);
                break;

            case MyNetUtils.NETWORK_RESULT_SUCCESS:
                UserInfoEvent userInfoEvent = new UserInfoEvent();
                if (event.getUseradata() != null)
                    userInfoEvent.setUseradata(event.getUseradata());
                userInfoEvent.setPlatform(ShareConstant.SHARE_MEDIA.WECHAT);
                userInfoEvent.setJsonStr(event.getMsg());
                shareListener.onComplete(ShareConstant.GET_INFO, ShareConstant.SHARE_MEDIA.WECHAT, userInfoEvent);
                break;

            case MyNetUtils.NETWORK_RESULT_JSON_EXCEPTION:
                ShareException jsonException = new ShareException("NETWORK_RESULT_JSON_EXCEPTION");
                shareListener.onException(ShareConstant.GET_INFO, ShareConstant.SHARE_MEDIA.WECHAT, jsonException);
                break;

            case MyNetUtils.NETWORK_RESULT_RETURN_NULL:
                ShareException nullException = new ShareException("NETWORK_RESULT_RETURN_NULL");
                shareListener.onException(ShareConstant.GET_INFO, ShareConstant.SHARE_MEDIA.WECHAT, nullException);
                break;

            default:
                break;
        }

    }

    public static boolean isInstalled(Context context, int type) {
        if (context == null)
            return false;
        IWXAPI api = WXAPIFactory.createWXAPI(context, BuildConfig.WECHAT_APP_ID, false);
        api.registerApp(BuildConfig.WECHAT_APP_ID);
        return api.isWXAppInstalled();
    }
}