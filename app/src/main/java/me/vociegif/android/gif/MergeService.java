package me.vociegif.android.gif;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import me.vociegif.android.event.EventMergeFinish;
import me.vociegif.android.gif.gif_vo.GifMinutes;
import me.vociegif.android.helper.utils.FileUtil;
import me.vociegif.android.mvp.vo.StickerAdapt2Result;
import me.vociegif.android.mvp.vo.StickerContentFull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Yoh Asakura.
 *
 * @version 1.0
 *
 * Address : https://github.com/junchenChow
 */
public class MergeService extends IntentService {

    public static final String MERGE_DATA = "mergeData";

    public static void intentMergeService(Activity activity, StickerAdapt2Result result) {
        Intent intent = new Intent(activity, MergeService.class);
        intent.putExtra(MERGE_DATA, result);
        activity.startService(intent);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MergeService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        StickerAdapt2Result stickerAdapt2Result = (StickerAdapt2Result) intent.getSerializableExtra(MERGE_DATA);
        beginMerge(stickerAdapt2Result);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void beginMerge(StickerAdapt2Result result) {
        final List<GifMinutes> gifMinutesList = new ArrayList<>();
        final Map<String, StickerContentFull> contentFullHashMap = new HashMap<>();
        MergeFactory.getDefault().drawStickers(gifMinutesList, contentFullHashMap, result);
        String gifPath = FileUtil.getAppFilePath(FileUtil.DIR_GIF) + SimpleDateFormat.getDateTimeInstance().getCalendar().getTime().getTime();
        GifMergeHelper.encodeForJava(gifMinutesList, gifPath);
        EventBus.getDefault().post(new EventMergeFinish(gifPath));
    }
}
