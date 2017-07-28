package me.vociegif.android.helper.download;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import me.vociegif.android.App;
import com.buhuavoice.app.R;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.utils.FileUtil;

import java.io.File;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class VersionUpdateService extends Service {

    private BroadcastReceiver receiver;
    private String apkName;

    public static final String UPDATE_URL = "update_url";
    private final String downloadPath = Environment.getExternalStorageDirectory() + "/newVoice/";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra(UPDATE_URL);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File(downloadPath + apkName)), "application/vnd.android.package-archive");
                startActivity(intent);
                stopSelf();
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        startDownload(url);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void startDownload(String downloadUrl) {
        String apkUrl = Constants.setAliyunImageUrl(downloadUrl);
        String[] arr = apkUrl.split("/");
        apkName = arr[arr.length - 1];
        DownloadManager downloadManager = (DownloadManager) App.getDefault().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        try {
            FileUtil.createFolder(downloadPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setDestinationInExternalPublicDir("newVoice", apkName);
        String title = this.getResources().getString(R.string.app_name);
        request.setTitle(title);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }
}
