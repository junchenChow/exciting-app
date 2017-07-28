package me.vociegif.android.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public abstract class BasePresenter<V, S extends BasePresenter.State> implements Presenter<V> {

    protected V mViewport;
    protected S mState;
    protected Context mContext;

    /**
     * Define presenter state interface
     */
    public interface State {
        void recycle();

        void resume(Bundle savedInstanceState);

        void saveInstanceState(Bundle outState);
    }

    public BasePresenter() {
        mState = initState();
    }

    @Override
    public void bindView(@NonNull V view) {
        mViewport = view;
        if (view instanceof Activity) {
            mContext = (Activity) view;
        } else if (view instanceof Fragment) {
            mContext = ((Fragment) view).getContext();
        }
    }

    @Nullable
    public S initState() {
        return null;
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void destroy() {
        if (mState != null) {
            mState.recycle();
        }
        mViewport = null;
        mContext = null;
    }

    protected boolean isViewBound() {
        return mViewport != null;
    }

    /**
     * Simple implementation as {@link State}
     */
    public static class SimpleState implements State {
        @Override
        public void recycle() {
        }

        @Override
        public void resume(Bundle savedInstanceState) {
        }

        @Override
        public void saveInstanceState(Bundle outState) {
        }
    }

}
