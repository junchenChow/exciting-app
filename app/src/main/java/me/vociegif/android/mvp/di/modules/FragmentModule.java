package me.vociegif.android.mvp.di.modules;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import me.vociegif.android.mvp.di.ContextLevel;
import me.vociegif.android.mvp.di.scope.FragmentScope;
import com.trello.rxlifecycle.FragmentLifecycleProvider;

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
public class FragmentModule {

    private FragmentLifecycleProvider fragmentLifecycleProvider;

    public FragmentModule(FragmentLifecycleProvider fragmentLifecycleProvider) {
        this.fragmentLifecycleProvider = fragmentLifecycleProvider;
    }

    @Provides
    @FragmentScope
    FragmentLifecycleProvider getFragmentLifecycleProvider() {
        return this.fragmentLifecycleProvider;
    }

    @Provides
    @FragmentScope
    @ContextLevel(ContextLevel.FRAGMENT)
    Context getContext(){
        return ((Fragment)fragmentLifecycleProvider).getActivity();
    }


    @Provides
    @FragmentScope
    FragmentManager providerFragmentManager() {
        return ((Fragment) fragmentLifecycleProvider).getChildFragmentManager();
    }
}
