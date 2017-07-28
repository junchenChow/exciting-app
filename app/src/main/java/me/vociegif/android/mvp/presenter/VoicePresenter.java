package me.vociegif.android.mvp.presenter;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import org.greenrobot.eventbus.EventBus;
import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnDeleteDownloadFileListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import me.vociegif.android.event.EventStartNewThread;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.MoHoSharedPreferences;
import me.vociegif.android.helper.utils.FileUtil;
import me.vociegif.android.helper.utils.ZipUtil;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.api.VoiceMainApi;
import me.vociegif.android.mvp.hepler.ProgressBarHelper;
import me.vociegif.android.mvp.view.IVoiceView;
import me.vociegif.android.mvp.vo.EventVoiceResponseError;
import me.vociegif.android.mvp.vo.StickerAdapt2Result;
import me.vociegif.android.mvp.vo.StickerContentFull;
import me.vociegif.android.mvp.vo.StickerPaint;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yoh Asakura.
 */
public class VoicePresenter extends BasePresenter<IVoiceView, BasePresenter.State> {
    private String mPath = "";
    private int mStickerIndex;
    private int mCurrentDownloadIndex;

    private VoiceMainApi voiceMainApi;
    private MoHoSharedPreferences mSharedPreferences;
    private StickerAdapt2Result mStickerAdapt2Result;
    private ActivityLifecycleProvider mActivityLifecycleProvider;

    private List<String> mZipUrls = new ArrayList<>();
    private Map<String, Boolean> mZipMap = new HashMap<>();
    private List<StickerPaint> mStickerAllPaint = new ArrayList<>();

    @Inject
    VoicePresenter(VoiceMainApi voiceMainApi, MoHoSharedPreferences mSharedPreferences, ActivityLifecycleProvider activityLifecycleProvider) {
        this.voiceMainApi = voiceMainApi;
        this.mSharedPreferences = mSharedPreferences;
        this.mActivityLifecycleProvider = activityLifecycleProvider;
        mPath = FileUtil.getAppFilePath(FileUtil.DIR_FRAME);
    }


