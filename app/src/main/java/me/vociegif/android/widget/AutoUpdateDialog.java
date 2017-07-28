package me.vociegif.android.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.buhuavoice.app.R;
import me.vociegif.android.helper.download.VersionUpdateService;
import me.vociegif.android.helper.utils.CommonUtil;
import me.vociegif.android.mvp.vo.VersionUpdateEntity;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class AutoUpdateDialog extends Dialog implements View.OnClickListener {

    private Activity mActivity;
    private TextView mTvVersionCode;
    private TextView mTvVersionSize;
    private TextView mTvVersionMsg;
    private VersionUpdateEntity mVersionResult;

    public AutoUpdateDialog(Activity context) {
        super(context, R.style.dialog);
        mActivity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_update);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initView();
    }

    private void initView() {
        mTvVersionCode = (TextView) findViewById(R.id.tv_version_code);
        mTvVersionSize = (TextView) findViewById(R.id.tv_version_size);
        mTvVersionMsg = (TextView) findViewById(R.id.tv_version_msg);
        findViewById(R.id.tv_start).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    public void setNewVersionData(VersionUpdateEntity versionResult){
        this.mVersionResult = versionResult;
        mTvVersionCode.setText("最新版本: " + versionResult.getVersion());
        mTvVersionMsg.setText(versionResult.getProfile());
        mTvVersionSize.setText("新版本大小: " + CommonUtil.bytes2kb(versionResult.getSize()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_start:
                Intent intent = new Intent(mActivity, VersionUpdateService.class);
                intent.putExtra(VersionUpdateService.UPDATE_URL, mVersionResult.getUrl());
                mActivity.startService(intent);
                break;
            case R.id.tv_cancel:
                break;
        }
        dismiss();
    }

}
