package me.vociegif.android.mvp.di.component;


import android.content.Context;

import dagger.Subcomponent;
import me.vociegif.android.mvp.di.ContextLevel;
import me.vociegif.android.mvp.di.modules.FragmentModule;
import me.vociegif.android.mvp.di.scope.FragmentScope;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
@FragmentScope
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {

    @ContextLevel(ContextLevel.FRAGMENT)
    Context context();

    CommonFragmentComponent provideCommonComponent();
}