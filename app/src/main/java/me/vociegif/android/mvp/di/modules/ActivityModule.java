package me.vociegif.android.mvp.di.modules;

import android.app.Activity;
import android.content.Context;

import me.vociegif.android.mvp.di.ContextLevel;
import me.vociegif.android.mvp.di.scope.ActivityScope;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

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
public class ActivityModule {

    private ActivityLifecycleProvider activityLifecycleProvider;

    public ActivityModule(ActivityLifecycleProvider activityLifecycleProvider) {
        this.activityLifecycleProvider = activityLifecycleProvider;
    }

    @Provides
    @ActivityScope
    ActivityLifecycleProvider getActivityLifecycleProvider() {
        return this.activityLifecycleProvider;
    }

    @Provides
    @ActivityScope
    @ContextLevel(ContextLevel.ACTIVITY)
    Context getContext() {
        return (Activity) this.activityLifecycleProvider;
    }

}