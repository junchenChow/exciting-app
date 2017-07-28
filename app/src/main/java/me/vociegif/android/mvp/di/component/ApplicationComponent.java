package me.vociegif.android.mvp.di.component;

import android.content.Context;

import me.vociegif.android.App;
import me.vociegif.android.helper.MoHoSharedPreferences;
import me.vociegif.android.helper.StickerBuild;
import me.vociegif.android.mvp.di.ContextLevel;
import me.vociegif.android.mvp.di.modules.ActivityModule;
import me.vociegif.android.mvp.di.modules.ApiModule;
import me.vociegif.android.mvp.di.modules.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;
import me.vociegif.android.mvp.di.modules.FragmentModule;

/**
 * Created by Liquor .
 * on 15/12/31 .
 */
@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class})
public interface ApplicationComponent {

    void inject(App appConfig);

    @ContextLevel(ContextLevel.APPLICATION)
    Context getContext();

    ActivityComponent provideActivityComponent(ActivityModule activityModule);

    FragmentComponent provideFragmentComponent(FragmentModule fragmentModule);

    StickerBuild getStickerBuild();

    MoHoSharedPreferences getMoHoSharedPreferences();
}
