package me.vociegif.android.mvp.di.modules;

import android.app.Service;
import android.content.Context;

import me.vociegif.android.mvp.di.ContextLevel;
import me.vociegif.android.mvp.di.scope.ServiceScope;

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
public class ServiceModule {
    private Service service;

    public ServiceModule(Service service) {
        this.service = service;
    }

    @Provides
    @ServiceScope
    @ContextLevel(ContextLevel.SERVICE)
    Context getContext() {
        return service;
    }

}
