package me.vociegif.android.helper.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.buhuavoice.app.BuildConfig;
import com.glidebitmappool.GlideBitmapFactory;

import me.vociegif.android.event.EventShare;
import me.vociegif.android.helper.share.entity.ShareException;
import me.vociegif.android.helper.share.iterface.GetPictureListener;
import me.vociegif.android.helper.share.iterface.SaveFileListener;
import me.vociegif.android.helper.share.iterface.Share;
import me.vociegif.android.helper.share.iterface.ShareListener;
import me.vociegif.android.helper.share.utils.ImageUtils;
import me.vociegif.android.helper.share.utils.ShareConstant;
import me.vociegif.android.helper.utils.ToastUtil;
import me.vociegif.android.helper.utils.Util;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXEmojiObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;


public class ShareManager {

    private String shareUrl = "";
    private String thumbUrl = "";
    private String thumbSmallUrl = "";

    private Share sharePlatform;
    private ShareListener shareListener;
    private WeakReference<Activity> mActivity;

    public void onCreate(Activity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public void auth(int platform) {
        if (mActivity == null)
            return;
        Activity activity = mActivity.get();
        if (activity == null)
            return;

        prepareHandle(activity, platform);
        switch (platform) {
            case ShareConstant.SHARE_MEDIA.QQ:
                sharePlatform = new ShareQQ();
                break;

            case ShareConstant.SHARE_MEDIA.WECHAT:
                sharePlatform = new ShareWechat();
                break;

            case ShareConstant.SHARE_MEDIA.SINA:
                sharePlatform = new ShareSina();
                break;

            default:
                break;
        }

        sharePlatform.setShareListener(shareListener);
        sharePlatform.auth(activity);
    }

    public void login(int platform) {
        if (mActivity == null)
            return;

        Activity activity = mActivity.get();
        if (activity == null)
            return;

        switch (platform) {
            case ShareConstant.SHARE_MEDIA.QQ:
                if (sharePlatform == null) {
                    sharePlatform = new ShareQQ();
                    sharePlatform.setShareListener(shareListener);
                    sharePlatform.auth(activity);
                }
                break;

            case ShareConstant.SHARE_MEDIA.SINA:
                if (sharePlatform == null) {
                    sharePlatform = new ShareSina();
                    sharePlatform.setShareListener(shareListener);
                    sharePlatform.auth(activity);
                }
                break;

            case ShareConstant.SHARE_MEDIA.WECHAT:
                if (sharePlatform == null) {
                    sharePlatform = new ShareWechat();
                    sharePlatform.setShareListener(shareListener);
                    sharePlatform.auth(activity);
                }
                break;

            default:
                break;
        }
        sharePlatform.login(activity);
    }

    public boolean isAuthuried(int platform) {
        final Activity activity = mActivity.get();
        if (activity == null)
            return false;

        sharePlatform = null;
        switch (platform) {
            case ShareConstant.SHARE_MEDIA.QQ:
            case ShareConstant.SHARE_MEDIA.QQ_ZONE:
                sharePlatform = new ShareQQ();
                break;

            case ShareConstant.SHARE_MEDIA.WECHAT:
            case ShareConstant.SHARE_MEDIA.WECHAT_CIRCLE:
                sharePlatform = new ShareWechat();
                break;

            case ShareConstant.SHARE_MEDIA.SINA:
                sharePlatform = new ShareSina();
                break;

            default:
                break;
        }

        boolean flag = sharePlatform.isAuthuried();
        sharePlatform.onDestroy();
        sharePlatform = null;

        return flag;
    }

    public boolean isInstalled(int platform) {

        final Activity activity = mActivity.get();
        if (activity == null)
            return false;

        sharePlatform = null;
        switch (platform) {
            case ShareConstant.SHARE_MEDIA.QQ:
            case ShareConstant.SHARE_MEDIA.QQ_ZONE:
                sharePlatform = new ShareQQ();
                break;

            case ShareConstant.SHARE_MEDIA.WECHAT:
            case ShareConstant.SHARE_MEDIA.WECHAT_CIRCLE:
                sharePlatform = new ShareWechat();
                break;

            case ShareConstant.SHARE_MEDIA.SINA:
                sharePlatform = new ShareSina();
                break;

            default:
                break;
        }

        boolean flag = sharePlatform.isInstalled(activity);
        sharePlatform.onDestroy();
        sharePlatform = null;
        return flag;
    }


    private void prepareHandle(Activity activity, int platform) {

        if (platform == ShareConstant.SHARE_MEDIA.WECHAT || platform == ShareConstant.SHARE_MEDIA.WECHAT_CIRCLE) {
            if (!ShareWechat.isInstalled(activity, 0)) {
                ShareException exception = new ShareException("not_installed");
                shareListener.onException(ShareConstant.NOT_INSTALLED, platform, exception);
                return;
            }
        } else if (platform == ShareConstant.SHARE_MEDIA.SINA) {
            if (!ShareSina.isInstalled(activity, 0)) {
                ShareException exception = new ShareException("not_installed");
                shareListener.onException(ShareConstant.NOT_INSTALLED, platform, exception);
                return;
            }
        } else {
            if (!isInstalled(platform)) {
                ShareException exception = new ShareException("not_installed");
                shareListener.onException(ShareConstant.NOT_INSTALLED, platform, exception);
                return;
            }
        }

        if (sharePlatform != null) {
            sharePlatform.onDestroy();
            sharePlatform = null;
        }
    }

    public void share(int platform, String contentId) {

        if (mActivity == null)
            return;
        Activity activity = mActivity.get();
        if (activity == null || TextUtils.isEmpty(shareUrl))
            return;

        prepareHandle(activity, platform);

        switch (platform) {
            case ShareConstant.SHARE_MEDIA.QQ: {
                sharePlatform = new ShareQQ();
                sharePlatform.setShareListener(shareListener);
                sharePlatform.setContentId(contentId);

                new ImageUtils().getBitmapUrl(shareUrl, new GetPictureListener() {
                    @Override
                    public void onSuccess(String url, Bitmap bitmap) {
                        sharePlatform.share(ShareConstant.SHARE_MEDIA.QQ,
                                url, activity);
                    }

                    @Override
                    public void onFailed() {
                        ShareException exception = new ShareException("load Image Failed");
                        shareListener.onException(ShareConstant.IAMGE_LOAD_FAILED, platform, exception);
                        sharePlatform.onDestroy();
                        sharePlatform = null;
                    }
                });
                break;
            }
            case ShareConstant.SHARE_MEDIA.QQ_ZONE: {
                sharePlatform = new ShareQQ();
                sharePlatform.setShareListener(shareListener);
                sharePlatform.setContentId(contentId);

                new ImageUtils().getBitmapUrl(shareUrl, new GetPictureListener() {
                    @Override
                    public void onSuccess(String url, Bitmap bitmap) {
                        sharePlatform.share(ShareConstant.SHARE_MEDIA.QQ_ZONE,
                                url, activity);
                    }

                    @Override
                    public void onFailed() {
                        ShareException exception = new ShareException("load Image Failed");
                        shareListener.onException(ShareConstant.IAMGE_LOAD_FAILED, platform, exception);
                        sharePlatform.onDestroy();
                        sharePlatform = null;
                    }
                });

                break;
            }
            case ShareConstant.SHARE_MEDIA.WECHAT: {
                sharePlatform = new ShareWechat();
                sharePlatform.setShareListener(shareListener);
                sharePlatform.setContentId(contentId);

                new ImageUtils().saveShareFile(shareUrl, new SaveFileListener() {
                    @Override
                    public void onSuccess(String savePath) {

                        String finalShareThunmbUrl = thumbUrl;
                        if (!TextUtils.isEmpty(thumbSmallUrl)) {
                            finalShareThunmbUrl = thumbSmallUrl;
                        }

                        new ImageUtils().getBitmap(finalShareThunmbUrl, new GetPictureListener() {
                            @Override
                            public void onSuccess(String url, Bitmap bitmap) {
                                sharePlatform.setSavePath(savePath);
                                sharePlatform.share(activity, null,
                                        bitmap, ShareConstant.SHARE_MEDIA.WECHAT);
                            }

                            @Override
                            public void onFailed() {
                                ShareException exception = new ShareException("load Image Failed");
                                shareListener.onException(ShareConstant.IAMGE_LOAD_FAILED, platform, exception);
                                sharePlatform.onDestroy();
                                sharePlatform = null;
                            }
                        });
                    }
                });

                break;
            }
            case ShareConstant.SHARE_MEDIA.WECHAT_CIRCLE: {

                sharePlatform = new ShareWechat();
                sharePlatform.setShareListener(shareListener);
                sharePlatform.setContentId(contentId);

                new ImageUtils().saveShareFile(shareUrl, new SaveFileListener() {
                    @Override
                    public void onSuccess(String savePath) {

                        String finalShareThunmbUrl = thumbUrl;
                        if (!TextUtils.isEmpty(thumbSmallUrl)) {
                            finalShareThunmbUrl = thumbSmallUrl;
                        }

                        new ImageUtils().getBitmap(finalShareThunmbUrl, new GetPictureListener() {
                            @Override
                            public void onSuccess(String url, Bitmap bitmap) {
                                sharePlatform.setSavePath(savePath);
                                sharePlatform.share(activity, null, bitmap, ShareConstant.SHARE_MEDIA.WECHAT_CIRCLE);
                            }

                            @Override
                            public void onFailed() {
                                ShareException exception = new ShareException("load Image Failed");
                                shareListener.onException(ShareConstant.IAMGE_LOAD_FAILED, platform, exception);
                                sharePlatform.onDestroy();
                                sharePlatform = null;
                            }

                        });
                    }
                });
                break;

            }
            case ShareConstant.SHARE_MEDIA.SINA: {
                sharePlatform = new ShareSina();
                sharePlatform.setShareListener(shareListener);
                sharePlatform.setContentId(contentId);

                Log.e("ShareSina:", "----------------------start--------------");
                new ImageUtils().getBitmap(shareUrl, new GetPictureListener() {
                    @Override
                    public void onSuccess(String url, Bitmap bitmap) {
                        Log.e("ShareSina:", "-----------------getted pic bitmap-----start share--------------");
                        sharePlatform.setSavePath(url);
                        sharePlatform.share(activity, bitmap, null, ShareConstant.SHARE_MEDIA.SINA);
                        Log.e("ShareSina:", "----------------------end--------------");
                    }

                    @Override
                    public void onFailed() {
                        ShareException exception = new ShareException("load Image Failed");
                        shareListener.onException(ShareConstant.IAMGE_LOAD_FAILED, platform, exception);
                        sharePlatform.onDestroy();
                        sharePlatform = null;
                        Log.e("ShareSina:", "----------------------end--------------");
                    }
                });

                break;
            }

            default:
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (sharePlatform != null)
            sharePlatform.onActivityResult(requestCode, resultCode, data);
    }

    public ShareListener getShareListener() {
        return shareListener;
    }

    public void setShareListener(ShareListener shareListener) {
        this.shareListener = shareListener;
    }

    public void setShareImage(String url, String thumbUrl, String thumbSmallUrl) {
        this.shareUrl = url;
        this.thumbUrl = thumbUrl;
        this.thumbSmallUrl = thumbSmallUrl;
    }

    private IWXAPI api;

    public void shareEmoticon(Context context, String path, boolean isFriends) {
        if (api == null) {
            api = WXAPIFactory.createWXAPI(context, BuildConfig.WECHAT_APP_ID, false);
            api.registerApp(BuildConfig.WECHAT_APP_ID);
        }
        File file = new File(path);
        if (!file.exists()) {
            ToastUtil.show(context, "文件不存在");
            return;
        }
        WXEmojiObject emojiObject = new WXEmojiObject();
        emojiObject.emojiPath = path;
        WXMediaMessage msg = new WXMediaMessage(emojiObject);
        msg.description = "表情";
        //创建一个缩略图
        msg.thumbData = Util.bmpToByteArray(GlideBitmapFactory.decodeFile(path), true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "emotion" + System.currentTimeMillis();
        req.message = msg;

        req.scene = isFriends ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        if (!api.sendReq(req)) {
            EventBus.getDefault().post(new EventShare(false));
            Toast.makeText(context, "分享表情失败", Toast.LENGTH_LONG).show();
        }
    }

    public void clearShareImage() {
        String shareUrl = "";
        String thumbUrl = "";
        String thumbSmallUrl = "";
    }

    public void onDestroyItem() {
        this.clearShareImage();
        if (sharePlatform != null)
            sharePlatform.onDestroy();
    }

    public void onDestroy() {
        this.clearShareImage();

        if (sharePlatform != null)
            sharePlatform.onDestroy();

        this.shareListener = null;

        if (mActivity == null)
            return;

        mActivity.clear();
        mActivity = null;
    }

}
