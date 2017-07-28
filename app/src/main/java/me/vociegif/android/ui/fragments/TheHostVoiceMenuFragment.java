package me.vociegif.android.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.buhuavoice.app.R;
import me.vociegif.android.event.EventChangeItem;
import me.vociegif.android.event.EventShare;
import me.vociegif.android.event.EventStartMainThread;
import me.vociegif.android.helper.share.entity.ShareException;
import me.vociegif.android.helper.share.iterface.ShareListener;
import me.vociegif.android.helper.share.utils.ShareConstant;
import me.vociegif.android.helper.share.utils.ShareUtils;
import me.vociegif.android.helper.utils.ToastUtil;
import me.vociegif.android.helper.viewanimator.ViewAnimator;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.mvp.di.component.FragmentComponent;
import me.vociegif.android.mvp.hepler.ProgressBarHelper;
import me.vociegif.android.ui.activitys.VoiceMainActivity;
import me.vociegif.android.ui.adapters.MyFragmentPagerAdapter;
import me.vociegif.android.ui.base.BaseFragment;
import me.vociegif.android.widget.VoiceIndicatorView;

import butterknife.Bind;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class TheHostVoiceMenuFragment extends BaseFragment<BaseComponent, BasePresenter> implements ViewPager.OnPageChangeListener, ShareListener {

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.ll_share)
    LinearLayout mLlShare;
    @Bind(R.id.view_indicator)
    VoiceIndicatorView mVoiceIndicatorView;

    private String mMergePath;
    private VoiceMainActivity mVoiceMainActivity;

    public static TheHostVoiceMenuFragment newInstance() {
        return new TheHostVoiceMenuFragment();
    }

    @Override
    protected boolean bindEventBusOn() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_host_voice;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        fragmentPagerAdapter.addFragment(InputMenuFragment.newInstance());
        fragmentPagerAdapter.addFragment(VoiceMenuFragment.newInstance());
        fragmentPagerAdapter.addFragment(ShakeMenuFragment.newInstance());
        mViewPager.setCurrentItem(1);
    }

    @Nullable
    @Override
    protected BaseComponent performInject(FragmentComponent component) {
        return null;
    }

    @Nullable
    @Override
    protected BasePresenter bindPresenter() {
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mVoiceIndicatorView.switchIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mVoiceMainActivity = (VoiceMainActivity) activity;
        VoiceMainActivity.mShareManager.setShareListener(this);
    }

    @OnClick({R.id.tvQzone, R.id.tvQFriend, R.id.tvWechatFriend, R.id.tvWechat, R.id.tvWeibo})
    void onclick(View view) {
        if (VoiceMainActivity.mShareManager == null)
            return;
        EventBus.getDefault().post(new EventShare(true));
        VoiceMainActivity.mShareManager.onDestroyItem();
        VoiceMainActivity.mShareManager.setShareImage(mMergePath, mMergePath, mMergePath);

        switch (view.getId()) {

            case R.id.tvQzone:
                VoiceMainActivity.mShareManager.share(ShareConstant.SHARE_MEDIA.QQ_ZONE, "");
                break;

            case R.id.tvQFriend:
                VoiceMainActivity.mShareManager.share(ShareConstant.SHARE_MEDIA.QQ, "");
                break;

            case R.id.tvWechatFriend:
                if (mVoiceMainActivity.isMergePath())
                    VoiceMainActivity.mShareManager.shareEmoticon(getActivity(), mMergePath, false);
                else
                    VoiceMainActivity.mShareManager.share(ShareConstant.SHARE_MEDIA.WECHAT, "");
                break;

            case R.id.tvWechat:
                VoiceMainActivity.mShareManager.share(ShareConstant.SHARE_MEDIA.WECHAT_CIRCLE, "");
                break;

            case R.id.tvWeibo:
                VoiceMainActivity.mShareManager.share(ShareConstant.SHARE_MEDIA.SINA, "");
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().post(new EventShare(false));
    }

    @Override
    public void onComplete(int type, int mediaType, Object response) {
        String sInfoFormat = getResources().getString(R.string.share_to_platform_success);
        String sFinalInfo = String.format(sInfoFormat, ShareUtils.getPfDesc(mediaType));
        ToastUtil.show(sFinalInfo);
        VoiceMainActivity.mShareManager.onDestroyItem();
        EventBus.getDefault().post(new EventShare(false));
    }

    @Override
    public void onCancel(int type, int mediaType) {
        String sInfoFormat = getResources().getString(R.string.share_to_platform_cancle);
        String sFinalInfo = String.format(sInfoFormat, ShareUtils.getPfDesc(mediaType));
        ToastUtil.show(sFinalInfo);
    }

    @Override
    public void onException(int type, int mediaType, ShareException e) {
        EventBus.getDefault().post(new EventShare(false));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventChangeItem changeItem) {
        mViewPager.setCurrentItem(changeItem.getIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventShare share) {
        if (share.isShare())
            ProgressBarHelper.getInstance(true).show(getActivity(), "正在进行分享, 请稍后");
        else
            ProgressBarHelper.getInstance(true).dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventStartMainThread mainThread) {
        mMergePath = mainThread.getPath();
        if (mainThread.isCreate()) {
            ProgressBarHelper.getInstance(false).dismiss();
            ViewAnimator.animate(mVoiceIndicatorView).fadeOut().accelerate().duration(600).start();
            ViewAnimator.animate(mLlShare).fadeIn().accelerate().duration(600)
                    .onStart(() -> {
                        mViewPager.setVisibility(View.GONE);
                        mLlShare.setVisibility(View.VISIBLE);
                    })
                    .start();
        } else {
            ViewAnimator.animate(mVoiceIndicatorView).fadeIn().accelerate().duration(600).start();
            ViewAnimator.animate(mLlShare).fadeOut().accelerate().duration(600)
                    .onStop(() -> {
                        mViewPager.setVisibility(View.VISIBLE);
                        mLlShare.setVisibility(View.GONE);
                    })
                    .start();
        }
    }
}
