package me.vociegif.android.ui.activitys;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.buhuavoice.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import javax.inject.Inject;

import butterknife.Bind;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.mvp.di.component.ActivityComponent;
import me.vociegif.android.mvp.di.component.CommonActivityComponent;
import me.vociegif.android.mvp.presenter.SplashPresenter;
import me.vociegif.android.ui.base.BaseToolBarActivity;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class SplashActivity extends BaseToolBarActivity<CommonActivityComponent, SplashPresenter> {

    @Bind(R.id.iv_splash)
    ImageView mIvSplashCover;
    @Bind(R.id.iv_360)
    ImageView mIv360Icon;

    @Inject
    SplashPresenter mPresenter;

    private DisplayImageOptions mDefaultOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageForEmptyUri(R.mipmap.icon_splash)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .displayer(new FadeInBitmapDisplayer(400))
            .imageScaleType(ImageScaleType.NONE)
            .build();

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (Constants.getAppCannel().equals("360app")) mIv360Icon.setVisibility(View.VISIBLE);
        else mIv360Icon.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageLoader.getInstance().displayImage(mPresenter.getSplashImageFilePath(), mIvSplashCover, mDefaultOptions);
        mPresenter.checkVersionUpdate();
        mPresenter.openVoiceMainActivity(this);
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
    protected SplashPresenter bindPresenter() {
        return mPresenter;
    }
}
