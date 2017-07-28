package me.vociegif.android.helper.share.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.buhuavoice.app.R;
import me.vociegif.android.App;
import me.vociegif.android.helper.share.ShareSina;
import me.vociegif.android.helper.share.entity.ShareException;
import me.vociegif.android.helper.share.iterface.ShareListener;
import me.vociegif.android.helper.utils.ToastUtil;


/**
 * Created by design on 2016/4/20.
 */
public class ShareUtils {

    public static void onSinaAttention(Context context, String uid){
        if(context == null || TextUtils.isEmpty(uid))
            return;

        String url = "http://weibo.cn/qr/userinfo?uid=" + uid;
        if(ShareSina.isInstalled(context,0))
            url = "sinaweibo://userinfo?uid=" + uid;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.createChooser(intent,"weibo");
        context.startActivity(intent);
    }

    public static String getPfDesc(int platform) {
        String name = "";
        switch (platform) {
            case ShareConstant.SHARE_MEDIA.QQ:
                name = "QQ";
                break;

            case ShareConstant.SHARE_MEDIA.QQ_ZONE:
                name = "QQ空间";
                break;

            case ShareConstant.SHARE_MEDIA.WECHAT:
                name = "微信";
                break;

            case ShareConstant.SHARE_MEDIA.SINA:
                name = "新浪微博";
                break;

            case ShareConstant.SHARE_MEDIA.WECHAT_CIRCLE:
                name = "微信朋友圈";
                break;
            default:
                break;
        }

        return name;
    }

    public static String getTypeDes(int type) {
        String msg = "";
        switch (type) {
            case ShareConstant.AUTH:
                msg = "授权";
                break;
            case ShareConstant.GET_INFO:
                msg = "获取用户信息";
                break;
            case ShareConstant.SHARE:
                msg = "分享";
                break;
            case ShareConstant.NOT_INSTALLED:
                msg = "没得安装";
                break;
            case ShareConstant.IAMGE_LOAD_FAILED:
                msg = "图片加载失败";
                break;
        }

        return msg;
    }

    public static String getPfAuthDesc(int platform) {

        String name = "";

        switch (platform) {
            case ShareConstant.SHARE_MEDIA.QQ:
                name = "qq_auth";
                break;

            case ShareConstant.SHARE_MEDIA.QQ_ZONE:
                name = "qzone_auth";
                break;

            case ShareConstant.SHARE_MEDIA.WECHAT:
                name = "wechat_auth";
                break;

            case ShareConstant.SHARE_MEDIA.SINA:
                name = "sina_auth";
                break;

            default:
                break;
        }

        return name;
    }


    public static ShareListener getDefaultShareListener(Context context) {

        ShareListener shareListener = new ShareListener() {
            @Override
            public void onComplete(int type, int mediaType, Object response) {
                String sInfoFormat = context.getResources().getString(R.string.share_to_platform_success);
                String sFinalInfo = String.format(sInfoFormat, ShareUtils.getPfDesc(mediaType));
                ToastUtil.show(App.getDefault(), sFinalInfo);
            }

            @Override
            public void onCancel(int type, int mediaType) {
                String sInfoFormat = context.getResources().getString(R.string.share_to_platform_cancle);
                String sFinalInfo = String.format(sInfoFormat, ShareUtils.getPfDesc(mediaType));
                ToastUtil.show(App.getDefault(), sFinalInfo);
            }

            @Override
            public void onException(int type, int mediaType, ShareException e) {
                if(type == ShareConstant.NOT_INSTALLED) {
                    ToastUtil.show(App.getDefault(), "请安装最新版" + ShareUtils.getPfDesc(mediaType) + "客户端");
                    return;
                }
                String sInfoFormat = context.getResources().getString(R.string.share_to_platform_failed);
                String sFinalInfo = String.format(sInfoFormat, ShareUtils.getPfDesc(mediaType));
                ToastUtil.show(App.getDefault(), sFinalInfo + ", 请稍后重试~ ");

            }
        };

        return shareListener;
    }
}
