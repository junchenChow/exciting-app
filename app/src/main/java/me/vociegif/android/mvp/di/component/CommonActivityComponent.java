package me.vociegif.android.mvp.di.component;


import dagger.Subcomponent;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.ui.activitys.SplashActivity;
import me.vociegif.android.ui.activitys.VoiceMainActivity;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
@Subcomponent
public interface CommonActivityComponent extends BaseComponent {

    /**
     * @param voiceMainActivity 程序主入口
     */
    void inject(VoiceMainActivity voiceMainActivity);

    /**
     * @param splashActivity 闪屏页面
     */
    void inject(SplashActivity splashActivity);

}
