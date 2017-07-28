package me.vociegif.android.mvp.di.component;


import me.vociegif.android.mvp.di.modules.ServiceModule;
import me.vociegif.android.mvp.di.scope.ServiceScope;

import dagger.Component;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
@ServiceScope
@Component(dependencies = ApplicationComponent.class,modules = {ServiceModule.class})
public interface ServiceComponent {
}
