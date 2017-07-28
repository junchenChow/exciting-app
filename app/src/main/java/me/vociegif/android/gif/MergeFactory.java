package me.vociegif.android.gif;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;

import com.glidebitmappool.GlideBitmapFactory;
import com.glidebitmappool.GlideBitmapPool;
import me.vociegif.android.App;
import me.vociegif.android.event.EventMergeProgress;
import me.vociegif.android.gif.gif_vo.GifMinutes;
import me.vociegif.android.helper.utils.BitmapPostUtil;
import me.vociegif.android.helper.utils.CommonUtil;
import me.vociegif.android.helper.utils.FileUtil;
import me.vociegif.android.mvp.vo.StickerAdapt2Result;
import me.vociegif.android.mvp.vo.StickerContentFull;
import me.vociegif.android.mvp.vo.StickerPaint;

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
public class MergeFactory {

    private float scaleImg = (float) 1.0;
    private static final int TYPE_STICKER = 0;
    private static final int TYPE_STICKER_TEXT = 1;
    private static final int TYPE_STICKER_GIF = 4;
    static volatile MergeFactory defaultInstance;

    public MergeFactory() {
        this.scaleImg = 300f / App.getDefault().getScreenWidth();
    }

    public static MergeFactory getDefault() {
        if (defaultInstance == null) {
            synchronized (MergeFactory.class) {
                if (defaultInstance == null) {
                    defaultInstance = new MergeFactory();
                }
            }
        }
        return defaultInstance;
    }

    public void drawStickers(List<GifMinutes> gifMinutesList, Map<String, StickerContentFull> contentFullHashMap, StickerAdapt2Result result) {
        float progress;
        contentFullHashMap.clear();
        for (StickerContentFull stickerContentFull : result.getList()) {
            String key = String.valueOf(stickerContentFull.getSeriesid()) + stickerContentFull.getStickerid();
            contentFullHashMap.put(key, stickerContentFull);
        }

        for (int i = 0, len = result.getFramecount(); i < len; i++) {
            final Bitmap localBitmap = GlideBitmapPool.getBitmap(300, 300, Bitmap.Config.RGB_565);
            final Canvas localCanvas = new Canvas(localBitmap);
            final Paint localPaint = new Paint();
            checkBackgroundIsDrawLocal(result, localCanvas, localPaint);
            localCanvas.save();
            for (StickerPaint stickerPaint : result.getStickerList()) {
                String key = String.valueOf(stickerPaint.getSeriesid()) + stickerPaint.getStickerid();
                StickerContentFull stickerContentFull = contentFullHashMap.get(key);
                getTypeDrawBitmap(stickerPaint, localCanvas, localPaint, stickerContentFull, i);
            }
            GifMinutes gifMinutes = new GifMinutes();
            gifMinutes.setDelay(100);
            gifMinutes.setBitmap(localBitmap);
            gifMinutesList.add(gifMinutes);
            progress = 40 + 20f * i / len;
            EventBus.getDefault().post(new EventMergeProgress(progress));
        }
    }

    public void checkBackgroundIsDrawLocal(StickerAdapt2Result result, Canvas localCanvas, Paint localPaint) {
        if (result.getScenes() != null && !result.getScenes().isEmpty()) {
            int hashName = FileUtil.STICKER_BG_NAME.hashCode();
            String path = FileUtil.getAppFilePath(FileUtil.DIR_STICKERS) + hashName;
            Bitmap bitmap = GlideBitmapFactory.decodeFile(path);
            if (bitmap != null) {
                bitmap = BitmapPostUtil.resizeBitmap(bitmap, 300, 300);
                localCanvas.drawBitmap(bitmap, 0, 0, localPaint);
            }
        } else {
            localCanvas.drawColor(0xffffffff);
        }
    }

