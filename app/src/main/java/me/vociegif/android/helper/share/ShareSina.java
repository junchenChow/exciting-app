package me.vociegif.android.helper.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.glidebitmappool.GlideBitmapFactory;
import me.vociegif.android.App;
import com.buhuavoice.app.BuildConfig;
import me.vociegif.android.helper.share.entity.ShareException;
import me.vociegif.android.helper.share.event.NetUtilsEvent;
import me.vociegif.android.helper.share.event.UserInfoEvent;
import me.vociegif.android.helper.share.iterface.AuthSinaListener;
import me.vociegif.android.helper.share.utils.MyNetUtils;
import me.vociegif.android.helper.share.utils.ShareConstant;
import me.vociegif.android.helper.utils.Util;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

import java.util.LinkedHashMap;

import org.greenrobot.eventbus.EventBus;

public class ShareSina extends BaseShare {

    private static final int THUMB_SIZE = 150;
    public static final String REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    public static final String TAG = "ShareSina";

    private AuthWrap authWrap;
    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private IWeiboShareAPI mWeibo;
    private AuthSinaListener listener;

    public ShareSina() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void auth(Activity activity) {
        ShareType = ShareConstant.AUTH;
        mAccessToken = new Oauth2AccessToken();
        mAuthInfo = new AuthInfo(activity, BuildConfig.SINA_APP_ID, REDIRECT_URL, null);
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
        authWrap = new AuthWrap();

        authWrap.context = activity;
        authWrap.authListener = new AuthListener();
        mSsoHandler.authorize(authWrap.authListener);
    }


    @Override
    public void login(Activity activity) {
        ShareType = ShareConstant.GET_INFO;
        if (mAccessToken == null || !mAccessToken.isSessionValid())
            return;

        App.getDefault().setValue("sina_token", mAccessToken.getToken());
        MyNetUtils.getSinaUser(mAccessToken.getToken(), mAccessToken.getUid());
    }


    @Override
    public void share(Activity activity, Bitmap shareBitmap, Bitmap thumbBitmap, int platform) {
        mWeibo = null;
        mWeibo = WeiboShareSDK.createWeiboAPI(activity, BuildConfig.SINA_APP_KEY);
        mWeibo.registerApp();
        ShareType = ShareConstant.SHARE;

        share(activity, shareBitmap);
    }

    @Override
    public void setSavePath(String savePath) {
        super.setSavePath(savePath);
        Log.i("savePa", savePath);
    }

    // TODO 通过跳客户端进行分享
    public void share(Activity activity, Bitmap bitmap) {
        String token = App.getDefault().getStringValue("sina_token");
        if (!TextUtils.isEmpty(token)) {
            sendPictrue(activity, bitmap);
            // share(activity, bitmap, ShareType);
        } else if (mAccessToken == null || !mAccessToken.isSessionValid()) {
            setAuthListener(activity, bitmap);
            auth(activity);
        } else {
            sendPictrue(activity, bitmap);
            //share(activity, bitmap, ShareType);
        }

    }


    public void share(Activity activity, Bitmap bitmap, int ShareType) {
        ImageObject imageobj = new ImageObject();
        if (bitmap != null) {
            imageobj.setImageObject(bitmap);
            // TODO 更高效的处理图片
            Bitmap thumBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            imageobj.setThumbImage(thumBmp);
        }

        WeiboMessage multmess = new WeiboMessage();
        multmess.mediaObject = imageobj;
        SendMessageToWeiboRequest multRequest = new SendMessageToWeiboRequest();
        multRequest.message = multmess;
        multRequest.transaction = String.valueOf(System.currentTimeMillis());

        mWeibo.sendRequest(activity, multRequest);
    }

