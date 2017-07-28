package me.vociegif.android.widget;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buhuavoice.app.R;

import java.util.Locale;


/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class MergeLoadingDialog {
    private View mView;
    private TextView mTvProgress;
    private AlertDialog mDialog;
    private ProgressBar mProgressBar;

    public MergeLoadingDialog(Activity context) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_merge, null);
        mTvProgress = (TextView) mView.findViewById(R.id.tv_progress);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.my_progress);
    }

    public void show(Activity context) {
        mDialog = new AlertDialog.Builder(context).create();
        mTvProgress.setText("准备合成");
        mDialog.setCancelable(false);
        mDialog.show();
        mDialog.getWindow().setContentView(mView);
    }

    public void setProgress(float progress) {
        if (mDialog != null && mDialog.isShowing() && mTvProgress != null) {
            mProgressBar.setProgress((int) progress);
            mTvProgress.setText(String.format(Locale.CHINA, "合成进度 %.02f%%", progress));
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            ((ViewGroup) mDialog.getWindow().getDecorView()).removeAllViews();
            mDialog.dismiss();
        }
        mDialog = null;
    }
}