    public void getVoiceResponse(String text) {
        voiceMainApi.getVoiceResponse(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
                .filter(stickerAdapt2Result -> {
                    if (stickerAdapt2Result == null) {
                        ProgressBarHelper.getInstance(true).dismiss();
                        return false;
                    }
                    return true;
                })
                .subscribe(dataResponse -> {
                    if (isViewBound()) {
                        mStickerAdapt2Result = dataResponse;
                        addPaintData(mStickerAdapt2Result);
                        mViewport.bindVoiceData(mStickerAdapt2Result);
                    }
                }, throwable -> {
                    EventBus.getDefault().post(new EventVoiceResponseError(true));
                    ProgressBarHelper.getInstance(true).dismiss();
                    throwable.printStackTrace();
                });
    }

    public void downloaded(DownloadFileInfo downloadInfo) {
        if (downloadInfo.getStatus() == 5 && downloadInfo.getDownloadedSizeLong() == downloadInfo.getFileSizeLong()) {
            Boolean aBoolean = mZipMap.get(downloadInfo.getUrl());
            mZipMap.put(downloadInfo.getUrl(), true);
            Observable.just(aBoolean)
                    .filter(isDownload -> !isDownload)
                    .map(isDownload -> {
                        String zipUrl = ZipUtil.parserZipUrl(downloadInfo.getFileName(), downloadInfo.getFilePath(), mPath);
                        return ZipUtil.upZipFile(new File(zipUrl), mPath);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(isFalse -> {
                        String zipUrl = ZipUtil.parserZipUrl(downloadInfo.getFileName(), downloadInfo.getFilePath(), mPath);
                        boolean isUpZipSucceed = ZipUtil.upZipFile(new File(zipUrl), mPath);
                        if (mZipUrls.size() - 1 == mCurrentDownloadIndex && isViewBound()) {
                            mViewport.beginMergeService();
                            return;
                        }
                        if (isUpZipSucceed) {
                            ++mCurrentDownloadIndex;
                            beginDownloads(mCurrentDownloadIndex, mZipUrls);
                        }
                    }, Throwable::printStackTrace);
        }
    }

    private void getZipUrlsToDownload(StickerAdapt2Result stickerAdapt2Result) {
        mZipUrls.clear();
        mZipMap.clear();
        List<StickerContentFull> fullList = stickerAdapt2Result.getList();
        Observable.from(fullList)
                .filter(stickerContentFull -> (stickerContentFull.getType() == 4))
                .map(stickerContentFull -> {
                    String keyUrl = Constants.setAliyunImageUrl(stickerContentFull.getZip_url());
                    mZipUrls.add(keyUrl);
                    mZipMap.put(keyUrl, false);
                    return keyUrl;
                })
                .toList()
                .doOnNext(this::checkUrlBeginDownload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(keyUrls -> (keyUrls.size() > 0))
                .subscribe(keyUrls -> {
                    if (isViewBound())
                        mViewport.showMergingDialog();
                }, Throwable::printStackTrace);
    }

    private void addPaintData(StickerAdapt2Result stickerAdapt2Result) {
        if (mStickerAllPaint.size() > 0)
            mStickerAllPaint.clear();
        for (StickerContentFull stickerContentFull : stickerAdapt2Result.getList()) {
            String key = String.valueOf(stickerContentFull.getSeriesid()) + "_" + stickerContentFull.getStickerid();
            mSharedPreferences.saveSticker(key, stickerContentFull);
        }
        mStickerAllPaint.addAll(stickerAdapt2Result.getStickerList());
    }

    private void beginDownloads(int currentDownloadIndex, List<String> urls) {
        try {
            FileDownloader.delete(urls.get(currentDownloadIndex), true, new OnDeleteDownloadFileListener() {
                @Override
                public void onDeleteDownloadFilePrepared(DownloadFileInfo downloadFileNeedDelete) {
                }

                @Override
                public void onDeleteDownloadFileSuccess(DownloadFileInfo downloadFileDeleted) {
                    FileDownloader.start(urls.get(currentDownloadIndex));
                }

                @Override
                public void onDeleteDownloadFileFailed(DownloadFileInfo downloadFileInfo, DeleteDownloadFileFailReason failReason) {
                    if (DeleteDownloadFileFailReason.TYPE_FILE_RECORD_IS_NOT_EXIST.equals(failReason.getType())) {
                        FileDownloader.start(urls.get(currentDownloadIndex));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveStickerFrame() {
        List<StickerContentFull> contentFullList = mStickerAdapt2Result.getList();
        if (contentFullList != null)
            for (int i = 0, size = contentFullList.size(); i < size; i++) {
                StickerContentFull stickerContentFull = contentFullList.get(i);
                int type = stickerContentFull.getType();
                if (type == 0 || type == 1) {
                    saveStickers(stickerContentFull);
                }
            }
        getZipUrlsToDownload(mStickerAdapt2Result);
    }

    private void checkUrlBeginDownload(List<String> keyUrls) {
        if (keyUrls.size() > 0) {
            try {
                if (FileUtil.getFolderSize(mPath) > 0) {
                    FileUtil.deleteAllFiles(new File(mPath));
                }
                beginDownloads(mCurrentDownloadIndex, keyUrls);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            EventBus.getDefault().post(new EventStartNewThread(true, ""));
    }

    public void setCurrDownloadIndex(int index) {
        this.mCurrentDownloadIndex = index;
    }

    private void saveStickers(StickerContentFull stickerContentFull) {
        String url = stickerContentFull.getResource_url();
        String name = String.valueOf(stickerContentFull.getSeriesid()) + "_" + stickerContentFull.getStickerid();
        Bitmap bitmap = ImageLoader.getInstance().getMemoryCache().get(url);
        String path = FileUtil.getAppFilePath(FileUtil.DIR_STICKERS) + "/" + name.hashCode();
        if (bitmap != null) {
            FileUtil.saveBitmap(path, bitmap);
        }
    }

    public float conversionProgress(int progress){
        int mergeProgress = mCurrentDownloadIndex * 100 + progress;
        return 40f * (mergeProgress / (100f * mZipUrls.size()));
    }

    public StickerAdapt2Result getStickerAdapt2Result() {
        return mStickerAdapt2Result;
    }

    public void continueStickerIndex(){
        mStickerIndex++;
    }

    public VoicePresenter setStickerIndex(int mStickerIndex) {
        this.mStickerIndex = mStickerIndex;
        return this;
    }

    public int getStickerIndex() {
        return mStickerIndex;
    }

    public List<StickerPaint> getStickerAllPaint() {
        return mStickerAllPaint;
    }

    public MoHoSharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
}
