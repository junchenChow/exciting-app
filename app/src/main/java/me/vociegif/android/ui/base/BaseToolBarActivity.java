package me.vociegif.android.ui.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buhuavoice.app.R;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.vociegif.android.event.EventServerError;
import me.vociegif.android.helper.utils.ToastUtil;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.di.base.BaseComponent;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public abstract class BaseToolBarActivity<C extends BaseComponent, P extends BasePresenter> extends BaseActivity {

    @TargetApi(19)
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setUpStatusBar(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
            textView.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_ffde600));
            textView.setLayoutParams(lParams);
            ViewGroup view = (ViewGroup) getWindow().getDecorView();
            view.addView(textView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("MainScreen");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("MainScreen");
    }

    public void getIntents(){}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerErrorHint(EventServerError serverError){
        ToastUtil.show(serverError.getErrorMsg());
    }
}
