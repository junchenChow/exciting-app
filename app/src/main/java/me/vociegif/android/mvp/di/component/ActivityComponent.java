package me.vociegif.android.mvp.di.component;


import android.content.Context;

import dagger.Subcomponent;
import me.vociegif.android.mvp.di.ContextLevel;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.mvp.di.modules.ActivityModule;
import me.vociegif.android.mvp.di.scope.ActivityScope;
import me.vociegif.android.ui.activitys.SplashActivity;
import me.vociegif.android.ui.activitys.VoiceMainActivity;

import dagger.Component;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent extends BaseComponent {

    @ContextLevel(ContextLevel.ACTIVITY)
    Context context();

    CommonActivityComponent provideCommonComponent();
}
