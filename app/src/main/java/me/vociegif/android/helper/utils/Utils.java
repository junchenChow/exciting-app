package me.vociegif.android.helper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author zihao
 */
public class Utils {

    public static int screenWidth;
    public static int screenHeight;

    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 600) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public synchronized static boolean isFastClick(int timer) {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < timer) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static String TimeStamp2Date(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = null;
        long time = System.currentTimeMillis() - timestamp;
        long tenfenzhong = 10 * 60 * 1000;
        long sixtfenzhong = 60 * 60 * 1000;
        long dayfenzhong = 24 * 60 * 60 * 1000;
        long yearfenzhong = 365 * 24 * 60 * 60 * 1000;
        if (time < tenfenzhong) {
            return date = "刚刚";
        } else if (time < sixtfenzhong) {
            return date = time / 1000 / 60 + "分钟前";
        } else if (time < dayfenzhong) {
            return date = time / 1000 / 60 / 60 + "小时前";
        } else if (time < yearfenzhong) {
            String formats = "MM月dd日";
            date = new java.text.SimpleDateFormat(formats, Locale.CHINA).format(new java.util.Date(timestamp));
            return date;
        } else {
            String formats = "yyyy年MM月dd日";
            date = new java.text.SimpleDateFormat(formats, Locale.CHINA).format(new java.util.Date(timestamp));
            return date;
        }
    }

    /**
     * 根据手机分辨率从dp转成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * @Title: setListViewHeightBasedOnChildren @Description:
     * TODO(根据ListView算出List的高度) @param @param mContext @param @param
     * listView 参数 @return void 返回类型 @throws
     */
    public static void setListViewHeightBasedOnChildren(Context mContext, ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }



    public static int getNewX(Context context, int oldX) {
        Point outSize = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(outSize);
        return (int) (outSize.x * oldX / 360.0f);

    }

    // 检测网络连接
    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }


    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
        String expression2 = "^\\(?(\\d{2})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";

        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);

        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(inputStr);
        if (matcher.matches() || matcher2.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 添加QQ群
     *
     * @param context
     * @return
     */
    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));

        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        try {
            context.startActivity(intent);
            context = null;
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 首页Tab索引常量类
     */
    public static class TabIndex {
        /**
         * 热点
         **/
        public static final int TAB_HOT = 0;
        /**
         * 关注
         */
        public static final int TAB_ATTENTION = 1;
        /**
         * 发现
         **/
        public static final int TAB_DISCOVER = 2;
        /**
         * 精选
         **/
        public static final int TAB_CULL = 3;
        /**
         * 消息
         **/
        public static final int TAB_NEWS = 4;
        /**
         * 我的
         **/
        public static final int TAB_MINE = 5;
        /**
         * 主页
         **/
        public static final int TAB_HOME = 6;

        public static final int TAB_NEW_ATTENTION = 7;

    }

    /**
     * 登录注册入口Tab索引常量类
     */
    public static class TabEntranceIndex {
        /**
         * 注册
         */
        public static final int TAB_REGISTER = 1;
        /**
         * 登录
         **/
        public static final int TAB_LOGIN = 0;
    }

    /**
     * 背景商店界面入口Tab索引常量类
     */
    public static class TabBackShopIndex {
        /**
         * 简约
         */
        public static final int TAB_BRIFE = 0;
        /**
         * 条漫
         **/
        public static final int TAB_SMALL_CARTOON = 1;
        /**
         * 场景
         */
        public static final int TAB_SCENE = 2;
        /**
         * 动漫
         **/
        public static final int TAB_CARTOON = 3;


    }

    /**
     * 我的主页Tab索引常量类
     */
    public static class TabMyHomePageIndex {
        /**
         * 作品
         */
        public static final int TAB_WORKS = 0;
        /**
         * 消息
         **/
        public static final int TAB_NEWS = 1;
        /**
         * 收藏
         **/
        public static final int TAB_COLLECT = 2;
        /**
         * 草稿箱
         **/
        public static final int TAB_DRAFT = 3;
    }

    /**
     * 获取屏幕的一些信息
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}