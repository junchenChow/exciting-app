package me.vociegif.android.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AnimRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;

import com.buhuavoice.app.R;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import me.vociegif.android.App;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.mvp.di.component.ActivityComponent;
import me.vociegif.android.mvp.di.modules.ActivityModule;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public abstract class BaseActivity<C extends BaseComponent, P extends BasePresenter> extends RxAppCompatActivity {

    @AnimRes
    private int mStartEnterAnim;
    @AnimRes
    private int mStartExitAnim;
    @AnimRes
    private int mStopEnterAnim;
    @AnimRes
    private int mStopExitAnim;

    private C mComponent;               // dependencies injector component interface
    private P mPagePresenter;           // instance presenter

    private boolean mBoundEvent;
    private boolean mReadyToClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bindEventBusOn()) {
            mBoundEvent = true;
            EventBus.getDefault().register(this);
        }
        createInstance(savedInstanceState);
    }


    @Override
    protected void onPause() {
        if (mReadyToClose) {
            overridePendingTransition(mStopEnterAnim, mStopExitAnim);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mBoundEvent) {
            EventBus.getDefault().unregister(this);
        }
        recycle();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuRes = getMenuRes();
        if (menuRes > 0) {
            getMenuInflater().inflate(menuRes, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!onNavBack()) {
            if (!hasFragmentBackStack()) {
                prepareClosePage();
            }
            super.onBackPressed();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extended method
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Close activity page.
     */
    public void close() {
        prepareClosePage();
        finish();
    }

    public void close(int result) {
        setResult(result);
        close();
    }

    public void close(int result, @Nullable Intent data) {
        setResult(result, data);
        close();
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

    /**
     * Perform create instance.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onCreate(Bundle)
     */
    protected void createInstance(Bundle savedInstanceState) {
        if (prepareDataAndState(savedInstanceState)) {
            setPageEnterTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);
            initContentView();
            initInject();
            attachPresenter();
            initView(savedInstanceState);
            overridePendingTransition(mStartEnterAnim, mStartExitAnim);
        } else {
            finish();
        }
    }

    protected final void initInject() {
        ActivityComponent activityComponent = App.getDefault()
                .getApplicationComponent()
                .provideActivityComponent(new ActivityModule(this));
        mComponent = performInject(activityComponent);
    }

    protected boolean hasFragmentBackStack() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0;
    }

    /**
     * Provide current activity layout res.
     */
    @LayoutRes
    protected abstract int getLayoutRes();

    /**
     * Init view components.
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * Perform inject behavior.
     *
     * @param component {@link ActivityComponent} activity component
     */
    @Nullable
    protected abstract C performInject(ActivityComponent component);

    /**
     * Init presenter and bind to current instance.
     *
     * @return {@link P} current page presenter
     */
    @Nullable
    protected abstract P bindPresenter();

    @MenuRes
    protected int getMenuRes() {
        return 0;
    }

    /**
     * Provide activity content view.
     * <br/>
     * Called while {@link #getLayoutRes()} return 0 without layout configuration only.
     *
     * @return View activity content view, null default.
     */
    protected View provideContentView() {
        return null;
    }

    /**
     * Determine whether this activity bind EventBus for receive event.
     *
     * @return false default
     */
    protected boolean bindEventBusOn() {
        return false;
    }

    /**
     * Called when navigation back from appbar navigation icon or back pressed.
     *
     * @return if the back event consume
     */
    protected boolean onNavBack() {
        return false;
    }

    /**
     * Called while navigate up from appbar and has parent specified only.
     * <p/>
     * <h1>If return true, it will ignore invoke {@link #onNavBack()}</h1>
     *
     * @return true if navigate directly to the parent page, else false default
     */
    protected boolean onNavUp() {
        return false;
    }

    /**
     * Called when attach presenter while create instance, before {@link #initView(Bundle)} invoked.
     */
    protected void onAttachPresenter() {
    }

    /**
     * <b>Prepare instance data & state.</b>
     * <p/>
     * Called before initialization of view.
     *
     * @return activity data and state prepared state, default true. If false, activity create will not continue
     */
    protected boolean prepareDataAndState(Bundle savedInstanceState) {
        return true;
    }

    /**
     * Called when {@link #onDestroy()} invoked, release holding resources.
     */
    protected void recycle() {
        mComponent = null;
    }

    protected void setPageEnterTransition(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        mStartEnterAnim = enterAnim;
        mStartExitAnim = exitAnim;
    }

    protected void setPageExitTransition(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        mStopEnterAnim = enterAnim;
        mStopExitAnim = exitAnim;
    }

    protected void prepareClosePage() {
        mReadyToClose = true;
        setPageExitTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);
    }

    protected View.OnClickListener getOnNavigationClickListener() {
        return v -> onNavigationClick();
    }

    protected void onNavigationClick() {
        if (onNavUp()) {
            prepareClosePage();
            setResult(RESULT_CANCELED);
            Intent upIntent = getSupportParentActivityIntent();
            if (upIntent != null) {
                NavUtils.navigateUpTo(this, upIntent);
            } else {
                finish();
            }
        } else if (!onNavBack()) {
            close();
        }
    }

    /**
     * Obtain string parameter from intent extras and intent data uri.
     *
     * @param paramName String param name
     */
    protected String obtainStringParameter(String paramName) {
        Intent intent = getIntent();
        String value = intent.getStringExtra(paramName);
        if (TextUtils.isEmpty(value)) {
            if (intent.getData() == null) return "";

            String currentValue = intent.getData().getQueryParameter(paramName);
            if (!TextUtils.isEmpty(currentValue)) {
                return currentValue;
            }
        }
        return TextUtils.isEmpty(value) ? "" : value;
    }

    protected boolean obtainBoolParameter(String paramName) {
        Intent intent = getIntent();
        boolean value = intent.getBooleanExtra(paramName, false);
        if (!value && intent.getData() != null) {
            String v = intent.getData().getQueryParameter(paramName);
            value = (!TextUtils.isEmpty(v) && "true".equalsIgnoreCase(v));
        }
        return value;
    }

    protected int obtainIntParameter(String paramName) {
        return obtainIntParameter(paramName, -1);
    }

    protected int obtainIntParameter(String paramName, int defValue) {
        Intent intent = getIntent();
        int value = intent.getIntExtra(paramName, defValue);
        if (value == defValue && intent.getData() != null) {
            String currentValue = intent.getData().getQueryParameter(paramName);
            if (!TextUtils.isEmpty(currentValue) && currentValue.matches("\\d?+")) {
                value = Integer.parseInt(currentValue);
            }
        }
        return value;
    }

    protected <T extends Parcelable> T obtainParcelable(String paramName) {
        Intent intent = getIntent();
        return intent == null ? null : intent.getParcelableExtra(paramName);
    }

    private void initContentView() {
        // inflate layout and content view
        final int layoutRes = getLayoutRes();
        if (layoutRes != 0) {
            setContentView(layoutRes);
            ButterKnife.bind(this);
        } else {
            View contentView = provideContentView();
            if (contentView != null) {
                setContentView(contentView);
            }
        }
    }

    private void attachPresenter() {
        mPagePresenter = bindPresenter();
        onAttachPresenter();
    }
}