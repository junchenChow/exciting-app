package me.vociegif.android.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.vociegif.android.App;
import me.vociegif.android.mvp.vo.SeriesDetailHeader;
import me.vociegif.android.mvp.vo.StickerContentFull;
import me.vociegif.android.mvp.vo.StickerMenuResult;
import me.vociegif.android.mvp.vo.StickerSeries;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 存取贴纸信息
 *
 * @author 小贴纸保存形式：               key:  贴纸包Id + _ + 贴纸Id     value： json(String)
 *         贴纸包中的小贴纸列表：     key:  贴纸包Id + _             value: json(List<String>)
 *         整个贴纸包保存形式：        key:  Constants.ALL_STICKER   value: json
 */
public class StickerService {

    /**
     * 通过id获得单个贴纸详细信息
     *
     * param seriesid
     * return DONE
     */
    public static StickerContentFull getStickerById(int stickerId, int seriesId) {
        StickerContentFull stickerContentFull = new StickerContentFull();
        String stickerStr = getStickerJson("" + seriesId + "_" + stickerId);
        Gson gson = new Gson();
        stickerContentFull = gson.fromJson(stickerStr, StickerContentFull.class);
        return stickerContentFull;
    }

    /**
     * 保存单个贴纸信息
     *
     * param sticker 仅保存一个key ＋ value
     *                DONE
     */
    public static void saveSticker(int seriesId, StickerContentFull sticker) {
        Gson gson = new Gson();
        saveStickerJson("" + seriesId + "_" + sticker.getStickerid(), gson.toJson(sticker)); // 保存单个贴纸信息
    }

    public static void saveSticker(String key, StickerContentFull sticker) {
        Gson gson = new Gson();
        saveStickerToJson(key, gson.toJson(sticker)); // 保存单个贴纸信息
    }