    private void shareWeiboGif(Activity activity, String url) {
        if (mWeibo == null) {
            mWeibo = WeiboShareSDK.createWeiboAPI(activity, BuildConfig.SINA_APP_KEY);
            mWeibo.registerApp();
        }
        ImageObject imageobj = new ImageObject();
        imageobj.imageData = Util.bmpToByteArray(GlideBitmapFactory.decodeFile(url), true);
        WeiboMessage multmess = new WeiboMessage();
        multmess.mediaObject = imageobj;
        SendMessageToWeiboRequest multRequest = new SendMessageToWeiboRequest();
        multRequest.message = multmess;
        multRequest.transaction = String.valueOf(System.currentTimeMillis());

        mWeibo.sendRequest(activity, multRequest);
    }

    @Override
    public void shareGif(Activity context, String url) {
//        sendPictrue(context, url);
    }

    /**
     * 发送一条微博
     *
     * @param context
     */
    public void sendPictrue(Context context, String url) {
        this.onDestroy();

        String token = App.getDefault().getStringValue("sina_token");
        if (TextUtils.isEmpty(token)) {
            ShareException exception = new ShareException("token is null");
            shareListener.onException(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA, exception);
            return;
        }
        WeiboParameters params = new WeiboParameters(BuildConfig.SINA_APP_KEY);
        LinkedHashMap mParams = new LinkedHashMap();
        mParams.put("access_token", token);
        mParams.put("status", "分享自不画漫画");
        mParams.put("visible", "0");
        mParams.put("list_id", "");
        mParams.put("pic", Util.File2byte(url));
        mParams.put("lat", "14.5");
        mParams.put("long", "23.0");
        mParams.put("annotations", "");
        params.setParams(mParams);

        new AsyncWeiboRunner(context)
                .requestAsync(
                        "https://api.weibo.com/2/statuses/upload.json",
                        params,
                        "POST",
                        new RequestListener() {
                            @Override
                            public void onComplete(String s) {
                                Log.e("ShareSina:", "onComplete s :" + s);
                                MyNetUtils.shareCount(App.getDefault(), contentId); // 向服务器发分享成功
                                // shareListener.onComplete(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA, s);
                                shareListener.onComplete(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA, "分享成功");

                            }

                            @Override
                            public void onWeiboException(WeiboException e) {
                                Log.e("ShareSina:  ", "exception :" + e.getMessage());
//                                ShareException exception = new ShareException(e.getMessage());
//                                shareListener.onException(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA,exception);
                            }
                        });


    }

    /**
     * 发送一条微博
     *
     * @param context
     * @param bitmap
     */
    public void sendPictrue(Context context, Bitmap bitmap) {
        this.onDestroy();

        String token = App.getDefault().getStringValue("sina_token");
        if (TextUtils.isEmpty(token)) {
            ShareException exception = new ShareException("token is null");
            shareListener.onException(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA, exception);
            return;
        }

        WeiboParameters params = new WeiboParameters("");
        params.put("access_token", token);
        params.put("status", "分享自不画漫画");
        params.put("visible", "0");
        params.put("list_id", "");
        params.put("pic", bitmap);
        params.put("lat", "14.5");
        params.put("long", "23.0");
        params.put("annotations", "");
        new AsyncWeiboRunner(context)
                .requestAsync(
                        "https://api.weibo.com/2/statuses/upload.json",
                        params,
                        "POST",
                        new RequestListener() {
                            @Override
                            public void onComplete(String s) {
                                Log.e("ShareSina:", "onComplete s :" + s);
                                MyNetUtils.shareCount(App.getDefault(), contentId); // 向服务器发分享成功
                                shareListener.onComplete(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA, "分享成功");

                                // shareListener.onComplete(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA, s);
                            }

                            @Override
                            public void onWeiboException(WeiboException e) {
                                Log.e("ShareSina:  ", "exception :" + e.getMessage());
//                                ShareException exception = new ShareException(e.getMessage());
//                                shareListener.onException(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA,exception);
                            }
                        });


    }


    @Override
    public boolean isAuthuried() {
        return App.getDefault().getBooleanValue("sina_auth");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null)
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    /**
     * TODO 跳客户端分享
     *
     * @param context
     * @param intent
     */
    @Override
    public void onNewIntent(Context context, Intent intent) {
        if (mWeibo == null)
            return;
        if (context == null)
            return;

        mWeibo.handleWeiboResponse(intent, new shareSinaListener(context));
    }

