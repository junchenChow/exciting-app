package me.vociegif.android.mvp;




import me.vociegif.android.mvp.hepler.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class ParamsBuilder {

    private Map<String, String> mParams;

    public ParamsBuilder() {
        mParams = new HashMap<>();
    }

    public Map<String, String> build(Object object) {
        mParams.put("data", RetrofitClient.baseRequestMsg(object));
        return mParams;
    }
}
