package me.vociegif.android.helper.share.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import me.vociegif.android.App;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.ConstantsPath;
import me.vociegif.android.helper.share.iterface.GetPictureListener;
import me.vociegif.android.helper.share.iterface.SaveFileListener;
import me.vociegif.android.helper.utils.FileUtils_zm;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by design on 2016/3/28.
 */
public class ImageUtils {

    public Bitmap bitmap;
    public String shareUrl;

    public ImageUtils() {

    }

    public void getBitmap(String url, GetPictureListener listener) {

        File files = new File(url);
        if(files.exists()){
            String savePath = ConstantsPath.getDCIMPath();
            FileUtils_zm.getInst().copyFile(files.toString(), savePath);
            Bitmap bitmapLocal = BitmapFactory.decodeFile(savePath);
            Log.e("ImageUtils", "file.exists()　image file path：" + url + "bitmapLocal size:" + bitmapLocal.getByteCount());
            listener.onSuccess(savePath, bitmapLocal);
            return;
        }

        Log.e("ImageUtils", "url :" + url);
        String picurl = Constants.setAliyunImageUrl(url);
        // 图片
        String filepath = ImageLoader.getInstance().getDiskCache().get(picurl).getAbsolutePath();

        File file = new File(filepath);
        if (file.exists()) {
            String savePath = ConstantsPath.getDCIMPath();
            FileUtils_zm.getInst().copyFile(file.toString(), savePath);
            Bitmap bitmapLocal = BitmapFactory.decodeFile(savePath);
            Log.e("ImageUtils", "file.exists()　image file path：" + filepath + "bitmapLocal size:" + bitmapLocal.getByteCount());
            listener.onSuccess(savePath, bitmapLocal);
        } else {
            ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    Log.e("imageLoadingListener", "onLoadingStarted　imageUri：" + imageUri);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    Log.e("imageLoadingListener", "onLoadingFailed　failReason：" + failReason.getCause());
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    listener.onSuccess(null, loadedImage);
                    Log.e("imageLoadingListener", "onLoadingComplete　savePath：" + imageUri + "loadedImage" +
                            loadedImage + "size:" + loadedImage.getByteCount());
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    Log.e("imageLoadingListener", "onLoadingCancelled　imageUri：" + imageUri);
                }
            };
            ImageLoader.getInstance().loadImage(picurl, imageLoadingListener);
        }
    }

    public void getBitmapUrl(String url, GetPictureListener listener) {

        File files = new File(url);
        if(files.exists()){
            String savePath = ConstantsPath.getDCIMPath();
            FileUtils_zm.getInst().copyFile(files.toString(), savePath);
            listener.onSuccess(savePath, null);
            return;
        }
        String picurl = Constants.setAliyunImageUrl(url);
        // 图片
        String filepath = ImageLoader.getInstance().getDiskCache().get(picurl).getAbsolutePath();
        Log.e("getBitmapUrl", "picurl: " + picurl +  "---url:" + url + "-- filepath :" + filepath);

        File file = new File(filepath);
        if (file.exists()) {
            String savePath = ConstantsPath.getDCIMPath();
            FileUtils_zm.getInst().copyFile(file.toString(), savePath);

            listener.onSuccess(filepath, null);
            Log.e("getBitmapUrl", "file.exists()　savePath：" + savePath + "file path :" + filepath + "shareUrl:" + shareUrl);

        } else {
            ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    Log.e("imageLoadingListener", "onLoadingStarted　imageUri：" + imageUri);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    Log.e("imageLoadingListener", "onLoadingFailed　imageUri：" + imageUri);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    String savePath = ConstantsPath.getDCIMPath();
                    saveBitmap(loadedImage, savePath);
                    Log.e("imageLoadingListener", "shareUrl" + shareUrl + "savePath:" + savePath);
                    MediaScannerConnection.scanFile(App.getDefault(),
                            new String[] { savePath }, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                }
                            });
                    listener.onSuccess(savePath, null);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    Log.e("imageLoadingListener", "onLoadingCancelled　imageUri：" + imageUri);
                }
            };

            Log.e("imageLoadingListener", "picurl：" + filepath);
            ImageLoader.getInstance().loadImage(picurl, imageLoadingListener);
        }

    }



    public Bitmap saveBitmap(Bitmap mBitmap, String bitName)  {
        bitmap = null;
        Log.e("saveBitmap", "bitName：" +bitName);

        File f = new File(bitName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    public void saveShareFile(String url, SaveFileListener listener) {
        File files = new File(url);
        if(files.exists()){
            String savePath = ConstantsPath.getDCIMPath();
            FileUtils_zm.getInst().copyFile(files.toString(), savePath);
            listener.onSuccess(savePath);
            return;
        }

        String[] resultArr = url.split("/");
        String baseName = resultArr[resultArr.length - 1];
        String savepath = ConstantsPath.getCachePath() + "/" + String.valueOf(baseName.hashCode());

        HttpUtils http = new HttpUtils();
        File file = new File(savepath);
        if (file.exists()) {
            listener.onSuccess(savepath);
            return;
        }


        http.download(Constants.setAliyunImageUrl(url), savepath, true, true,
                new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                listener.onSuccess(savepath);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e("saveShareFile", "failed" + e.getMessage() + " -- message: " + s);
            }
        });

    }
}
