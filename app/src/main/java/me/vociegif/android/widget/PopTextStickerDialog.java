package me.vociegif.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;

import com.buhuavoice.app.R;
import me.vociegif.android.event.EventTextInput;
import me.vociegif.android.helper.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class PopTextStickerDialog extends Dialog implements OnClickListener {

    private String text;
    private EditText etComment;

    public PopTextStickerDialog(Context mContext) {
        super(mContext, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_nd);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        initView();
    }

    private void initView() {
        etComment = (EditText) findViewById(R.id.et_comment);
        etComment.setText(text);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.bg_view).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (etComment.getText().toString().length() > 15) {
                    ToastUtil.show("字数不能超出15个字符");
                } else
                    done();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;

        }
    }

    private void done() {
        EventBus.getDefault().post(new EventTextInput(etComment.getText().toString()));
        etComment.setText("");
        dismiss();
    }

    public void showThis(String text) {
        if (!text.equals("双击输入文字") && !TextUtils.isEmpty(text))
            this.text = text;
        show();
    }

}
