package me.vociegif.android.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import me.vociegif.android.App;
import me.vociegif.android.event.EventServerError;
import me.vociegif.android.helper.utils.ToastUtil;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.mvp.di.component.FragmentComponent;
import me.vociegif.android.mvp.di.modules.FragmentModule;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public abstract class BaseFragment<C extends BaseComponent, P extends BasePresenter> extends RxFragment {

    private C mComponent;               // dependencies injector component interface
    private P mPagePresenter;           // instance presenter

    private boolean mBoundEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPrepareInstanceState(savedInstanceState);
        initInject();
        mPagePresenter = bindPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (bindEventBusOn() && !EventBus.getDefault().isRegistered(this)) {
            mBoundEvent = true;
            EventBus.getDefault().register(this);
        }
        onPrepareViewState(savedInstanceState);
        final int layoutRes = getLayoutRes();
        if (layoutRes != 0) {
            return inflater.inflate(layoutRes, null);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (mBoundEvent && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        recycle();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initView(view, savedInstanceState);
    }

    /**
     * Provide component injector.
     */
    public C getComponent() {
        return mComponent;
    }

    /**
     * Provide page presenter.
     */
    public P getPresenter() {
        return mPagePresenter;
    }

    public CharSequence getPageTitle() {
        return "";
    }

    protected final void initInject() {
        FragmentComponent fragmentComponent = App.getDefault()
                .getApplicationComponent()
                .provideFragmentComponent(new FragmentModule(this));
        mComponent = performInject(fragmentComponent);
    }

    /**
     * Provide current activity layout res.
     */
    @LayoutRes
    protected abstract int getLayoutRes();

    /**
     * Init page view components.
     *
     * @param view               page content view
     * @param savedInstanceState saved instance state bundle
     * @see #onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    /**
     * Perform inject behavior.
     *
     * @param component {@link FragmentComponent} activity component
     */
    @Nullable
    protected abstract C performInject(FragmentComponent component);

    /**
     * Init presenter and bind to current instance.
     *
     * @return {@link P} current page presenter
     */
    @Nullable
    protected abstract P bindPresenter();

    public String getPageTitle4Statistics() {
        return "";
    }

    /**
     * Called when setUserVisibleHint set and visible.
     */
    protected void onUserVisible() {
    }

    /**
     * Called when {@link #onDestroy()} invoked.
     */
    protected void recycle() {
        if (mPagePresenter != null) {
            mPagePresenter.destroy();
        }
        mComponent = null;
    }

    /**
     * <b>Prepare instance data & state.</b>
     * <p/>
     * Called while create instance.
     */
    protected void onPrepareInstanceState(@Nullable Bundle savedInstanceState) {
    }

    /**
     * <b>Prepare view components' data & state.</b>
     * <p/>
     * Called while create instance view.
     */
    protected void onPrepareViewState(@Nullable Bundle savedInstanceState) {
    }

    /**
     * Determine whether this activity bind EventBus for receive event.
     *
     * @return false default
     */
    protected boolean bindEventBusOn() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerErrorHint(EventServerError serverError) {
        ToastUtil.show(serverError.getErrorMsg());
    }
}