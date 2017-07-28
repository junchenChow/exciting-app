package me.vociegif.android.helper.share.iterface;


import me.vociegif.android.helper.share.entity.ShareException;

/**
 * Created by design on 2016/3/21.
 *
 */
public interface ShareListener {

    void onComplete(int type, int mediaType, Object response);

    void onCancel(int type, int mediaType);

    void onException(int type, int mediaType, ShareException e);

}
