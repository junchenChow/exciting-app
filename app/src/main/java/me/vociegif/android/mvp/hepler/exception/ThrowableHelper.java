package me.vociegif.android.mvp.hepler.exception;

import android.content.Context;
import android.support.annotation.NonNull;

import me.vociegif.android.helper.utils.ToastUtil;
import me.vociegif.android.mvp.hepler.ProgressBarHelper;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class ThrowableHelper {

    @NonNull
    public static void processThrowable(Context context, Throwable throwable, boolean showToast) {

        if (context == null) {
            return;
        }
        ProgressBarHelper.getInstance(true).dismiss();

        if (throwable instanceof AccountForcedException) {
            return;
        }

        if (!showToast) {
            return;
        }
        if (throwable instanceof HttpException) {
            ToastUtil.show(context, throwable.getMessage());
            return;
        }

        if (throwable instanceof ConnectTimeoutException) {
            ToastUtil.show(context, "网络不给力哦！");
            return;
        }

        if (throwable instanceof SocketTimeoutException) {
            ToastUtil.show(context, "网络不给力哦！");
            return;
        }

        if (throwable instanceof UnknownHostException) {
            ToastUtil.show(context, "网络不给力哦！");
            return;
        }


        throwable.printStackTrace();
    }
}
