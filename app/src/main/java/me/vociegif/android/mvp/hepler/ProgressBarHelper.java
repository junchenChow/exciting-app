package me.vociegif.android.mvp.hepler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class ProgressBarHelper {

    private static ProgressBarHelper sInstance = new ProgressBarHelper();
    private static boolean canTouch = true;
    ProgressDialog progressDialog;


    public static ProgressBarHelper getInstance(@Nullable() boolean canTouch) {
        ProgressBarHelper.canTouch = canTouch;
        return sInstance;
    }

    public synchronized void show(Context context) {
        if (context instanceof Activity) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("加载中");
            progressDialog.setCancelable(canTouch);
            progressDialog.show();
            if (!canTouch)
                progressDialog.setOnKeyListener((v1, keyCode, event) -> KeyEvent.KEYCODE_BACK == keyCode);
            else
                progressDialog.setOnKeyListener((v1, keyCode, event) -> {
                    if (KeyEvent.KEYCODE_BACK == keyCode) {
                        dismiss();
                        return true;
                    }
                    return false;
                });
        }
    }

    public synchronized void show(Context context, String stringId) {
        if (context instanceof Activity) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(stringId);
            progressDialog.setCancelable(canTouch);
            progressDialog.show();
            progressDialog.setOnKeyListener((v1, keyCode, event) -> {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
//                    dismiss();
                    return true;
                }
                return false;
            });
        }
    }

    public synchronized void dismiss() {
        if (progressDialog != null) {

            progressDialog.dismiss();
            progressDialog.setOnKeyListener(null);
            progressDialog = null;
        }

    }

}
