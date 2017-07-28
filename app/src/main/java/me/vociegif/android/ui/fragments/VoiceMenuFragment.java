package me.vociegif.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.andexert.library.RippleView;

import com.buhuavoice.app.BuildConfig;
import com.buhuavoice.app.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import me.vociegif.android.event.EventGetVoiceResponse;
import me.vociegif.android.helper.JsonParser;
import me.vociegif.android.helper.utils.ToastUtil;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.di.base.BaseComponent;
import me.vociegif.android.mvp.di.component.FragmentComponent;
import me.vociegif.android.mvp.hepler.ProgressBarHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import me.vociegif.android.ui.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class VoiceMenuFragment extends BaseFragment implements View.OnTouchListener, InitListener {

    @Bind(R.id.iv_pressed_voice)
    RippleView ivPresserVoice;
    @Bind(R.id.tv_pressed_text)
    TextView tvPresserText;

    private SpeechRecognizer mIat;
    private Map<String, String> mIatResults = new HashMap<>();

    static VoiceMenuFragment newInstance() {
        return new VoiceMenuFragment();
    }

    @Override
    protected boolean bindEventBusOn() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_voice_menu;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        ivPresserVoice.setOnTouchListener(this);
        initXFVoice();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIat.cancel();
        mIat.destroy();
    }

    @Nullable
    @Override
    protected BaseComponent performInject(FragmentComponent component) {
        return null;
    }

    @Nullable
    @Override
    protected BasePresenter bindPresenter() {
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.iv_pressed_voice) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tvPresserText.setText("松开发送");
                    mIat.startListening(mRecoListener);
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    tvPresserText.setText("按住说话");
                    mIat.stopListening();
                    ProgressBarHelper.getInstance(true).show(getActivity());
                    break;

            }
        }
        return false;
    }

    @Override
    public void onInit(int code) {
        if (code != ErrorCode.SUCCESS) {
            ToastUtil.show(getActivity(), "初始化失败，错误码：" + code);
        }
    }


    private RecognizerListener mRecoListener = new RecognizerListener() {
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results, isLast);
        }

        @Override
        public void onError(SpeechError error) {
            ProgressBarHelper.getInstance(false).dismiss();
            error.getPlainDescription(true);
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            if (BuildConfig.DEBUG)
                Log.i("voiceS", "音量:" + i);
        }

        @Override
        public void onBeginOfSpeech() {
            if (BuildConfig.DEBUG)
                Log.i("voiceS", "开始录音!");
        }

        @Override
        public void onEndOfSpeech() {
            if (BuildConfig.DEBUG)
                Log.i("voiceS", "结束录音!");
        }
    };

    private void initXFVoice() {
        SpeechUtility.createUtility(getActivity(), SpeechConstant.APPID + BuildConfig.XF_APP_ID);
        mIat = SpeechRecognizer.createRecognizer(getActivity(), this);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
    }

    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);
        StringBuilder resultBuffer = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        ProgressBarHelper.getInstance(false).dismiss();
        if (!TextUtils.isEmpty(resultBuffer.toString()) && isLast) {
            if (resultBuffer.toString().length() > 15) {
                ToastUtil.show("字数不能超出15个字符");
            } else {
                EventBus.getDefault().post(new EventGetVoiceResponse(resultBuffer.toString()));
            }
        }
    }
}
