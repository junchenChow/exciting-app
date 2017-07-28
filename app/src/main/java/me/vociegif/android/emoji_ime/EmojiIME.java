package me.vociegif.android.emoji_ime;

import android.inputmethodservice.InputMethodService;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.buhuavoice.app.R;

public class EmojiIME extends InputMethodService {

    private InputConnection mConnection = null;
    private View mRootView;
    private View mCandView;

    /**
     * ACTION_INPUT_METHOD_SETTINGS ：    // 跳转系统语言和输入设备
     * <p>
     * Intent intent =  new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
     * startActivity(intent);
     */

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        mRootView = inflater.inflate(R.layout.ime_input_content, null);
        return mRootView;
    }

    @Override
    public View onCreateCandidatesView() {
        LayoutInflater inflater = getLayoutInflater();
        mCandView = inflater.inflate(R.layout.ime_input_top_item, null);
        mCandView.findViewById(R.id.btn_1).setOnClickListener(v -> {});

        return mCandView;
    }

    @Override
    public void onStartInputView(EditorInfo editorInfo, boolean restarting) {
        mConnection = getCurrentInputConnection();
        setCandidatesViewShown(true); // 候选框.
        getKeyboardMode(editorInfo);
        super.onStartInputView(editorInfo, restarting);
    }


    /**
     * 获取输入法键盘类型.
     */
    private int getKeyboardMode(final EditorInfo editorInfo) {
        if (editorInfo == null) {
            return KeyBoardID.MODE_TEXT;
        }
        //
        final int inputType = editorInfo.inputType;
        final int variation = inputType & InputType.TYPE_MASK_VARIATION;
        // 根据 eidttext inputtype 返回软键盘类型.
        switch (inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER: // 数字键盘.
                // return KeyBoardID.MODE_NUMBER;
            case InputType.TYPE_CLASS_PHONE: // 显示符号键盘. phone拨号.
                // return KeyBoardID.MODE_PHONE;
            case InputType.TYPE_CLASS_DATETIME: // 日期，时间. 文本输入框要求输入数字或日期时.
                switch (variation) {
                    case InputType.TYPE_DATETIME_VARIATION_DATE: // 多一个 "/"
                        return KeyBoardID.MODE_DATE;
                    case InputType.TYPE_DATETIME_VARIATION_TIME: // 多一个 ":"
                        return KeyBoardID.MODE_TIME;
                    default: // InputType.TYPE_DATETIME_VARIATION_NORMAL
                        return KeyBoardID.MODE_DATETIME; // 多两个 "/"，":"
                }
            case InputType.TYPE_CLASS_TEXT: // 显示输入字母的软键盘.
                if (KeyBoardUtils.isEmailVariation(variation)) {
                    return KeyBoardID.MODE_EMAIL;
                } else if (variation == InputType.TYPE_TEXT_VARIATION_URI) {
                    return KeyBoardID.MODE_URL;
                } else if (variation == InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE) {
                    return KeyBoardID.MODE_IM;
                } else if (variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    return KeyBoardID.MODE_TEXT;
                } else {
                    return KeyBoardID.MODE_TEXT;
                }
            default:
                return KeyBoardID.MODE_TEXT;
        }
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isInputViewShown()) {
            return super.onKeyDown(keyCode, event);
        }
        return processKey(event, false) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!isInputViewShown()) {
            return super.onKeyDown(keyCode, event);
        }
        return processKey(event, true) || super.onKeyUp(keyCode, event);
    }

    private boolean processKey(KeyEvent event, boolean realAction) {
        int keyCode = event.getKeyCode();
        // 按返回键.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return false;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_UP:
                //
                if (realAction)
                    mRootView.onKeyDown(keyCode, event);
                else
                    mRootView.onKeyUp(keyCode, event);
                return true;
            default:
                break;
        }

        return true;
    }

    public void onTextInput(CharSequence text) {
        mConnection.beginBatchEdit();
        mConnection.commitText(text, 1); // 输入文本.
        mConnection.endBatchEdit();
    }

    public void onCodeInput(int primaryCode) {
        // IME_ACTION_GO: go操作，将用户带入到一个该输入框的目标的动作。确认键将不会有icon，只有label: GO
        switch (primaryCode) {
            case KeyBoardID.CODE_DELETE:
                break;
            // IME_ACTION_NEXT: next操作，将用户带入到该文本框的写一个输入框中。
            case KeyBoardID.CODE_ACTION_NEXT:
                // EditorInfo.IME_ACTION_GO
                mConnection.performEditorAction(EditorInfo.IME_ACTION_NEXT);
                break;
            case KeyBoardID.CODE_ACTION_ENTER:
                break;
        }

    }

}
