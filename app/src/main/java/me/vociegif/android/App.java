package me.vociegif.android;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.glidebitmappool.GlideBitmapPool;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.greenrobot.eventbus.util.AsyncExecutor;
import org.wlf.filedownloader.FileDownloadConfiguration;
import org.wlf.filedownloader.FileDownloader;

import java.io.File;

import me.vociegif.android.helper.download.StickerManager2;
import me.vociegif.android.mvp.di.component.ApplicationComponent;
import me.vociegif.android.mvp.di.component.DaggerApplicationComponent;
import me.vociegif.android.mvp.di.modules.ApiModule;
import me.vociegif.android.mvp.di.modules.ApplicationModule;

public class App extends ConfigApplication {

    public static App defaultInstance;
    public ApplicationComponent mApplicationComponent;
    private AsyncExecutor threadPool;

    public App() {
        threadPool = AsyncExecutor.create();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        defaultInstance = this;
        initComponent();
        initImageLoader();
        initFileDownloader();
        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size
        StickerManager2.getInstance().onCreate();
    }

    private DisplayMetrics displayMetrics = null;

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public static App getDefault() {
        if (defaultInstance == null) {
            synchronized (App.class) {
                if (defaultInstance == null) {
                    defaultInstance = new App();
                    defaultInstance.onCreate();
                }
            }
        }
        return defaultInstance;
    }

    private void initComponent() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .apiModule(new ApiModule())
                .build();
        mApplicationComponent.inject(this);
    }

    // init FileDownloader
    private void initFileDownloader() {

        // 1.create FileDownloadConfiguration.Builder
        FileDownloadConfiguration.Builder builder = new FileDownloadConfiguration.Builder(this);

        // 2.config FileDownloadConfiguration.Builder
        builder.configFileDownloadDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "MoHoVoice"); // config the download path
        // builder.configFileDownloadDir("/storage/sdcard1/FileDownloader");

        // allow 3 download tasks at the same time
        builder.configDownloadTaskSize(3);

        // config retry download times when failed
        builder.configRetryDownloadTimes(5);

        // enable debug mode
        //builder.configDebugMode(true);

        // config connect timeout
        builder.configConnectTimeout(15000); // 25s

        // 3.init FileDownloader with the configuration
        FileDownloadConfiguration configuration = builder.build(); // build FileDownloadConfiguration with the builder
        FileDownloader.init(configuration);
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.NONE)
                .cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .defaultDisplayImageOptions(defaultOptions)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new WeakMemoryCache())
                .threadPoolSize(5)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    public String getFilesDirPath() {
        return getFilesDir().getAbsolutePath();
    }

    public String getCacheDirPath() {
        return getCacheDir().getAbsolutePath();
    }

    public void runThreadInPool(final AsyncExecutor.RunnableEx runnable) {
        threadPool.execute(runnable);
    }

    public void LoadStickerBg(String url, ImageView img, int type) {
        StickerManager2.getInstance().LoadStickerImage(url, img, type, true);
    }
}
