package me.vociegif.android.mvp.api;


import me.vociegif.android.helper.ConstantsPath;
import me.vociegif.android.helper.utils.CommonUtil;
import me.vociegif.android.mvp.ParamsBuilder;
import me.vociegif.android.mvp.hepler.retrofit.RetrofitClient;
import me.vociegif.android.mvp.vo.StickerAdapt2Result;
import me.vociegif.android.mvp.vo.VersionResult;
import me.vociegif.android.mvp.vo.VoiceEntity;

import java.util.Map;

import javax.inject.Inject;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class VoiceMainApi {
    private Api mApi;

    @Inject
    public VoiceMainApi() {
        mApi = RetrofitClient.createApi(Api.class);
    }

    public Observable<StickerAdapt2Result> getVoiceResponse(String text){
        return mApi.getVoiceResponse(new ParamsBuilder().build(new VoiceEntity(text, ConstantsPath.SMART_EMOTION)));
    }

    public Observable<VersionResult> checkVersionUpdate(){
        VoiceEntity voiceEntity = new VoiceEntity(ConstantsPath.VERSION_CHECK);
        voiceEntity.setVersion(CommonUtil.getVersion());
        voiceEntity.setOperatingSystem(CommonUtil.getOSVersion());
        return mApi.checkVersionUpdate(new ParamsBuilder().build(voiceEntity));
    }

    interface Api {
        @FormUrlEncoded
        @POST(ConstantsPath.VOICE_URL)
        Observable<StickerAdapt2Result> getVoiceResponse(@FieldMap Map<String, String> loginInfo);

        @FormUrlEncoded
        @POST(ConstantsPath.VOICE_URL)
        Observable<VersionResult> checkVersionUpdate(@FieldMap Map<String, String> loginInfo);
    }
}
