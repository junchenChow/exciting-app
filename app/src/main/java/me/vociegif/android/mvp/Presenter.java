package me.vociegif.android.mvp;

import android.support.annotation.NonNull;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public interface Presenter<V> {

    void bindView(@NonNull V view);

    void destroy();

    void start();
}
