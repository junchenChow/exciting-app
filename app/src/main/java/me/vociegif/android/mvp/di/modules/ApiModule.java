package me.vociegif.android.mvp.di.modules;



import javax.inject.Singleton;

import me.vociegif.android.mvp.api.VoiceMainApi;

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
@Singleton
public class ApiModule {

    @Provides
    @Singleton
    VoiceMainApi getAccountApi(){
        return new VoiceMainApi();
    }
}
