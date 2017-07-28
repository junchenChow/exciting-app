package me.vociegif.android.helper.share.iterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Created by design on 2016/3/14.
 */
public interface Share {

    void setSavePath(String savePath);

    void auth(Activity activity); // 授权

    void login(Activity activity); // 登录

    void share(int platform, final String url, final Activity activity); // 分享

    void share(Activity activity, Bitmap shareBitmap, Bitmap thumbBitmap, int platform); // 主要為了微信分享

    void onActivityResult(int requestCode, int resultCode, Intent data); // 转发数据

    boolean isAuthuried(); // 判断是否授权

    boolean isInstalled(Context context); // 判断是否安装

    void setShareListener(ShareListener shareListener); // 设置监听

    void onNewIntent(Context context, Intent intent); // 新浪分享需要用的

    void setContentId(String contentId);

    void onDestroy();

    void shareGif(Activity context, String url);
}
