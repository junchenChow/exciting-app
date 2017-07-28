package me.vociegif.android.helper.share.iterface;

import android.graphics.Bitmap;

/**
 * Created by design on 2016/4/15.
 */
public interface GetPictureListener {
    void onSuccess(String url, Bitmap bitmap);
    void onFailed();
}
