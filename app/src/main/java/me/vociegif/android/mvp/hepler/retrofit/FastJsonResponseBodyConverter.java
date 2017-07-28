package me.vociegif.android.mvp.hepler.retrofit;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.buhuavoice.app.BuildConfig;
import me.vociegif.android.event.EventServerError;
import me.vociegif.android.helper.Constants;
import me.vociegif.android.helper.utils.CryptUtil;
import me.vociegif.android.mvp.hepler.ProgressBarHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.greenrobot.eventbus.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private Type type;

    public FastJsonResponseBodyConverter(Type type) {
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        InputStreamReader isr = null;
        BufferedReader bf = null;
        try {
            isr = new InputStreamReader(value.byteStream(), UTF_8);
            bf = new BufferedReader(isr);
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = bf.readLine()) != null) {
                buffer.append(line);
            }
            String strResult = CryptUtil.decompressNetData(CryptUtil.MD51(CryptUtil.encryptMD5(buffer.toString()), buffer.toString()));
            Gson gson = new Gson();
            JsonArray jsonArray;
            try {
                JsonParser parser = new JsonParser();
                JsonElement el = parser.parse(strResult);
                jsonArray = el.getAsJsonArray();
            } catch (Exception e) {
                EventBus.getDefault().post(new EventServerError("网络消息错误，请稍后重试！"));
                return null;
            }
            int forlen = jsonArray.size();
            for (int i = 0; i < forlen; i++) {
                JsonObject obj = (JsonObject) jsonArray.get(i);
                int code = obj.get("code").getAsInt();
                switch (code) {
                    case Constants.requestType.UPDATE:
                    break;
                    case Constants.requestType.LOGIN:
                        break;
                    case Constants.requestType.VERIFY_INVALID:
                        ProgressBarHelper.getInstance(true).dismiss();
                        break;
                    case Constants.requestType.SERVER_ERROR:
                        String message = "";
                        if (obj.has("msg")) {
                            message = obj.get("msg").getAsString();
                        }
                        EventBus.getDefault().post(new EventServerError(message));
                        ProgressBarHelper.getInstance(true).dismiss();
                        break;
                    default: {
                        if(BuildConfig.DEBUG)
                        Log.e("json",obj.toString());
                        return gson.fromJson(obj, type);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }
}
