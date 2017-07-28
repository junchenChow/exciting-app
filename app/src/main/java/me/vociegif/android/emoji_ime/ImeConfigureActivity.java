package me.vociegif.android.emoji_ime;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.buhuavoice.app.R;

import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.mvp.di.component.ActivityComponent;
import me.vociegif.android.ui.base.BaseToolBarActivity;

public class ImeConfigureActivity extends BaseToolBarActivity {


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_ime_configure;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Nullable
    @Override
    protected BaseComponent performInject(ActivityComponent component) {
        return null;
    }

    @Nullable
    @Override
    protected BasePresenter bindPresenter() {
        return null;
    }
}
