package me.vociegif.android.helper.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import me.vociegif.android.helper.share.iterface.Share;
import me.vociegif.android.helper.share.iterface.ShareListener;


/**
 * Created by design on 2016/3/23.
 */
public class BaseShare implements Share {

    protected int ShareType;
    protected String contentId;
    protected String savePath;

    protected ShareListener shareListener;

    @Override
    public void auth(Activity activity) {

    }

    @Override
    public void login(Activity activity) {

    }

    @Override
    public void share(int platform, String url, Activity activity) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean isAuthuried() {
        return true;
    }

    @Override
    public boolean isInstalled(Context context) {
        return false;
    }


    @Override
    public void setShareListener(ShareListener shareListener) {
        this.shareListener = shareListener;
    }

    @Override
    public void onNewIntent(Context context, Intent intent) {

    }

    @Override
    public void share(Activity activity, Bitmap shareBitmap, Bitmap thumbBitmap, int platform) {

    }

    @Override
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    @Override
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void shareGif(Activity context, String url) {

    }
}
