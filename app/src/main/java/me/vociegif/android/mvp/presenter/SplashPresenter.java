package me.vociegif.android.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.vociegif.android.event.EventVersionUpdate;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.ConstantsPath;
import me.vociegif.android.helper.MoHoSharedPreferences;
import me.vociegif.android.helper.utils.FileUtil;
import me.vociegif.android.mvp.BasePresenter;
import me.vociegif.android.mvp.api.VoiceMainApi;
import me.vociegif.android.mvp.vo.VersionUpdateEntity;
import me.vociegif.android.ui.activitys.VoiceMainActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class SplashPresenter extends BasePresenter {
    private VoiceMainApi mVoiceMainApi;
    private MoHoSharedPreferences mPreferences;
    private VersionUpdateEntity mVersionResult;

    @Inject
    SplashPresenter(VoiceMainApi voiceMainApi, MoHoSharedPreferences preferences) {
        this.mVoiceMainApi = voiceMainApi;
        this.mPreferences = preferences;
    }

    public void checkVersionUpdate() {
        mVoiceMainApi.checkVersionUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(data -> {
                    if (!TextUtils.isEmpty(data.getWelcomeimage())) {
                        String[] resultArr = data.getWelcomeimage().split("/");
                        String baseName = resultArr[resultArr.length - 1];
                        String mStrWelcomeImageLocalPath = ConstantsPath.getCachePath() + "/" + String.valueOf(baseName.hashCode());
                        if (!FileUtil.isFileExist(mStrWelcomeImageLocalPath)) {
                            downloadSplashImage(data.getWelcomeimage(), mStrWelcomeImageLocalPath);
                        }
                    }
                })
                .subscribe(dataResponse -> {
                    mVersionResult = dataResponse.getVersionchange();
                    EventBus.getDefault().post(new EventVersionUpdate(dataResponse.getVersionchange()));
                }, Throwable::printStackTrace);
    }

    public void openVoiceMainActivity(Activity activity) {
        Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Intent intent = new Intent(activity, VoiceMainActivity.class);
                    intent.putExtra(VoiceMainActivity.CHECK_VERSION, mVersionResult);
                    activity.startActivity(intent);
                    activity.finish();
                }, Throwable::printStackTrace);
    }

    public String getSplashImageFilePath() {
        return mPreferences.getSplashImagePath();
    }


    private void downloadSplashImage(String url, String mStrWelcomeImageLocalPath) {
        HttpUtils http = new HttpUtils();
        http.download(Constants.setAliyunImageUrl(url), mStrWelcomeImageLocalPath, true, true,
                new RequestCallBack<File>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> arg0) {
                        File file = arg0.result;
                        File newPath = new File(mStrWelcomeImageLocalPath);
                        file.renameTo(newPath);
                        mPreferences.saveSplashImagePath(Constants.setAliyunImageUrl(url));
                    }
                });
    }
}