    /**
     * 根据type来draw外层序列bitmap里的贴纸元素
     *
     * @param stickerPaint
     * @param localCanvas
     * @param localPaint
     * @param stickerContentFull
     * @param i
     */
    public void getTypeDrawBitmap(StickerPaint stickerPaint, Canvas localCanvas, Paint localPaint, StickerContentFull stickerContentFull, int i) {

        switch (stickerContentFull.getType()) {
            case TYPE_STICKER: {
                String name = String.valueOf(stickerContentFull.getSeriesid()) + "_" + stickerContentFull.getStickerid();
                String path = FileUtil.getAppFilePath(FileUtil.DIR_STICKERS) + name.hashCode();
                Bitmap bitmap = GlideBitmapFactory.decodeFile(path);
                int x = (int) (stickerPaint.getPosX() * scaleImg - bitmap.getWidth() / 2 * stickerPaint.getScale());
                int y = (int) (stickerPaint.getPosY() * scaleImg - bitmap.getHeight() / 2 * stickerPaint.getScale());
                localCanvas.drawBitmap(BitmapPostUtil.rotateWithScale(bitmap, stickerPaint.getScale(), stickerPaint.getRotate()), x, y, localPaint);
            }
            break;

            case TYPE_STICKER_TEXT: {
                RectF mCropRect = new RectF(stickerPaint.getCropRect()[0], stickerPaint.getCropRect()[1], stickerPaint.getCropRect()[2], stickerPaint.getCropRect()[3]);
                List<Integer> textRange = stickerPaint.getTextRange();
                float scale = stickerPaint.getScale();
                float tx = ((float) (textRange.get(0)) / scaleImg);
                float ty = ((float) (textRange.get(1)) / scaleImg);
                float tWidth = ((float) (textRange.get(2)) / scaleImg * scale);
                float tHeight = ((float) (textRange.get(3)) / scaleImg * scale);
                RectF textRect = new RectF(mCropRect.left + tx, mCropRect.top + ty, mCropRect.left + tx + tWidth, mCropRect.top + ty + tHeight);

                localPaint.setAntiAlias(true);
                localPaint.setColor(Color.BLACK);
                localPaint.setDither(true);
                localPaint.setTextAlign(Paint.Align.CENTER);
                localPaint.setTextSize(stickerPaint.getFontSize());
                localPaint.setStyle(Paint.Style.FILL_AND_STROKE);

                drawTextRange(localCanvas, stickerPaint.getText(), textRect, localPaint, stickerPaint.getColor(), stickerPaint.getShadowcolor());
            }
            break;

            case TYPE_STICKER_GIF: {
                String path = stickerContentFull.getPngname().get(i % stickerContentFull.getPngname().size());
                if (!TextUtils.isEmpty(path)) {
                    Bitmap bitmap = GlideBitmapFactory.decodeFile(FileUtil.getAppFilePath(FileUtil.DIR_FRAME) + path);
                    int x = (int) (stickerPaint.getPosX() * scaleImg - bitmap.getWidth() / 2 * stickerPaint.getScale());
                    int y = (int) (stickerPaint.getPosY() * scaleImg - bitmap.getHeight() / 2 * stickerPaint.getScale());
                    localCanvas.drawBitmap(BitmapPostUtil.rotateWithScale(bitmap, stickerPaint.getScale(), stickerPaint.getRotate()), x, y, localPaint);
                }
            }
            break;
        }
    }

    public void drawTextRange(Canvas canvas, String text, RectF textRect, Paint textPaint, int color, int shadowColor) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        // 计算行高
        //float textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        float textHeight = fm.bottom - fm.top - 8;
        float rectHeight = textRect.height();
        if (textHeight > rectHeight)
            return;
        // 字体宽度
        float widthText = textPaint.measureText(text);
        float rectWidth = textRect.width() * scaleImg;
        // 行数
        float line = widthText / rectWidth;
        float baseline = ((textRect.bottom + textRect.top) * scaleImg - fm.bottom - fm.top) / 2;
        if (line <= 1) {
            textPaint.setColor(shadowColor);
            canvas.drawText(text, textRect.centerX() * scaleImg - 2, baseline, textPaint);
            canvas.drawText(text, textRect.centerX() * scaleImg + 2, baseline, textPaint);
            canvas.drawText(text, textRect.centerX() * scaleImg, baseline + 2, textPaint);
            canvas.drawText(text, textRect.centerX() * scaleImg, baseline - 2, textPaint);
            textPaint.setColor(color);
            canvas.drawText(text, textRect.centerX() * scaleImg, baseline, textPaint);
        } else {
            baseline -= (textHeight / 2 + 12);
            float[] widths = new float[text.length()];
            textPaint.getTextWidths(text, widths);
            int newline = formatFloatToInt(line);
            // 计算基线
            //float contentHeight = newline * textHeight;
            //if (contentHeight < rectHeight) {
            //    baseline = textRect.top + (rectHeight - contentHeight) / 2 + textHeight;
            //}
            float baseWidth = 0;
            // idline为行数
            for (int index = 0, idline = 0, lastindex = 0; index < widths.length; index++) {
                // 本行宽度
                float temp = (baseWidth + widths[index]);
                float dis = rectWidth - temp;
                if (index + 1 != widths.length) {
                    if (dis >= (widths[index + 1])) {
                        baseWidth = temp;
                        continue;
                    }
                    textPaint.setColor(shadowColor);
                    canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg) - 2, baseline + idline * textHeight, textPaint);
                    canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg) + 2, baseline + idline * textHeight, textPaint);
                    canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg), baseline + idline * textHeight + 2, textPaint);
                    canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg), baseline + idline * textHeight - 2, textPaint);
                    textPaint.setColor(color);
                    canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg), baseline + idline * textHeight, textPaint);

                    baseWidth = 0;
                    lastindex = index + 1;
                    idline++;
                    if ((baseline + idline * textHeight) > (textRect.bottom * scaleImg)) {
                        break;
                    }
                    continue;
                }

                if ((baseline + idline * textHeight) > (textRect.bottom * scaleImg)) {
                    break;
                }
                textPaint.setColor(shadowColor);
                canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg) - 2, baseline + idline * textHeight, textPaint);
                canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg) + 2, baseline + idline * textHeight, textPaint);
                canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg), baseline + idline * textHeight + 2, textPaint);
                canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg), baseline + idline * textHeight - 2, textPaint);
                textPaint.setColor(color);
                canvas.drawText(text, lastindex, index + 1, (textRect.centerX() * scaleImg), baseline + idline * textHeight, textPaint);
            }
        }
    }

    public int formatFloatToInt(float line) {
        if ((line - (int) line) > 0f)
            return (int) (line + 1);
        return (int) line;
    }
}
