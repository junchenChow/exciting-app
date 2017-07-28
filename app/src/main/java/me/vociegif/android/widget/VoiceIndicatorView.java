package me.vociegif.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buhuavoice.app.R;
import me.vociegif.android.event.EventChangeItem;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class VoiceIndicatorView extends LinearLayout {

    @Bind(R.id.tvInput)
    TextView mTvInput;
    @Bind(R.id.tvVoice)
    TextView mTvVoice;
    @Bind(R.id.tvShake)
    TextView mTvShake;
    @BindColor(R.color.yellow_fb9b01)
    int mSelectorTextColor;
    @BindColor(R.color.grey_97)
    int mNormalTextColor;
    @BindDrawable(R.drawable.corners_voice)
    Drawable mDrawableSelector;
    @BindDrawable(R.drawable.corners_voice_white)
    Drawable mDrawableNormal;

    public VoiceIndicatorView(Context context) {
        super(context);
    }

    public VoiceIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_indicator_voice_main, this, false);
        addView(view);
        ButterKnife.bind(this, view);
        mDrawableNormal.setBounds(0, 0, mDrawableNormal.getMinimumWidth(), mDrawableNormal.getMinimumHeight());
        mDrawableSelector.setBounds(0, 0, mDrawableSelector.getMinimumWidth(), mDrawableSelector.getMinimumHeight());
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @OnClick({R.id.tvVoice, R.id.tvInput, R.id.tvShake})
    void switchClick(View view){
        switch (view.getId()){
            case R.id.tvInput:
                switchIndex(0);
                EventBus.getDefault().post(new EventChangeItem(0));
                break;
            case R.id.tvVoice:
                switchIndex(1);
                EventBus.getDefault().post(new EventChangeItem(1));
                break;
            case R.id.tvShake:
                switchIndex(2);
                EventBus.getDefault().post(new EventChangeItem(2));
                break;
        }
    }

    public void switchIndex(int index) {
        switch (index) {
            case 0:
                mTvShake.setCompoundDrawables(null, mDrawableNormal, null, null);
                mTvShake.setTextColor(mNormalTextColor);
                mTvVoice.setCompoundDrawables(null, mDrawableNormal, null, null);
                mTvVoice.setTextColor(mNormalTextColor);

                mTvInput.setCompoundDrawables(null, mDrawableSelector, null, null);
                mTvInput.setTextColor(mSelectorTextColor);
                break;
            case 1:
                mTvInput.setCompoundDrawables(null, mDrawableNormal, null, null);
                mTvInput.setTextColor(mNormalTextColor);
                mTvShake.setCompoundDrawables(null, mDrawableNormal, null, null);
                mTvShake.setTextColor(mNormalTextColor);

                mTvVoice.setCompoundDrawables(null, mDrawableSelector, null, null);
                mTvVoice.setTextColor(mSelectorTextColor);
                break;
            case 2:
                mTvInput.setCompoundDrawables(null, mDrawableNormal, null, null);
                mTvInput.setTextColor(mNormalTextColor);
                mTvVoice.setCompoundDrawables(null, mDrawableNormal, null, null);
                mTvVoice.setTextColor(mNormalTextColor);

                mTvShake.setCompoundDrawables(null, mDrawableSelector, null, null);
                mTvShake.setTextColor(mSelectorTextColor);
                break;
        }
    }
}
