package me.vociegif.android.helper.download;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class OkHttpDownload {

    public final OkHttpClient client;
    public static OkHttpDownload defaultInstance;
    public DownloadProgressListener downloadProgressListener;

    public interface DownloadProgressListener {
        void onDownloadProgress(long progress, boolean isDone);
        void onDownloadFinish(InputStream inputStream);
    }

    public static OkHttpDownload getDefault(DownloadProgressListener downloadProgressListener) {
        if (defaultInstance == null) {
            synchronized (OkHttpDownload.class) {
                if (defaultInstance == null) {
                    defaultInstance = new OkHttpDownload(downloadProgressListener);
                }
            }
        }
        return defaultInstance;
    }

    public OkHttpDownload(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain -> {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), (bytesRead, contentLength, done) -> {
                                if (downloadProgressListener != null) {
                                    downloadProgressListener.onDownloadProgress(((100 * bytesRead) / contentLength), done);
                                }
                            }))
                            .build();
                })
                .build();
    }


    public void setRequestUrl(String url) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                downloadProgressListener.onDownloadFinish(response.body().byteStream());
            }
        });
    }

}