    private void setAuthListener(Context context, Bitmap bitmap) {
        listener = new AuthSinaListener() {
            @Override
            public void onSuccess() {
                sendPictrue(context, bitmap);
            }
        };
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }


    /**
     * 获取用户信息
     *
     * @param event
     */
    public void onEventMainThread(NetUtilsEvent event) {
        if (event.getMediaType() != ShareConstant.SHARE_MEDIA.SINA)
            return;

        switch (event.getResult()) {
            case MyNetUtils.NETWORK_RESULT_FAIL:
                ShareException exception = new ShareException("NETWORK_RESULT_FAIL");
                shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.SINA, exception);
                break;

            case MyNetUtils.NETWORK_RESULT_SUCCESS:
                UserInfoEvent userInfoEvent = new UserInfoEvent();
                if (event.getUseradata() != null)
                    userInfoEvent.setUseradata(event.getUseradata());
                userInfoEvent.setPlatform(ShareConstant.SHARE_MEDIA.SINA);
                userInfoEvent.setJsonStr(event.getMsg());

                shareListener.onComplete(ShareType, ShareConstant.SHARE_MEDIA.SINA, userInfoEvent);
                break;

            case MyNetUtils.NETWORK_RESULT_JSON_EXCEPTION:
                ShareException jsonException = new ShareException("NETWORK_RESULT_JSON_EXCEPTION");
                shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.SINA, jsonException);
                break;

            case MyNetUtils.NETWORK_RESULT_RETURN_NULL:
                ShareException nullException = new ShareException("NETWORK_RESULT_RETURN_NULL");
                shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.SINA, nullException);
                break;

            default:
                break;
        }
    }

    class AuthWrap {
        AuthListener authListener;
        Context context;
    }

    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            if (values == null)
                return;
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);

            if (mAccessToken.isSessionValid()) {
                if (authWrap.context == null)
                    return;
                App.getDefault().setValue("sina_auth", true);
                if (!TextUtils.isEmpty(mAccessToken.getToken())) {
                    App.getDefault().setValue("sina_token", mAccessToken.getToken());
                }

                shareListener.onComplete(ShareType, ShareConstant.SHARE_MEDIA.SINA, values);

                if (listener != null) {
                    listener.onSuccess();
                }
            } else {
                ShareException exception = new ShareException("授权失败");
                shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.SINA, exception);
            }
        }

        @Override
        public void onCancel() {
            shareListener.onCancel(ShareType, ShareConstant.SHARE_MEDIA.SINA);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ShareException exception = new ShareException("授权失败" + e.getMessage());
            shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.SINA, exception);
        }
    }

    class shareSinaListener implements IWeiboHandler.Response {

        private Context context;

        public shareSinaListener(Context context) {
            this.context = context;
        }

        @Override
        public void onResponse(BaseResponse baseResponse) {
            if (baseResponse != null) {
                switch (baseResponse.errCode) {
                    case WBConstants.ErrorCode.ERR_OK:
                        MyNetUtils.shareCount(context, contentId);
                        App.getDefault().setValue("sina_auth", true);
                        shareListener.onComplete(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA, baseResponse);
                        break;

                    case WBConstants.ErrorCode.ERR_CANCEL:
                        shareListener.onCancel(ShareConstant.SHARE, ShareConstant.SHARE_MEDIA.SINA);
                        break;

                    case WBConstants.ErrorCode.ERR_FAIL:
                        ShareException nullException = new ShareException(baseResponse.errMsg + baseResponse.reqPackageName + baseResponse.errCode);
                        shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.SINA, nullException);
                        break;

                    default:
                        ShareException exception = new ShareException("default");
                        shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.SINA, exception);
                        break;
                }
            }
        }
    }

    public static boolean isInstalled(Context context, int type) {
        if (context == null)
            return false;

        return WeiboShareSDK.createWeiboAPI(context, BuildConfig.SINA_APP_KEY).isWeiboAppInstalled();
    }

}
