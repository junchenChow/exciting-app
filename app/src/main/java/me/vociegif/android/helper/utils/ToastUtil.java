/**
 *
 */
package me.vociegif.android.helper.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import me.vociegif.android.App;


public class ToastUtil {

    private static Toast toast;

    private ToastUtil() {

    }

    public static void show(Context context, int stringRes) {
        show(context, context.getString(stringRes));
    }

    public static void show(String stringRes) {
        show(App.getDefault(), stringRes);
    }

    public static void show(Context context, String stringRes) {
        if (toast == null) {
            toast = Toast.makeText(context, stringRes, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(stringRes);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showLong(Context context, int stringRes) {
        showLong(context, context.getString(stringRes));
    }

    public static void showLong(Context context, String stringRes) {
        if (toast == null) {
            toast = Toast.makeText(context, stringRes, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(stringRes);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }


    public static void showNotNetWork(Context context) {
        if (toast == null) {
            toast = Toast.makeText(context,"没有网络", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText("没有网络");
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    static Toast toasts;
    static ImageView imageCodeProject;

}