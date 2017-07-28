package me.vociegif.android.mvp.di.component;

import dagger.Subcomponent;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.ui.fragments.InputMenuFragment;
import me.vociegif.android.ui.fragments.ShakeMenuFragment;
import me.vociegif.android.ui.fragments.TheHostVoiceMenuFragment;
import me.vociegif.android.ui.fragments.VoiceMenuFragment;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
@Subcomponent
public interface CommonFragmentComponent extends BaseComponent {

    void inject(TheHostVoiceMenuFragment theHostVoiceMenuFragment);

    void inject(InputMenuFragment discoveryMainFragment);

    void inject(VoiceMenuFragment voiceMenuFragment);

    void inject(ShakeMenuFragment shakeMenuFragment);

}
