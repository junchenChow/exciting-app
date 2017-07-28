package me.vociegif.android.mvp.di.modules;

import android.content.Context;

import me.vociegif.android.App;
import me.vociegif.android.helper.MoHoSharedPreferences;
import me.vociegif.android.helper.StickerBuild;
import me.vociegif.android.mvp.di.ContextLevel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
@Module
public class ApplicationModule {

    private App appConfig;

    public ApplicationModule(App appConfig){
        this.appConfig = appConfig;
    }

    @Provides
    @Singleton
    @ContextLevel(ContextLevel.APPLICATION)
    public Context provideContext() {
        return appConfig.getApplicationContext();
    }

    @Provides
    @Singleton
    public MoHoSharedPreferences provideMoHoSharedPreferences(){
        return new MoHoSharedPreferences(appConfig);
    }

    @Provides
    @Singleton
    public StickerBuild provideStickerBuild(){
        return new StickerBuild(appConfig);
    }
}
