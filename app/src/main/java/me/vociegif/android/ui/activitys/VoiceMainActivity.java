package me.vociegif.android.ui.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buhuavoice.app.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.wlf.filedownloader.DownloadFileInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import me.vociegif.android.App;
import me.vociegif.android.event.EventFocusCancel;
import me.vociegif.android.event.EventGetVoiceResponse;
import me.vociegif.android.event.EventMergeFinish;
import me.vociegif.android.event.EventMergeProgress;
import me.vociegif.android.event.EventStartMainThread;
import me.vociegif.android.event.EventStartNewThread;
import me.vociegif.android.event.EventStickerAnalysis;
import me.vociegif.android.event.EventVersionUpdate;
import me.vociegif.android.gif.MergeService;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.ConstantsPath;
import me.vociegif.android.helper.StickerBuild;
import me.vociegif.android.helper.download.OnCurrentDownLoadStatusListener;
import me.vociegif.android.helper.download.StickerDownLoadHelper;
import me.vociegif.android.helper.download.StickerManager2;
import me.vociegif.android.helper.share.ShareManager;
import me.vociegif.android.helper.utils.CommonUtil;
import me.vociegif.android.helper.utils.FileToPhotoUtils;
import me.vociegif.android.helper.utils.FileUtil;
import me.vociegif.android.helper.utils.ToastUtil;
import me.vociegif.android.helper.viewanimator.ViewAnimator;
import me.vociegif.android.mvp.di.component.ActivityComponent;
import me.vociegif.android.mvp.di.component.CommonActivityComponent;
import me.vociegif.android.mvp.hepler.ProgressBarHelper;
import me.vociegif.android.mvp.presenter.VoicePresenter;
import me.vociegif.android.mvp.view.IVoiceView;
import me.vociegif.android.mvp.vo.EventVoiceResponseError;
import me.vociegif.android.mvp.vo.StickerAdapt2Result;
import me.vociegif.android.mvp.vo.StickerContentFull;
import me.vociegif.android.mvp.vo.StickerPaint;
import me.vociegif.android.mvp.vo.VersionUpdateEntity;
import me.vociegif.android.ui.base.BaseToolBarActivity;
import me.vociegif.android.ui.fragments.TheHostVoiceMenuFragment;
import me.vociegif.android.widget.AutoUpdateDialog;
import me.vociegif.android.widget.MergeLoadingDialog;
import me.vociegif.android.widget.MyHighlightView;
import me.vociegif.android.widget.MyImageViewDrawableOverlay;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class VoiceMainActivity extends BaseToolBarActivity<CommonActivityComponent, VoicePresenter> implements IVoiceView, OnCurrentDownLoadStatusListener {

    private static final String FRG_VOICE_MENU = "voice_menu";
    public static final String CHECK_VERSION = "version_result";

    @Bind(R.id.tv_back)
    TextView mTvBack;
    @Bind(R.id.view_line)
    View mViewLine;
    @Bind(R.id.tv_create)
    TextView mTvCreate;
    @Bind(R.id.cd_content)
    CardView mCdContent;
    @Bind(R.id.iv_save_local)
    ImageView mIvSaveLocal;
    @Bind(R.id.iv_background)
    ImageView mIvBackground;
    @Bind(R.id.gif_content_view)
    GifImageView mGifImageView;
    @Bind(R.id.viewDrawableOverlay)
    MyImageViewDrawableOverlay mViewDrawableOverlay;

    @Inject
    VoicePresenter mVoicePresenter;
    @Inject
    StickerBuild mStickerBuild;

    private String mVoiceText = "";
    private String mCompleterPath = "";

    private boolean isMergePath = false;
    private boolean isShakeComplete = true;
    private boolean isAutoUpdateShow = false;

    private StickerPaint mCurLoadStickerPaint;
    private StickerContentFull mStickerContent;

    private AutoUpdateDialog mAutoUpdateDialog;
    private MergeLoadingDialog mStickerTextDownLoadDialog;

    public static ShareManager mShareManager = new ShareManager();

    @Override
    protected boolean bindEventBusOn() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_voice_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mShareManager.onCreate(this);
        setUpStatusBar();
        getIntents();
        adjustDrawableOverlayLayoutParams();
        setupMainFragment(savedInstanceState);
        StickerDownLoadHelper.getDefault().registerCurrentDownLoad(this);
    }

    @Nullable
    @Override
    protected CommonActivityComponent performInject(ActivityComponent component) {
        CommonActivityComponent commonActivityComponent = component.provideCommonComponent();
        commonActivityComponent.inject(this);
        return commonActivityComponent;
    }

    @Nullable
    @Override
    protected VoicePresenter bindPresenter() {
        mVoicePresenter.bindView(this);
        return mVoicePresenter;
    }

    @Override
    public void getIntents() {
        VersionUpdateEntity versionUpdateEntity = (VersionUpdateEntity) getIntent().getSerializableExtra(CHECK_VERSION);
        if (versionUpdateEntity != null) {
            mAutoUpdateDialog = new AutoUpdateDialog(this);
            mAutoUpdateDialog.show();
            mAutoUpdateDialog.setNewVersionData(versionUpdateEntity);
            isAutoUpdateShow = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StickerDownLoadHelper.getDefault().unRegisterCurrentDownLoad(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mShareManager != null) {
            mShareManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void bindVoiceData(StickerAdapt2Result stickerAdapt2Result) {
        mViewDrawableOverlay.post(this::loadSticker);
        if (stickerAdapt2Result.getScenes().size() > 0)
            ImageLoader.getInstance().displayImage(Constants.setAliyunImageUrl(stickerAdapt2Result.getScenes().get(0).getImgname_url()), mIvBackground, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    Observable.just(loadedImage)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .filter(loadedImg -> loadedImg != null && !loadedImg.isRecycled())
                            .subscribe(loadedImg -> {
                                int hashName = FileUtil.STICKER_BG_NAME.hashCode();
                                FileUtil.saveBitmap(FileUtil.getAppFilePath(FileUtil.DIR_STICKERS) + hashName, loadedImage);
                            }, Throwable::printStackTrace);
                }
            });
    }

    @Override
    public void beginMergeService() {
        MergeService.intentMergeService(this, mVoicePresenter.getStickerAdapt2Result());
    }

    @Override
    public void showMergingDialog() {
        mStickerTextDownLoadDialog = new MergeLoadingDialog(this);
        mStickerTextDownLoadDialog.show(this);
    }

    @Override
    public void onDownLoadProgress(int progress) {
        mStickerTextDownLoadDialog.setProgress(mVoicePresenter.conversionProgress(progress));
    }

    @Override
    public void onDownLoadFinish(DownloadFileInfo downloadInfo) {
        mVoicePresenter.downloaded(downloadInfo);
    }

    @Override
    public void onDownLoadFailure() {
        if (mStickerTextDownLoadDialog != null)
            mStickerTextDownLoadDialog.dismiss();
    }

    @OnClick({R.id.tv_create, R.id.tv_back, R.id.iv_save_local})
    void onClicks(View view) {
        switch (view.getId()) {
            case R.id.tv_create:
                if (mViewDrawableOverlay.getmOverlayViews().size() <= 0)
                    return;
                mVoicePresenter.setCurrDownloadIndex(0);
                mVoicePresenter.saveStickerFrame();
                break;
            case R.id.tv_back:
                EventBus.getDefault().post(new EventStartMainThread(mCompleterPath, false));
                break;
            case R.id.iv_save_local:
                try {
                    String footer = isMergePath ? FileToPhotoUtils.IMAGE_TYPE_GIF : FileToPhotoUtils.IMAGE_TYPE_JPG;
                    FileUtil.copyAndReplaceFile(mCompleterPath, ConstantsPath.getGLYPath(footer));
                    ToastUtil.showLong(this, "文件已经保存到路径: " + ConstantsPath.getGLYPath(footer));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setupMainFragment(Bundle savedInstanceState) {
        Fragment menuFragment;
        if (savedInstanceState != null) {
            menuFragment = getSupportFragmentManager().findFragmentByTag(FRG_VOICE_MENU);
            if (menuFragment == null) {
                menuFragment = TheHostVoiceMenuFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.fl_menu, menuFragment, FRG_VOICE_MENU).commit();
            }
            getSupportFragmentManager().beginTransaction().show(menuFragment).commit();
        } else {
            menuFragment = TheHostVoiceMenuFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.fl_menu, menuFragment, FRG_VOICE_MENU).commit();
            getSupportFragmentManager().beginTransaction().show(menuFragment).commitAllowingStateLoss();
        }
    }

    private void adjustDrawableOverlayLayoutParams() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mViewLine.setVisibility(View.GONE);
        } else {
            mViewLine.setVisibility(View.VISIBLE);
        }
        ViewGroup.LayoutParams layoutParams = mCdContent.getLayoutParams();
        layoutParams.height = App.getDefault().getScreenWidth();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mCdContent.setLayoutParams(layoutParams);
    }

    private synchronized void clearCurrentStickers() {
        mVoicePresenter.setStickerIndex(0);
        if (mViewDrawableOverlay.getmOverlayViews() != null && mViewDrawableOverlay.getmOverlayViews().size() > 0) {
            for (MyHighlightView myHighlightView : mViewDrawableOverlay.getmOverlayViews()) {
                mViewDrawableOverlay.removeHightlightView(myHighlightView);
            }
        }
    }

    private void loadSticker() {
        int stickerIndex = mVoicePresenter.getStickerIndex();
        List<StickerPaint> stickerAllPaint = mVoicePresenter.getStickerAllPaint();
        if (stickerIndex >= stickerAllPaint.size()) {
            isShakeComplete = true;
            ProgressBarHelper.getInstance(false).dismiss();
            EventBus.getDefault().post(new EventFocusCancel());
        } else {
            StickerPaint stickerPaint = stickerAllPaint.get(stickerIndex);
            String key = String.valueOf(stickerPaint.getSeriesid()) + "_" + stickerPaint.getStickerid();
            if (stickerPaint.getType() == 1)
                loadStickers(-1, mVoicePresenter.getSharedPreferences().getStickerContentFullById(key), stickerPaint);
            else if (stickerPaint.getType() == 0)
                loadStickers(0, mVoicePresenter.getSharedPreferences().getStickerContentFullById(key), stickerPaint);
        }
    }

    private void loadStickers(int type, StickerContentFull stickerContentFull, StickerPaint stickerPaint) {
        mStickerContent = stickerContentFull;
        mCurLoadStickerPaint = stickerPaint;
        mCurLoadStickerPaint.setTextRange(stickerContentFull.getTextRange());
        App.getDefault().LoadStickerBg(mStickerContent.getResource_url(), null, type);
    }

    private void setupStickerPhoto(Bitmap data, MyImageViewDrawableOverlay overlay, StickerPaint curLoadStickerPaint) {
        if (curLoadStickerPaint == null) {
            this.mCurLoadStickerPaint = new StickerPaint();
            this.mCurLoadStickerPaint.setSeriesid(mStickerContent.getSeriesid());
            this.mCurLoadStickerPaint.setStickerid(mStickerContent.getStickerid());
            this.mCurLoadStickerPaint.setType(mStickerContent.getType());
        }
        mStickerBuild.addStickerImage(overlay, curLoadStickerPaint, data, false);
    }

    private void setupStickerText(Bitmap data, MyImageViewDrawableOverlay overlay, StickerPaint curLoadStickerPaint) {
        if (curLoadStickerPaint == null) {
            this.mCurLoadStickerPaint = new StickerPaint();
            this.mCurLoadStickerPaint.setSeriesid(mStickerContent.getSeriesid());
            this.mCurLoadStickerPaint.setStickerid(mStickerContent.getStickerid());
            this.mCurLoadStickerPaint.setType(mStickerContent.getType());
            this.mCurLoadStickerPaint.setTextRange(mStickerContent.getTextRange());
            this.mCurLoadStickerPaint.setColor(ContextCompat.getColor(this, R.color.black));
            this.mCurLoadStickerPaint.setFontSize((int) ConstantsPath.DEFAULTINPUTTEXTSIZE);
            this.mCurLoadStickerPaint.setText("双击输入文字");
        }
        mStickerBuild.addTextSticker(overlay, curLoadStickerPaint, data, null);
        ProgressBarHelper.getInstance(true).dismiss();
    }

    private String createTempPhoto() {
        Bitmap bitmap = Bitmap.createBitmap(mCdContent.getWidth(), mCdContent.getHeight(), Bitmap.Config.ARGB_8888);
        mCdContent.draw(new Canvas(bitmap));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
        try {
            File file = FileUtil.createFile(FileToPhotoUtils.getInstance().getAppDirPath() + FileToPhotoUtils.TEMP_NAME + FileToPhotoUtils.IMAGE_TYPE_JPG);
            return FileToPhotoUtils.saveBitmapToLocal(scaledBitmap, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return "";
    }

    public String getCurrentVoiceText() {
        return mVoiceText;
    }

    public boolean isMergePath() {
        return isMergePath;
    }

    public boolean isShakeComplete() {
        return isShakeComplete;
    }

    /****************************************************************************************************
     * events
     ****************************************************************************************************/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getStickersResponseEvent(EventGetVoiceResponse getResponse) {
        clearCurrentStickers();
        isShakeComplete = false;
        mVoiceText = getResponse.getText();
        mVoicePresenter.getVoiceResponse(getResponse.getText());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShakeEvent(EventVoiceResponseError event) {
        isShakeComplete = event.isShakeComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void analysisStickerEvent(EventStickerAnalysis evt) {
        switch (evt.getSucceed()) {
            case StickerManager2.STICKER_LOAD_FINISH: {
                mVoicePresenter.continueStickerIndex();
                if (mStickerContent == null)
                    return;
                if (!mStickerContent.getResource_url().equals(evt.getUrl()))
                    return;
                if (evt.getParam() == -1)
                    setupStickerText(evt.getBitmap(), mViewDrawableOverlay, mCurLoadStickerPaint);
                else if (evt.getParam() == 0)
                    setupStickerPhoto(evt.getBitmap(), mViewDrawableOverlay, mCurLoadStickerPaint);
            }
            loadSticker();
            break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMergedEvent(EventMergeFinish mergeService) {
        ProgressBarHelper.getInstance(true).dismiss();
        try {
            mViewDrawableOverlay.setVisibility(View.GONE);
            ViewAnimator.animate(mGifImageView).fadeIn().accelerate().duration(600).start();
            mGifImageView.setVisibility(View.VISIBLE);
            GifDrawable gifFromBytes = new GifDrawable(mergeService.getGifPath());
            gifFromBytes.setLoopCount(0xFFFF);
            mGifImageView.setImageDrawable(gifFromBytes);
            mStickerTextDownLoadDialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String mergePath = mergeService.getGifPath();
        EventBus.getDefault().post(new EventStartNewThread(true, mergePath));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMergingProgress(EventMergeProgress mergeProgress) {
        mStickerTextDownLoadDialog.setProgress(mergeProgress.getProgress());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onContentAnimEvent(EventStartNewThread newThread) {
        if (newThread.isCreate()) {
            if (!TextUtils.isEmpty(newThread.getPhotoPath())) {
                isMergePath = true;
                mCompleterPath = newThread.getPhotoPath();
            } else {
                isMergePath = false;
                mCompleterPath = createTempPhoto();
            }
            EventBus.getDefault().post(new EventStartMainThread(mCompleterPath, true));
        } else {
            EventBus.getDefault().post(new EventStartMainThread(mCompleterPath, false));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContentAnimToggleEvent(EventStartMainThread mainThread) {
        if (mainThread.isCreate()) {
            ProgressBarHelper.getInstance(false).dismiss();
            ViewAnimator.animate(mCdContent).scale(0.6f).accelerate().duration(600).thenAnimate(mIvSaveLocal).fadeIn().duration(600).onStart(() -> mIvSaveLocal.setVisibility(View.VISIBLE)).start();
            ViewAnimator.animate(mTvCreate).translationX(0, 400).accelerate().duration(300).onStop(() -> mTvBack.setVisibility(View.VISIBLE)).thenAnimate(mTvBack).translationX(-400, 0).duration(300).start();
        } else {
            if (!TextUtils.isEmpty(mainThread.getPath())) {
                mViewDrawableOverlay.setVisibility(View.VISIBLE);
                mGifImageView.setImageDrawable(null);
                mGifImageView.setVisibility(View.GONE);
            }
            ViewAnimator.animate(mCdContent).scale(1.0f).accelerate().duration(600).onStart(() -> mIvSaveLocal.setVisibility(View.INVISIBLE)).start();
            ViewAnimator.animate(mTvBack).translationX(-400).accelerate().duration(300).thenAnimate(mTvCreate).translationX(400, 0).duration(300).start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVersionUpdateEvent(EventVersionUpdate versionUpdate) {
        if (!isAutoUpdateShow && mAutoUpdateDialog != null) {
            mAutoUpdateDialog = new AutoUpdateDialog(this);
            mAutoUpdateDialog.setNewVersionData(versionUpdate.getVersionResult());
            mAutoUpdateDialog.show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return CommonUtil.tryClickBackAgain(keyCode, event, this) || super.onKeyDown(keyCode, event);
    }
}
 