    public static void saveStickerToJson(String key, String value) {
        SharedPreferences.Editor editor = App.getDefault().getSharedPreferences(Constants.STICKERPREFER, Context.MODE_PRIVATE).edit();
        try {
            value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        editor.putString(key, value);
        editor.apply();
    }

    public static StickerContentFull getStickerContentFullById(String key) {
        String stickerStr = getStickerContentFullJson(key);
        return new Gson().fromJson(stickerStr, StickerContentFull.class);
    }

    public static String getStickerContentFullJson(String key) {
        SharedPreferences shared = App.getDefault().getSharedPreferences(Constants.STICKERPREFER, Context.MODE_PRIVATE);
        String sjson = shared.getString(key, "");
        String json = "";
        try {
            json = URLDecoder.decode(sjson, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return json;
    }



    /**
     * 通过贴纸包id获得贴纸包信息
     *
     * param seriesId
     * return DONE
     */
    public static StickerSeries getStickerById(int seriesId) {
        StickerSeries stickerSeries = new StickerSeries();
        Gson gson = new Gson();
        String seriesStr = getStickerJson("" + seriesId);
        stickerSeries = gson.fromJson(seriesStr, StickerSeries.class);
        return stickerSeries;
    }


    /**
     * 保存单个贴纸包信息
     *
     * param sticker 1. 添加keyList信息
     *                2. 添加
     *                DONE
     */
    public static void saveSeries(StickerSeries series) {

        List<String> stickerKeyList = new ArrayList<String>();

        List<StickerContentFull> seriesList = series.getList();// 小贴纸列表
        if (null == seriesList) {
            seriesList = new ArrayList<StickerContentFull>();
        }
        String seriesKey = "" + series.getStickerHeader().getId(); // 贴纸包Id
        Gson gson = new Gson();
        saveStickerJson(seriesKey, gson.toJson(series)); // 保存整个贴纸包的信息

        for (int i = 0; i < seriesList.size(); i++) {
            String stickerKey = seriesKey + "_" + seriesList.get(i).getStickerid(); // key -》 seriesId_stickerId
            stickerKeyList.add(stickerKey); // 添加Key
            saveStickerJson(stickerKey, gson.toJson(seriesList.get(i))); // 保存单个贴纸信息
        }

        saveStickerJson(seriesKey + "_", gson.toJson(stickerKeyList)); // 保存贴纸里所有小贴纸KEY信息

        //  保存贴纸包Key到配置文件
        List<String> allSeries = new ArrayList<String>();
        if (null != getAllSeries()) {
            allSeries = getAllSeries();
        }
        if (!allSeries.contains(seriesKey)) {
            allSeries.add(seriesKey);
        }

        saveAllSeries(allSeries); // 保存所有贴纸信息

    }

    /**
     * 保存所有背景信息
     *
     * param bgSeriesList
     */
    public static void saveBgSeries(List<StickerSeries> bgSeriesList) {
        Gson gson = new Gson();
        List<String> bgSeries = new ArrayList<String>();
        for (StickerSeries series : bgSeriesList) {
            saveSeries(series); //保存整个贴纸的信息
            bgSeries.add("" + series.getStickerHeader().getId());
        }

        saveStickerJson(Constants.ALL_BG_STICKER_KEY, gson.toJson(bgSeries)); // 保存背景列表中包的Key列表信息
    }

    /**
     * 获得所有背景包贴纸信息
     *
     * return
     */
    public static List<StickerSeries> getBgSeries() {
        Gson gson = new Gson();

        // 获得背景贴纸包key列表

        String bgKeyList = getStickerJson(Constants.ALL_BG_STICKER_KEY);
        List<String> bgSeries = gson.fromJson(bgKeyList, new TypeToken<List<String>>() {
        }.getType());
        if (null == bgSeries) {
            bgSeries = new ArrayList<String>();
        }
        List<StickerSeries> bgSeriesList = new ArrayList<StickerSeries>();
        for (String data : bgSeries) {
            StickerSeries series = getStickerById(Integer.parseInt(data));
            bgSeriesList.add(series);
        }
        return bgSeriesList;
    }


    /**
     * 获得所有贴纸包的信息
     *
     * return DONE
     */
    public static List<String> getAllSeries() {

        List<String> seriesList = new ArrayList<String>();
        Gson gson = new Gson();
        String keyList = getStickerJson(Constants.ALL_STICKER);
        seriesList = gson.fromJson(keyList, new TypeToken<List<String>>() {
        }.getType());

        return seriesList;
    }


    /**
     * 保存所有贴纸包的key列表信息
     *
     * return DONE
     */
    public static void saveAllSeries(List<String> seriesKeyList) {

        Gson gson = new Gson();
        saveStickerJson(Constants.ALL_STICKER, gson.toJson(seriesKeyList));
    }


    /**
     * 获得包中所有贴纸的信息
     *
     * return DONE
     */
    public static List<StickerContentFull> getStickersBySeriesId(int SeriesId) {

        List<String> seriesKeyList = new ArrayList<String>();
        List<StickerContentFull> stickerList = new ArrayList<StickerContentFull>();

        String seriesListStr = getStickerJson("" + SeriesId + "_");
        Gson gson = new Gson();
        seriesKeyList = gson.fromJson(seriesListStr, new TypeToken<List<String>>() {
        }.getType()); // 获得一个包中所有贴纸Key信息

        for (int i = 0; i < seriesKeyList.size(); i++) {
            String stickerStr = getStickerJson(seriesKeyList.get(i));
            StickerContentFull sticker = gson.fromJson(stickerStr, StickerContentFull.class);
            stickerList.add(sticker);
        }

        return stickerList;
    }

    /**
     * 通过贴纸包Id 获得整个贴纸包的信息 ,不包括贴纸信息
     *
     * param stickerId
     * return DONE
     */
    public static SeriesDetailHeader getSeriesHeaderById(int seriesId) {
        StickerSeries series = getStickerById(seriesId);
        return series.getStickerHeader();
    }

    /**
     * 获得编辑器中的简单贴纸信息
     *
     * return DONE
     */
    public static List<StickerMenuResult.StickerSeriesSimple> getEditorBottomBarSticker() {
        List<StickerMenuResult.StickerSeriesSimple> simpleStcikers = new ArrayList<StickerMenuResult.StickerSeriesSimple>();
        String simpleStcikersStr = getStickerJson(Constants.STICKER_EDITOR_MENU);
        Gson gson = new Gson();
        simpleStcikers = gson.fromJson(simpleStcikersStr, new TypeToken<List<StickerMenuResult.StickerSeriesSimple>>() {
        }.getType());

        return simpleStcikers;
    }


    /**
     * 保存编辑器中的简单贴纸信息
     *
     * return DONE
     */
    public static void saveEditorBottomBarSticker(List<StickerMenuResult.StickerSeriesSimple> simpleStcikers) {
        String simpleStcikersStr = new Gson().toJson(simpleStcikers);
        saveStickerJson(Constants.STICKER_EDITOR_MENU, simpleStcikersStr);
    }


    /**
     * 保存贴纸信息
     *
     * param key
     * param value DONE
     */
    public static void saveStickerJson(String key, String value) {
        SharedPreferences.Editor editor = App.getDefault().getSharedPreferences(Constants.STICKERPREFER, Context.MODE_PRIVATE).edit();
        try {
            value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获得贴纸Json信息
     *
     * param key
     * return DONE
     */
    public static String getStickerJson(String key) {
        SharedPreferences shared = App.getDefault().getSharedPreferences(Constants.STICKERPREFER, Context.MODE_PRIVATE);
        String sjson = shared.getString(key, "");
        String json = "";
        try {
            json = URLDecoder.decode(sjson, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * 保存收藏列表
     *
     * param favorList
     */
    public static void saveFavorList(List<StickerMenuResult.StickerSeriesSimple> favorList) {
        saveStickerJson("stickerfavorite", new Gson().toJson(favorList, new TypeToken<List<StickerMenuResult.StickerSeriesSimple>>() {
        }.getType()));
    }

    /**
     * 保存推荐列表
     *
     * param recommendList
     */
    public static void saveRecommendList(List<StickerMenuResult.StickerSeriesSimple> recommendList) {
        saveStickerJson("stickerrecommend", new Gson().toJson(recommendList, new TypeToken<List<StickerMenuResult.StickerSeriesSimple>>() {
        }.getType()));
    }

    /**
     * 获得收藏列表
     *
     * return
     */
    public static List<StickerMenuResult.StickerSeriesSimple> getFavorList() {

        String favorListStr = getStickerJson("stickerfavorite");
        Gson gson = new Gson();

        List<StickerMenuResult.StickerSeriesSimple> favorList = gson.fromJson(favorListStr, new TypeToken<List<StickerMenuResult.StickerSeriesSimple>>() {
        }.getType());

        if (null == favorList) {
            favorList = new ArrayList<StickerMenuResult.StickerSeriesSimple>();
        }

        return favorList;
    }

    /**
     * 获得推荐列表
     *
     * return
     */
    public static List<StickerMenuResult.StickerSeriesSimple> getRecommendList() {

        String recommendListStr = getStickerJson("stickerrecommend");
        Gson gson = new Gson();

        List<StickerMenuResult.StickerSeriesSimple> recommendList = gson.fromJson(recommendListStr, new TypeToken<List<StickerMenuResult.StickerSeriesSimple>>() {
        }.getType());

        if (null == recommendList) {
            recommendList = new ArrayList<StickerMenuResult.StickerSeriesSimple>();
        }

        return recommendList;
    }
}
