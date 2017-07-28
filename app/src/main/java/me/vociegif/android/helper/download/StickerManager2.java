package me.vociegif.android.helper.download;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import me.vociegif.android.App;
import com.buhuavoice.app.R;
import me.vociegif.android.event.EventStickerAnalysis;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.ConstantsPath;
import me.vociegif.android.helper.utils.CryptUtil;
import me.vociegif.android.helper.utils.DecodeImageUtils;
import me.vociegif.android.helper.utils.ToastUtil;
import me.vociegif.android.helper.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

import org.greenrobot.eventbus.EventBus;

public class StickerManager2 {

    public static final int STICKER_LOAD_START = 0;
    public static final int STICKER_LOAD_PROGRESS = 1;
    public static final int STICKER_LOAD_FINISH = 2;
    public static final int STICKER_LOAD_NETERROR = 3;

    public static final int STIKER = 0;
    public static final int BACKGROUND = 1;

    private static StickerManager2 instance = null;
    // 下载流水线
    private boolean downloading = false;
    private Queue<StickerLoadData> downloadQueues = null;
    private StickerLoadData downloadData = null;
    // 解析流水线
    private boolean parsing = false;
    private Queue<StickerLoadData> parseQueues = null;
    private StickerLoadData parseData = null;
    // 贴纸图片的设置
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:// 解密数据结束
                {
                    setBitmapInMemory(parseData.url, parseData.bm);
                    parseData.setImage();
                    parseData.clear();
                    parseData = null;

                    if (parseQueues.size() > 0) {
                        parseSticker();
                    } else {
                        parsing = false;
                    }
                }
                break;
                case 1:// 下载进度
                {
                    if (downloadData != null)
                        downloadData.onLoadProgress(msg.arg1, msg.arg2);
                }
                break;
                case 2:// 加载进度
                {
                    if (parseData != null)
                        parseData.onLoadProgress(msg.arg1, msg.arg2);
                }
                break;
            }
        }
    };

    public StickerManager2() {
        downloading = false;
        parsing = false;
    }

    public static StickerManager2 getInstance() {
        if (instance == null) {
            synchronized (StickerManager2.class) {
                if (instance == null) {
                    instance = new StickerManager2();
                }
            }
        }
        return instance;
    }

    public void onCreate() {
        downloadQueues = new LinkedList<StickerLoadData>();
        parseQueues = new LinkedList<StickerLoadData>();
    }

    public void onDestroy() {

        ClearQueue();

        downloadQueues = null;
        parseQueues = null;
    }

    public void ClearQueue()
    {
        while (downloadQueues.peek() != null) {
            StickerLoadData data = downloadQueues.poll();
            data.clear();
        }
        downloadQueues.clear();

        while (parseQueues.peek() != null) {
            StickerLoadData data = parseQueues.poll();
            data.clear();
        }
        parseQueues.clear();
    }

    public Bitmap getBitmapInMemory(String key) {
        return ImageLoader.getInstance().getMemoryCache().get(key);
    }

    public void setBitmapInMemory(String key, Bitmap value) {
        if(TextUtils.isEmpty(key) || value == null)
            return;
        Log.i("bitmapKey","put: "+ key);
        ImageLoader.getInstance().getMemoryCache().put(key, value);
    }

    // 读取贴纸图片的入口，把请求分成本地和下载，并加入相应队列
    public void LoadStickerImage(String url, ImageView image) {
        LoadStickerImage(url, image, 0, true);
    }

    // 读取贴纸图片的入口，把请求分成本地和下载，并加入相应队列
    public void LoadStickerImage(String url, ImageView image, int param, boolean bimmediately) {
        StickerLoadData data = new StickerLoadData();
        data.url = url;
        data.wr_image = new WeakReference<ImageView>(image);
        data.type = StickerLoadData.STICKER_GET;
        data.param = param;
        if (null != image) {
            image.setTag(url);
            image.setImageResource(R.mipmap.stickers_loading);
        }

        Bitmap bitmap = getBitmapInMemory(url);
        if (bitmap != null) {//在图片内存中，直接设置。
            data.bm = bitmap;
            data.setImage();
            data.clear();
            return;
        }

        File file = data.checkLocal();
        if (file != null) {//图片在本地磁盘中，直接读取本地文件。
            parseQueues.offer(data);
            if (!parsing && bimmediately) {
                parseSticker();
            }
            return;
        }

        // 下载队列
        downloadQueues.offer(data);
        if (!downloading && bimmediately) {
            downloadSticker();
        }
    }

    public void StartParse()
    {
        if (!parsing && parseQueues.size() > 0) {
            parseSticker();
        }

        if (!downloading && downloadQueues.size() > 0) {
            downloadSticker();
        }
    }

    public boolean checkLocalStickerImage(String url) {
        StickerLoadData data = new StickerLoadData();
        data.url = url;
        data.type = StickerLoadData.STICKER_CHECK;

        File file = data.checkLocal();
        return (file != null);
    }

    // 读取贴纸图片的入口，把请求分成本地和下载，并加入相应队列
    public void checkStickerImage(String url) {
        StickerLoadData data = new StickerLoadData();
        data.url = url;
        data.wr_image = null;
        data.type = StickerLoadData.STICKER_CHECK;

        File file = data.checkLocal();
        if (file != null) {// 本地队列
            data.onLoadFinish();
            data.clear();
            return;
        }

        // 下载队列
        downloadQueues.offer(data);
        if (downloading == false) {
            downloadSticker();
        }
    }

    // 下载队列
    protected void downloadSticker() {
        downloading = true;

        boolean haslocal = false;
        do {
            if (downloadQueues.size() <= 0) {
                downloadData = null;
                break;
            }

            // 获取队列首位数据
            downloadData = downloadQueues.poll();

            // 判断是否存在本地数据
            haslocal = (downloadData.checkLocal() != null);
            if (haslocal) {
                parseQueues.offer(downloadData);
                if (parsing == false) {
                    parseSticker();
                }
                downloadData = null;
            }
        } while (haslocal);

        if (downloadData == null) {
            downloading = false;
            return;
        }

        // 判断网络
        if (!Utils.checkConnection(App.getDefault())) {
            ToastUtil.show(App.getDefault(), "没有网络连接");
            downloading = false;
            downloadData.onLoadError();
            return;
        }

        String savepath = downloadData.localPath;
        Log.i("SaveZipUrl", savepath);
        downloadData.onLoadStart();

        HttpUtils http = new HttpUtils();
        http.download(Constants.setAliyunImageUrl(downloadData.url), savepath, true, true, new RequestCallBack<File>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // 下载失败，把请求放入队尾。
                downloadData.trycount++;
                if (downloadData.trycount < 10) {
                    if(!downloadData.isDestroy())
                        downloadQueues.offer(downloadData);
                }else
                {
                    downloadData.clear();
                    downloadData = null;
                }
                downloadSticker();
            }

            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                // 下载完成，把读取请求加入本地队列
                downloadData.stkFile = arg0.result;
                switch (downloadData.type) {
                    case StickerLoadData.STICKER_GET: {
                        parseQueues.offer(downloadData);
                        if (parsing == false) {
                            parseSticker();
                        }
                    }
                    break;
                    case StickerLoadData.STICKER_CHECK: {
                        downloadData.onLoadFinish();
                        downloadData.clear();
                        downloadData = null;
                    }
                    break;
                }

                // 继续下载下一个
                if (downloadQueues.size() > 0) {
                    downloadSticker();
                } else {
                    downloading = false;
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);

                float cur = 0;
                switch (downloadData.type) {
                    case StickerLoadData.STICKER_GET: {
                        cur = 0.9f * current / total * 100.0f;
                    }
                    break;
                    case StickerLoadData.STICKER_CHECK: {
                        cur = 1.0f * current / total * 100.0f;
                    }
                    break;
                }

                Message msg = handler.obtainMessage();
                msg.what = 1;// 下载进度
                msg.arg1 = (int) cur;
                msg.arg2 = 100;
                handler.sendMessage(msg);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
            }
        });
    }

    // 贴纸图片的解密
    private void parseSticker() {
        parsing = true;
        parseData = parseQueues.poll();

        App.getDefault().runThreadInPool(() -> {
            FileInputStream inputStream = null;
            try {

                inputStream = new FileInputStream(parseData.stkFile);
                StringBuilder builder = new StringBuilder();
                byte[] buffer = new byte[1024 * 1024];
                int length = -1;
                int totaltemp = inputStream.available();
                while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    builder.append(new String(buffer, 0, length));
                }
                String stickerContent = builder.toString();
                String md5 = CryptUtil.encryptMD5(stickerContent);
                String strResult = CryptUtil.MD51(md5, stickerContent);
                byte[] aaa = CryptUtil.decompressData(strResult);

                parseData.bm = DecodeImageUtils.Bytes2Bimap(aaa);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                handler.sendEmptyMessage(0);
            }
        });
    }

    // 队列数据
    public class  StickerLoadData {
        public static final int STICKER_CHECK = 0;
        public static final int STICKER_GET = 1;

        public String url = "";
        public WeakReference<ImageView> wr_image = null;
        public Bitmap bm = null;
        public String baseName;
        public String localPath;
        public File stkFile;
        public int type;
        public int param = 0;
        public int trycount = 0;

        public void clear()
        {
            if(wr_image != null)
            {
                wr_image.clear();
                wr_image = null;
            }
            stkFile = null;
            bm = null;
        }

        public boolean isDestroy()
        {
            ImageView iv = wr_image.get();
            if(iv != null)
                return false;
            return true;
        }

        public File checkLocal() {
            if(url == null)
                return null;
            String[] resultArr = url.split("/");
            baseName = resultArr[resultArr.length - 1];
            localPath = ConstantsPath.getStickerFileDir() + String.valueOf(baseName.hashCode());

            try {
                stkFile = new File(localPath);
                if (stkFile.exists()) {
                    return stkFile;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            return null;
        }

        public void setImage() {
            if (bm != null) {
                if(wr_image == null)
                    return;
                ImageView iv = wr_image.get();
                if(iv != null)
                {
                    String tag = (String) iv.getTag();
                    if(TextUtils.isEmpty(tag)) {
                        iv.setImageBitmap(bm);
                    } else if(this.url.equals(tag)) {
                        iv.setImageBitmap(bm);
                    }
                }
            }
            this.onLoadFinish();
        }

        public void onLoadStart() {
        }

        public void onLoadProgress(int cur, int total) {
        }


        public void onLoadFinish() {
                EventStickerAnalysis event = new EventStickerAnalysis();
                event.setSucceed(EventStickerAnalysis.STICKER_LOAD_FINISH);
                event.setUrl(this.url);
                event.setBitmap(this.bm);
                event.setParam(this.param);
                EventBus.getDefault().post(event);
        }

        public void onLoadError() {
            EventStickerAnalysis event = new EventStickerAnalysis();
            event.setSucceed(EventStickerAnalysis.STICKER_LOAD_FAILED);
            event.setUrl(this.url);
            event.setParam(this.param);
            EventBus.getDefault().post(event);
        }
    }
}
