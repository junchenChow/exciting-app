package me.vociegif.android.helper.download;

import org.wlf.filedownloader.DownloadFileInfo;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public interface OnCurrentDownLoadStatusListener {
    void onDownLoadProgress(int progress);
    void onDownLoadFinish(DownloadFileInfo downloadFileInfo);
    void onDownLoadFailure();
}
