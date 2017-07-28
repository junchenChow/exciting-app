package me.vociegif.android.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.buhuavoice.app.R;

import me.vociegif.android.event.EventGetVoiceResponse;
import me.vociegif.android.event.EventTextInput;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.mvp.di.component.CommonFragmentComponent;
import me.vociegif.android.mvp.di.component.FragmentComponent;
import me.vociegif.android.mvp.hepler.ProgressBarHelper;
import me.vociegif.android.ui.activitys.VoiceMainActivity;
import me.vociegif.android.ui.base.BaseFragment;
import me.vociegif.android.widget.PopTextStickerDialog;

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
public class InputMenuFragment extends BaseFragment<BaseComponent, BasePresenter> {

    private VoiceMainActivity activity;

    public static InputMenuFragment newInstance() {
        return new InputMenuFragment();
    }

    @Override
    protected boolean bindEventBusOn() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_input_menu;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Nullable
    @Override
    protected CommonFragmentComponent performInject(FragmentComponent component) {
        return null;
    }

    @Nullable
    @Override
    protected BasePresenter bindPresenter() {
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (VoiceMainActivity) activity;
    }

    @OnClick(R.id.iv_pressed_voice)
    void openTextInput(View view) {
        new PopTextStickerDialog(getActivity()).showThis(activity.getCurrentVoiceText());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInputTextPost(EventTextInput textInput) {
        ProgressBarHelper.getInstance(false).show(getActivity());
        EventBus.getDefault().post(new EventGetVoiceResponse(textInput.getComment()));
    }
}
