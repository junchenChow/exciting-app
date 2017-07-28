package me.vociegif.android.helper.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;

import me.vociegif.android.App;
import com.buhuavoice.app.BuildConfig;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.share.entity.ShareException;
import me.vociegif.android.helper.share.entity.ShareUserData;
import me.vociegif.android.helper.share.event.UserInfoEvent;
import me.vociegif.android.helper.share.utils.MyNetUtils;
import me.vociegif.android.helper.share.utils.ShareConstant;
import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ShareQQ extends BaseShare{

    private static final String TAG = "ShareQQ";

    private Tencent mTencent;
    private IUiListener qqShareListener ;

    @Override
    public void auth(Activity activity) {
        ShareType = ShareConstant.AUTH;
        mTencent = Tencent.createInstance(BuildConfig.QQ_APP_ID, activity.getApplicationContext());

        if(shareListener == null)
            return;

        qqShareListener = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                if (response == null) {
                    ShareException exception = new ShareException("response == null");
                    shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.QQ, exception);
                    return;

                }
                try {
                    JSONObject jsonObject = (JSONObject) response;
                    if(null != jsonObject && jsonObject.length() == 0) {
                        ShareException exception = new ShareException("null != jsonObject && jsonObject.length() == 0");
                        shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.QQ, exception);
                    }

                    String token = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
                    String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                    String openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                    App.getDefault().setValue("qq_auth", true);
                    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                            && !TextUtils.isEmpty(openId)) {
                        mTencent.setAccessToken(token, expires);
                        mTencent.setOpenId(openId);
                        shareListener.onComplete(ShareType, ShareConstant.SHARE_MEDIA.QQ, response);
                    }

                } catch(Exception e) {
                    ShareException exception = new ShareException("解析json异常" + e.getMessage());
                    shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.QQ, exception);
                }

            }

            @Override
            public void onError(UiError uiError) {
                ShareException exception = new ShareException(uiError.errorDetail);
                shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.QQ, exception);
            }

            @Override
            public void onCancel() {
                shareListener.onCancel(ShareType, ShareConstant.SHARE_MEDIA.QQ);
            }
        };

        if (!mTencent.isSessionValid()) {
            mTencent.login(activity, "all", qqShareListener);
        } else {
            mTencent.logout(activity);
            mTencent.login(activity, "all", qqShareListener);
            return;
        }

        mTencent.logout(activity);
    }


    @Override
    public void login(Activity activity) {
        ShareType = ShareConstant.GET_INFO;
        if(mTencent == null)
             mTencent = Tencent.createInstance(BuildConfig.QQ_APP_ID, activity.getApplicationContext());

        if(shareListener == null)
            return;

        if (mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                    ShareException exception = new ShareException(e.errorDetail);
                    shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.QQ, exception);
                }

                @Override
                public void onComplete(final Object response) {

                    JSONObject json = (JSONObject) response;
                    try {
                        ShareUserData userData = new ShareUserData();
                        userData.head = json.getString("figureurl_qq_2"); // 获得qq登录后的用户头像
                        userData.nickname = json.getString("nickname");
                        userData.gender = 0;
                        if (json.has("gender")) {
                            String strgender = json.getString("gender");
                            if ("男".equals(strgender)) {
                                userData.gender = 1;
                            } else if ("女".equals(strgender)) {
                                userData.gender = 2;
                            }
                        }
                        userData.sign = json.getString("msg");

                        if (json.has("province")) {
                            userData.province = json.getString("province");
                        }

                        if (json.has("city")) {
                            userData.city = json.getString("city");
                        }

                        userData.uid = mTencent.getOpenId();
                        userData.pf = Constants.LOGIN_PF_QQ;

                        UserInfoEvent event = new UserInfoEvent();
                        event.setJsonStr(json.toString());
                        event.setUseradata(userData);
                        event.setPlatform(ShareConstant.SHARE_MEDIA.QQ);

                        shareListener.onComplete(ShareType, ShareConstant.SHARE_MEDIA.QQ, event);
                    } catch (JSONException e) {

                        ShareException exception = new ShareException(e.getMessage() + SystemClock.elapsedRealtime());
                        shareListener.onException(ShareType, ShareConstant.SHARE_MEDIA.QQ, exception);
                    }
                }

                @Override
                public void onCancel() {
                    shareListener.onCancel(ShareType, ShareConstant.SHARE_MEDIA.QQ);
                }
            };

            UserInfo mInfo = new UserInfo(activity, mTencent.getQQToken());
            mInfo.getUserInfo(listener);
        }
    }


    public void share(final int platform, String url, final Activity activity){

        App.getDefault().setValue("qq_auth", true);
        ShareType = ShareConstant.SHARE;

        if(activity == null)
            return;
        if(mTencent == null)
            mTencent = Tencent.createInstance(BuildConfig.QQ_APP_ID, activity);

        qqShareListener = new IUiListener() {
            @Override
            public void onCancel() {

                if(shareListener == null)
                    return;

                shareListener.onCancel(ShareConstant.SHARE, platform);
                onDestroy();
            }

            @Override
            public void onComplete(Object response) {
                if(shareListener == null)
                    return;

                MyNetUtils.shareCount(activity, contentId);
                shareListener.onComplete(ShareConstant.SHARE, platform, response);
                onDestroy();
            }

            @Override
            public void onError(UiError e) {
                if(shareListener == null)
                    return;

                ShareException exception = new ShareException(e.errorDetail + e.errorMessage);
                shareListener.onException(ShareConstant.SHARE, platform, exception);
                onDestroy();
            }
        };

        if(platform == ShareConstant.SHARE_MEDIA.QQ) {
            mTencent.shareToQQ(activity, getQQBundle(url), qqShareListener);
        } else {
            mTencent.shareToQQ(activity, getQZoneBundle(url), qqShareListener);
        }
    }

    @Override
    public boolean isInstalled(Context context) {
        if(context == null)
            return false;

        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;

                } else if(pn.equals("com.tencent.qzone")) {
                    return true;

                }
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
    }

    private Bundle getQQBundle(String url) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE); // 分享本地图片
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, url);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);

        return params;
    }

    private Bundle getQZoneBundle(String url) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE); // 分享本地图片
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, url);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);

        return params;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.shareListener = null;
    }
}
