package me.vociegif.android.mvp.hepler.retrofit;


import com.buhuavoice.app.BuildConfig;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.utils.CryptUtil;
import me.vociegif.android.mvp.vo.RequestBase;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;


/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public final class RetrofitClient {

    public static <T> T createApi(Class<T> clazz) {
        Retrofit builder = new Retrofit.Builder()
                .baseUrl(Constants.setAliyunImageUrl(BuildConfig.API_ROOT_URL))
                .addConverterFactory(OneOfMoreJsonConvertFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getClient())
                .build();

        return builder.create(clazz);
    }


    private static OkHttpClient getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static String baseRequestMsg(Object object) {
        RequestBase infoRequest = new RequestBase();
        Gson gson = new Gson();
        infoRequest.setData(object);
        String compressStr = CryptUtil.compressData(gson.toJson(infoRequest));
        String encryptStr = CryptUtil.encryptMD5(compressStr);
        return CryptUtil.MD5(encryptStr, compressStr);
    }
}
