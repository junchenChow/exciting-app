package me.vociegif.android.mvp.hepler.retrofit;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;


/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class FastJsonRequestBodyConvert<T> implements Converter<T, RequestBody> {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    @Override
    public RequestBody convert(T value) throws IOException {
        Gson gson = new Gson();
        return RequestBody.create(MEDIA_TYPE,gson.toJson(value).getBytes(UTF_8));
    }
}