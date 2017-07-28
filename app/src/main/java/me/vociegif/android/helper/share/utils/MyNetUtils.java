package me.vociegif.android.helper.share.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import me.vociegif.android.App;
import com.buhuavoice.app.BuildConfig;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.share.entity.ShareUserData;
import me.vociegif.android.helper.share.event.NetUtilsEvent;

import org.json.JSONException;
import org.json.JSONObject;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by design on 2016/3/14.
 */
public class MyNetUtils {

    public static String openid;
    public static String access_token;
    public static String refresh_token;
    public static boolean isVaild;

    public static final int NETWORK_RESULT_FAIL = 1;
    public static final int NETWORK_RESULT_JSON_EXCEPTION = 2;
    public static final int NETWORK_RESULT_RETURN_NULL = 3;
    public static final int NETWORK_RESULT_SUCCESS = 5;

    public static final String TAG = "MyNetUtils";

    public static void getSinaUser(String token, final String uid) { // 微博
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();
        String url = "https://api.weibo.com/2/users/show.json?access_token=" + token + "&uid=" + uid;
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                String str = responseInfo.result;
                Log.e(TAG, "getSinaUser onSuccess" + str);
                try {
                    JSONObject object = new JSONObject(str);


                    ShareUserData userData = new ShareUserData();
                    userData.city = object.getString("city");
                    userData.country = object.getString("location");

                    String strgender = object.getString("gender");

                    if ("f".equals(strgender)) {
                        userData.gender = 1;
                    } else if ("m".equals(strgender)) {
                        userData.gender = 2;
                    }
                    userData.head =  object.getString("profile_image_url");
                    userData.pf = Constants.LOGIN_PF_WEIBO;
                    userData.nickname =  object.getString("screen_name");
                    userData.sign =  object.getString("description");
                    userData.uid =  uid;

                    Log.e("getSinaUser send", str);
                    NetUtilsEvent netEvent = new NetUtilsEvent();
                    netEvent.setResult(NETWORK_RESULT_SUCCESS);
                    netEvent.setUseradata(userData);
                    netEvent.setMsg(str);
                    netEvent.setMediaType(ShareConstant.SHARE_MEDIA.SINA);
                    EventBus.getDefault().post(netEvent);


                } catch (JSONException e) {
                    e.printStackTrace();
                    NetUtilsEvent netEvent = new NetUtilsEvent();
                    netEvent.setMsg("getSinaUser -- JSONException");
                    netEvent.setResult(NETWORK_RESULT_JSON_EXCEPTION);
                    netEvent.setMediaType(ShareConstant.SHARE_MEDIA.SINA);
                    EventBus.getDefault().post(netEvent);
                    Log.e("getUser JSONException", e.getMessage());
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                NetUtilsEvent netEvent = new NetUtilsEvent();
                netEvent.setMsg("getToken -- HttpException" + s);
                netEvent.setResult(NETWORK_RESULT_FAIL);
                netEvent.setMediaType(ShareConstant.SHARE_MEDIA.SINA);
                EventBus.getDefault().post(netEvent);
            }
        });

    }

    public static void getToken(String mcode) { // 微信获取access_token

        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ BuildConfig.WECHAT_APP_ID
                +"&secret="+ BuildConfig.WECHAT_APP_KEY + "&code=" + mcode + "&grant_type=authorization_code";
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                String str = responseInfo.result;
                Log.e(TAG, "getToken onSuccess" + str);
                try {
                    JSONObject object = new JSONObject(str);

                    if (object.has("access_token") && object.has("openid")) {

                        openid = object.getString("openid");
                        access_token = object.getString("access_token");
                        refresh_token = object.getString("refresh_token");


                        if (!TextUtils.isEmpty(openid) &&! TextUtils.isEmpty(access_token) && !TextUtils.isEmpty(refresh_token)) {

                            Log.e("getToken onSuccess",str);
                            if(MyNetUtils.isTokenInvaild(access_token, openid)) {

                                App.getDefault().setValue("openid", openid);
                                App.getDefault().setValue("refresh_token", refresh_token);
                                App.getDefault().setValue("access_token", access_token);

                                getWechatUser(openid, access_token); // 获得了token信息
                            } else {
                                refreshToken(App.getDefault().getStringValue("refresh_token"));
                            }

                        } else {
                            NetUtilsEvent netEvent = new NetUtilsEvent();
                            netEvent.setMsg("getToken--TextUtils.isEmpty");
                            netEvent.setResult(NETWORK_RESULT_RETURN_NULL);
                            netEvent.setMediaType(ShareConstant.SHARE_MEDIA.WECHAT);
                            EventBus.getDefault().post(netEvent);
                        }
                    } else {
                        NetUtilsEvent netEvent = new NetUtilsEvent();
                        netEvent.setMsg("getToken--object.has(access_token)");
                        netEvent.setResult(NETWORK_RESULT_JSON_EXCEPTION);
                        netEvent.setMediaType(ShareConstant.SHARE_MEDIA.WECHAT);
                        EventBus.getDefault().post(netEvent);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                    NetUtilsEvent netEvent = new NetUtilsEvent();
                    netEvent.setMsg("getToken -- JSONException");
                    netEvent.setResult(NETWORK_RESULT_JSON_EXCEPTION);
                    netEvent.setMediaType(ShareConstant.SHARE_MEDIA.WECHAT);
                    EventBus.getDefault().post(netEvent);
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                NetUtilsEvent netEvent = new NetUtilsEvent();
                netEvent.setMsg("getToken -- HttpException"+ s);
                netEvent.setResult(NETWORK_RESULT_FAIL);
                netEvent.setMediaType(ShareConstant.SHARE_MEDIA.WECHAT);
                EventBus.getDefault().post(netEvent);
            }
        });

    }

    public static void getWechatUser(String openid, String access_token) {   // 微信获取用户信息
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String str = responseInfo.result;
                Log.e(TAG, "getWechatUser :" + str);
                try {
                    JSONObject json = new JSONObject(str);
                    ShareUserData userData = new ShareUserData();
                    userData.uid = json.getString("openid");
                    userData.pf = Constants.LOGIN_PF_WEBCHAT;

                    if (json.has("sex")) {
                        userData.gender = json.getInt("sex");
                    }
                    userData.nickname = json.getString("nickname");
                    userData.country = json.getString("country");
                    userData.city = json.getString("city");
                    userData.head = json.getString("headimgurl");

                    NetUtilsEvent netEvent = new NetUtilsEvent();
                    netEvent.setResult(NETWORK_RESULT_SUCCESS);
                    netEvent.setUseradata(userData);
                    netEvent.setMsg(str);
                    netEvent.setMediaType(ShareConstant.SHARE_MEDIA.WECHAT);
                    EventBus.getDefault().post(netEvent);

                } catch(JSONException e) {
                    e.printStackTrace();

                    NetUtilsEvent netEvent = new NetUtilsEvent();
                    netEvent.setResult(NETWORK_RESULT_JSON_EXCEPTION);
                    netEvent.setMediaType(ShareConstant.SHARE_MEDIA.WECHAT);
                    EventBus.getDefault().post(netEvent);

                    Log.e("getUser JSONException e", e.getMessage());
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e("onFailure", e.getMessage());

                NetUtilsEvent netEvent = new NetUtilsEvent();
                netEvent.setResult(NETWORK_RESULT_FAIL);
                netEvent.setMediaType(ShareConstant.SHARE_MEDIA.WECHAT);
                EventBus.getDefault().post(netEvent);

            }
        });

    }

    // 判断apenid ,access_token是否有效
    public static  boolean isTokenInvaild(String accessToken, String openId){

        String url = "https://api.weixin.qq.com/sns/auth?access_token=" + accessToken +
                "&openid="+ openId;
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();
        isVaild = true;
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>(){
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String str = responseInfo.result;
                Log.e(TAG, "isTokenInvaild :" + str);
                try{
                    JSONObject object = new JSONObject(str);

                    if (object.has("errcode") && object.has("errmsg")) {
                        String errmsg = object.getString("errmsg");
                        int errcode = object.getInt("errcode");
                        if(!"ok".equals(errmsg) || errcode != 0) {
                            isVaild = false;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }

            @Override
            public void onCancelled() {
                super.onCancelled();
            }
        });

        return isVaild;
    }

    public static void refreshToken(final String refresh_Token) {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + BuildConfig.WECHAT_APP_ID +
                "&grant_type=refresh_token&refresh_token=" + refresh_Token;
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>(){
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String str = responseInfo.result;
                Log.e(TAG, "refreshToken :" + str);
                try{
                      JSONObject object = new JSONObject(str);

                    if (object.has("access_token") && object.has("openid") && object.has("refresh_token")) {
                        openid = object.getString("openid");
                        access_token = object.getString("access_token");
                        refresh_token = object.getString("refresh_token");

                        //将获取的新的token和refresh_token写入sp中
                        App.getDefault().setValue("openid", object.getString("openid"));
                        App.getDefault().setValue("refresh_token", object.getString("access_token"));
                        App.getDefault().setValue("access_token", object.getString("refresh_token"));

                        getWechatUser(openid, access_token);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }

            @Override
            public void onCancelled() {
                super.onCancelled();
            }
        });

    }

    public static void shareCount(Context context, String contentId) {
//        Log.i(TAG, "start share count " + contentId);
//
//        if (TextUtils.isEmpty(contentId))
//            return;
//
//        HttpListener listener = new HttpListener() {
//            @Override
//            public void OnResponse(JsonObject obj, int connectType) {
//                Gson gson = new Gson();
//                if(obj == null)
//                    return;
//                Log.i(TAG, obj.toString());
//                switch (obj.get("code").getAsInt()) {
//                    case Constants.requestType.CONTENT_SHARE_COUNTER:
//                        ResultVO resultVOs = gson.fromJson(obj, ResultVO.class);
//                        if (resultVOs.getResult() == Constants.TYPE_1) {
//                            Log.i(TAG, "添加分享次数成功");
//                        }
//
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//        C2BHttpRequest c2BHttpRequest = new C2BHttpRequest(context, listener);
//        ShareCount shareCount = new ShareCount();
//        shareCount.setCode(Constants.requestType.CONTENT_SHARE_COUNTER);
//        shareCount.setContentid(contentId);
//        c2BHttpRequest.setDialog(false);
//        c2BHttpRequest.asyncHttpResponse(Constants.server.serverUrl,
//                c2BHttpRequest.setCanshuVerify(shareCount, Constants.uid, Constants.verify), Constants.requestType.CONTENT_SHARE_COUNTER);
    }
}