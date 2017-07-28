package me.vociegif.android.helper.download;

import me.vociegif.android.helper.utils.ToastUtil;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;
import org.wlf.filedownloader.listener.simple.OnSimpleFileDownloadStatusListener;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class StickerDownLoadHelper {

    public static final int DOWN_TYPE_STICKER_TEXT = 0;
    public static final int DOWN_TYPE_STICKER_ADAPT = 1;
    public static final int DOWN_TYPE_STICKER_SHOP = 2;


    static volatile StickerDownLoadHelper defaultInstance;

    private int type;

    public static StickerDownLoadHelper getDefault() {
        if (defaultInstance == null) {
            synchronized (StickerDownLoadHelper.class) {
                if (defaultInstance == null) {
                    defaultInstance = new StickerDownLoadHelper();
                }
            }
        }
        return defaultInstance;
    }

    private OnCurrentDownLoadStatusListener onCurrentDownLoadStatusListener;

    public void registerCurrentDownLoad(OnCurrentDownLoadStatusListener onCurrentDownLoadStatusListener) {
        this.onCurrentDownLoadStatusListener = onCurrentDownLoadStatusListener;
        FileDownloader.registerDownloadStatusListener(mOnFileDownloadStatusListener);
    }

    public void unRegisterCurrentDownLoad(OnCurrentDownLoadStatusListener onCurrentDownLoadStatusListener) {
        this.onCurrentDownLoadStatusListener = onCurrentDownLoadStatusListener;
        FileDownloader.unregisterDownloadStatusListener(mOnFileDownloadStatusListener);
    }

    public void registerDownLoad(int type) {
        this.type = type;
        FileDownloader.registerDownloadStatusListener(mOnFileDownloadStatusListener);
        FileDownloader.registerDownloadFileChangeListener(mOnDownloadFileChangeListener);
    }

    public void unRegisterDownLoad() {
        FileDownloader.unregisterDownloadStatusListener(mOnFileDownloadStatusListener);
        FileDownloader.unregisterDownloadFileChangeListener(mOnDownloadFileChangeListener);
    }


    private OnFileDownloadStatusListener mOnFileDownloadStatusListener = new OnSimpleFileDownloadStatusListener() {
        @Override
        public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
            // 正在重试下载（如果你配置了重试次数，当一旦下载失败时会尝试重试下载），retryTimes是当前第几次重试
            ToastUtil.show("正在重试下载" + "(" + retryTimes + ")");
        }

        @Override
        public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
            // 等待下载（等待其它任务执行完成，或者FileDownloader在忙别的操作）
        }

        @Override
        public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
            // 准备中（即，正在连接资源）
        }

        @Override
        public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
            // 已准备好（即，已经连接到了资源）
//            onCurrentDownLoadStatusListener.onDownLoadStart();
        }

        @Override
        public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long
                remainingTime) {
            // 正在下载，downloadSpeed为当前下载速度，单位KB/s，remainingTime为预估的剩余时间，单位秒
            onCurrentDownLoadStatusListener.onDownLoadProgress((int) ((downloadFileInfo.getDownloadedSizeLong() * 100) / downloadFileInfo.getFileSizeLong()));

        }

        @Override
        public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
            ToastUtil.show("下载已被暂停");
//            onCurrentDownLoadStatusListener.onDownLoadPause();
        }

        @Override
        public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
            // 下载完成（整个文件已经全部下载完成）
            onCurrentDownLoadStatusListener.onDownLoadFinish(downloadFileInfo);
//            try {
//                String compPath = FileToPhotoUtils.getInstance(MHApplication.getInstance()).getAppDirPath() + "FONT/" + downloadFileInfo.getFileName();
//                File tempFile = FileUtil.createNewFile(compPath);
//                FileUtil.moveAndReplaceFile(downloadFileInfo.getFilePath(),tempFile.getPath());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
            // 下载失败了，详细查看失败原因failReason，有些失败原因你可能必须关心

            String failType = failReason.getType();
            String failUrl = failReason.getUrl();// 或：failUrl = url，url和failReason.getType()会是一样的
            onCurrentDownLoadStatusListener.onDownLoadFailure();
            if (FileDownloadStatusFailReason.TYPE_URL_ILLEGAL.equals(failType)) {
                // 下载failUrl时出现url错误
                ToastUtil.show("url错误");

            } else if (FileDownloadStatusFailReason.TYPE_STORAGE_SPACE_IS_FULL.equals(failType)) {
                // 下载failUrl时出现本地存储空间不足
                ToastUtil.show("本地存储空间不足,下载失败");

            } else if (FileDownloadStatusFailReason.TYPE_NETWORK_DENIED.equals(failType)) {
                // 下载failUrl时出现无法访问网络
                ToastUtil.show("无法访问网络,请重试");
            } else if (FileDownloadStatusFailReason.TYPE_NETWORK_TIMEOUT.equals(failType)) {
                // 下载failUrl时出现连接超时
                ToastUtil.show("连接超时,请重试");
            } else {
                // 更多错误....
                ToastUtil.show("发生未知错误");
            }

            // 查看详细异常信息
            Throwable failCause = failReason.getCause();// 或：failReason.getOriginalCause()

            // 查看异常描述信息
            String failMsg = failReason.getMessage();// 或：failReason.getOriginalCause().getMessage()
        }
    };

    private OnDownloadFileChangeListener mOnDownloadFileChangeListener = new OnDownloadFileChangeListener() {
        @Override
        public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
            // 一个新下载文件被创建，也许你需要同步你自己的数据存储，比如在你的业务数据库中增加一条记录
        }

        @Override
        public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo, Type type) {
            // 一个下载文件被更新，也许你需要同步你自己的数据存储，比如在你的业务数据库中更新一条记录
        }

        @Override
        public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
            // 一个下载文件被删除，也许你需要同步你自己的数据存储，比如在你的业务数据库中删除一条记录
        }
    };
}
