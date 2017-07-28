package me.vociegif.android.ui.fragments;

import android.app.Activity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.buhuavoice.app.R;
import me.vociegif.android.event.EventGetVoiceResponse;
import me.vociegif.android.helper.utils.MediaUtil;
import me.vociegif.android.helper.utils.ShakeListenerUtils;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.mvp.di.component.FragmentComponent;
import me.vociegif.android.mvp.hepler.ProgressBarHelper;
import me.vociegif.android.ui.activitys.VoiceMainActivity;

import butterknife.Bind;
import me.vociegif.android.ui.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class ShakeMenuFragment extends BaseFragment implements ShakeListenerUtils.ShakeStatusListener {

    @Bind(R.id.iv_pressed_voice)
    ImageView mIvShake;

    private boolean isRegisterShake;
    private VoiceMainActivity mActivity;
    private SensorManager mSensorManager;
    private ShakeListenerUtils mShakeUtils;

    public static ShakeMenuFragment newInstance() {
        return new ShakeMenuFragment();
    }

    @Override
    protected boolean bindEventBusOn() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_shake_menu;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mShakeUtils = new ShakeListenerUtils(this);
        mSensorManager = (SensorManager) getActivity().getSystemService(Service.SENSOR_SERVICE);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mSensorManager != null) {
            if (getUserVisibleHint()) {
                if (!isRegisterShake)
                    mSensorManager.registerListener(mShakeUtils, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                isRegisterShake = true;
            } else {
                if (isRegisterShake)
                    mSensorManager.unregisterListener(mShakeUtils);
                isRegisterShake = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            if (!isRegisterShake)
                mSensorManager.registerListener(mShakeUtils, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            isRegisterShake = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isRegisterShake)
            mSensorManager.unregisterListener(mShakeUtils);
        isRegisterShake = false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (VoiceMainActivity) activity;
    }

    @Override
    public void onShakeComplete() {
        if (mActivity.isShakeComplete()) {
            MediaUtil.playNewShake(getActivity());
            ShakeListenerUtils.shakeAnimator(mIvShake);
            ProgressBarHelper.getInstance(false).show(getActivity());
            EventBus.getDefault().post(new EventGetVoiceResponse(mActivity.getCurrentVoiceText()));
        }
    }
}
