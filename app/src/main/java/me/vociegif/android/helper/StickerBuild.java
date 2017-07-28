package me.vociegif.android.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import com.buhuavoice.app.R;

import me.vociegif.android.App;
import me.vociegif.android.helper.utils.CommonUtil;
import me.vociegif.android.helper.utils.MatrixUtils;
import me.vociegif.android.mvp.di.ContextLevel;
import me.vociegif.android.mvp.vo.StickerFont;
import me.vociegif.android.mvp.vo.StickerPaint;
import me.vociegif.android.widget.MyHighlightView;
import me.vociegif.android.widget.MyImageViewDrawableOverlay;
import me.vociegif.android.widget.StickerDrawable;

import org.wlf.filedownloader.FileDownloader;

import javax.inject.Inject;

/**
 * Created by Yoh Asakura.
 */
public class StickerBuild {
    private Context mContext;
    private int mScreenWidth;
    private float mScaleDegrees = (float) 1.0;

    @Inject
    public StickerBuild(@ContextLevel(ContextLevel.APPLICATION) Context context) {
        this.mContext = context;
        mScreenWidth = App.getDefault().getScreenWidth();
        mScaleDegrees = 300f / mScreenWidth;
    }

    public MyHighlightView addStickerImage(MyImageViewDrawableOverlay processImage, StickerPaint paint, Bitmap bitmap, boolean isShowAnchors) {
        Bitmap newBitmap;
        float normalWidth = bitmap.getWidth() / mScaleDegrees;
        float normalHeight = bitmap.getHeight() / mScaleDegrees;
        if (!paint.isEmpty()) {
            paint.setWidth(normalWidth * paint.getScale());
            paint.setHeight(normalHeight * paint.getScale());
            // 缩放效果在此实现（神展开）
            float posX = paint.getPosX();
            float posY = paint.getPosY();
            posX = posX / mScaleDegrees;
            posY = posY / mScaleDegrees;
//            (340f / mScreenWidth)
            Log.i("sticker", "bitmapW: " + bitmap.getWidth() + ", bitmapH: " + bitmap.getHeight() + ", scale: " + mScaleDegrees + ", SCALE" + paint.getScale() + ", POSX: " + paint.getPosX() + ", POSY:" + paint.getPosY() + ", x" + posX + ", y" + posY);
            paint.setPosX(posX);
            paint.setPosY(posY);

            newBitmap = Bitmap.createScaledBitmap(bitmap, (int) paint.getWidth(), (int) paint.getHeight(), true);
        } else {
            newBitmap = Bitmap.createScaledBitmap(bitmap, (int) (normalWidth), (int) (normalHeight), true);
        }
        return addStickerImage(processImage, paint, newBitmap, normalWidth, isShowAnchors);
    }

    public MyHighlightView addStickerImage(MyImageViewDrawableOverlay processImage, StickerPaint paint, Bitmap bitmap, float normalWidthBase, boolean isShowAnchors) {

        StickerDrawable drawable = new StickerDrawable(mContext.getResources(), bitmap);
        drawable.setAntiAlias(true);
        drawable.setMinSize(120, 120);

        final MyHighlightView hv = new MyHighlightView(processImage, R.style.AppTheme, drawable);// FIXME
        hv.setIsTextSticker(false);
        hv.showAnchors(isShowAnchors);
        hv.setPadding(0);
        hv.setWidthBase(normalWidthBase);
        hv.setScreenScaleImage(mScaleDegrees);
        hv.setOnDeleteClickListener(new MyHighlightView.OnDeleteClickListener() {

            @Override
            public void onDeleteClick() {
                // 移除贴纸
                processImage.removeHightlightView(hv);
                processImage.invalidate();
            }

            @Override
            public void addText(MyHighlightView myHighlightView) {
//                setStickerViewText(processImage, myHighlightView);
            }
        });

        if (paint == null) {
            /**
             * 视图可见区域
             */
            Rect visibleRect = new Rect();
            processImage.getLocalVisibleRect(visibleRect);
            /**
             * 当前 view的矩阵
             */
            Matrix mImageMatrix = processImage.getImageViewMatrix();
            int cropWidth, cropHeight;
            int x, y;
            final int width = visibleRect.width() == 0 ? mScreenWidth : visibleRect.width();
            final int height = visibleRect.height() == 0 ? CommonUtil.getScreenHeight(mContext) : visibleRect.height();
            cropWidth = (int) drawable.getCurrentWidth();
            cropHeight = (int) drawable.getCurrentHeight();
            x = visibleRect.left + (width - cropWidth) / 2;
            y = visibleRect.top + (height - cropHeight) / 2;
            Matrix matrix = new Matrix(mImageMatrix);
            matrix.invert(matrix);
            float[] pts = new float[]{x, y, x + cropWidth, y + cropHeight};
            MatrixUtils.mapPoints(matrix, pts);

            RectF cropRect = new RectF(pts[0], pts[1], pts[2], pts[3]);
            Rect imageRect = new Rect(0, 0, width, height);
            hv.setup(mContext, mImageMatrix, imageRect, cropRect, false);
            hv.setSticker(new StickerPaint(), 1);
        } else {
            if (paint.isEmpty()) {
                /**
                 * 视图可见区域
                 */
                Rect visibleRect = new Rect();
                processImage.getLocalVisibleRect(visibleRect);
                /**
                 * 当前 view的矩阵
                 */
                Matrix mImageMatrix = processImage.getImageViewMatrix();
                int cropWidth, cropHeight;
                int x, y;
                final int width = visibleRect.width();
                final int height = visibleRect.height();
                cropWidth = (int) drawable.getCurrentWidth();
                cropHeight = (int) drawable.getCurrentHeight();
                x = visibleRect.left + (width - cropWidth) / 2;
                y = visibleRect.top + (height - cropHeight) / 2;
                Matrix matrix = new Matrix(mImageMatrix);
                matrix.invert(matrix);
                float[] pts = new float[]{x, y, x + cropWidth, y + cropHeight};
                MatrixUtils.mapPoints(matrix, pts);

                RectF cropRect = new RectF(pts[0], pts[1], pts[2], pts[3]);
                Rect imageRect = new Rect(0, 0, width, height);
                hv.setup(mContext, mImageMatrix, imageRect, cropRect, false);
                hv.setSticker(paint, 1);
            } else {

                hv.setCanbeMoveDepth(!paint.isLockon());
                /**
                 * 视图可见区域
                 */
                Rect visibleRect = new Rect();

                int wid = (int) (paint.getWidth() / 2);
                int hei = (int) (paint.getHeight() / 2);
                // 获取视图本身可见的坐标区域，坐标以自己的左上角为原点（0，0）
                visibleRect.set((int) (paint.getPosX() - wid), (int) (paint.getPosY() - hei), (int) (paint.getPosX() + wid),
                        (int) (paint.getPosY() + hei));

                /**
                 * 当前 view的矩阵
                 */
                Matrix mImageMatrix = processImage.getImageViewMatrix();
                int cropWidth, cropHeight;
                int x, y;
                final int width = visibleRect.width();
                final int height = visibleRect.height();
                cropWidth = (int) drawable.getCurrentWidth();
                cropHeight = (int) drawable.getCurrentHeight();
                x = visibleRect.left;
                y = visibleRect.top;
                Matrix matrix = new Matrix(mImageMatrix);
                /**
                 * 将当前矩阵反转，并且反转后的值存入inverse中，如果当前矩阵不能反转，那么inverse不变，返回false，
                 * 反转规则应该是满足
                 * 当前矩阵*inverse=标准矩阵，标准矩阵为[1,0,0,0,1,0,0,0,1];不过其实也不用想得那么复杂，
                 * 比如当前matrix是setTranslate
                 * (10,20)，那么反转后的matrix就是setTranslate(-10,-20);
                 */
                matrix.invert(matrix);
                float[] pts;
                if (paint.getType() == 3) {
                    pts = new float[]{x, y, x + 720 / mScaleDegrees, y + 540 / mScaleDegrees};
                    hv.lockView();
                } else
                    pts = new float[]{x, y, x + cropWidth, y + cropHeight};
                MatrixUtils.mapPoints(matrix, pts);

                RectF cropRect = new RectF(pts[0], pts[1], pts[2], pts[3]);
                Rect imageRect = new Rect(0, 0, width, height);
                Log.e("cropRect:", cropRect.toString());
                hv.setup(mContext, mImageMatrix, imageRect, cropRect, false);
                hv.setSticker(paint, 1);

            }
        }

        processImage.addHighlightView(hv);
        processImage.setSelectedHighlightView(hv);
        return hv;
    }

    /**
     * 最小尺寸
     */
    public final static int StickerDrawableMINW = 120;
    public final static int StickerDrawableMINH = 120;
    public final static int StickerDrawablePADDING = 0;

    public MyHighlightView addTextSticker(MyImageViewDrawableOverlay processImage, StickerPaint paint, Bitmap bitmap, StickerFont stickerFont) {
        Log.i("TextRange", paint.getTextRange().get(0) + " , " + paint.getTextRange().get(1) + " , " + paint.getTextRange().get(2) + " , " + paint.getTextRange().get(3));

        Bitmap newBitmap;
        // 转为屏幕尺寸
        float normalwidth = bitmap.getWidth() / mScaleDegrees;
        float normalheight = bitmap.getHeight() / mScaleDegrees;
        if (!paint.isEmpty()) {
            // 支持缩放比例
            paint.setType(ConstantsPath.COMMONBUDDLESTICKER);
            paint.setWidth(normalwidth * paint.getScale());
            paint.setHeight(normalheight * paint.getScale());
            // 缩放效果在此实现（神展开）
            float posx = paint.getPosX();
            float posy = paint.getPosY();
            posx = posx / mScaleDegrees;
            posy = posy / mScaleDegrees;
            paint.setPosX(posx);
            paint.setPosY(posy);
//            (mScreenWidth <= 720 ? 350f : 320f)
            Log.i("paintS", "type: " + paint.getType() + bitmap.getWidth() + ",  " + bitmap.getHeight() + ",   " + mScaleDegrees + ",  " + paint.getWidth() + ",  " + paint.getHeight() + ",  P:" + posx + ",  PY:" + posy);
            newBitmap = Bitmap.createScaledBitmap(bitmap, (int) paint.getWidth(), (int) paint.getHeight(), true);
        } else {
            Log.i("paintS", "atype: " + paint.getType());

            newBitmap = Bitmap.createScaledBitmap(bitmap, (int) (normalwidth), (int) (normalheight), true);
        }
        return createTextSticker(processImage, paint, newBitmap, normalwidth, stickerFont);
    }


    public MyHighlightView createTextSticker(MyImageViewDrawableOverlay processImage, StickerPaint paint, Bitmap bitmap, float normalWidthBase, StickerFont stickerFont) {

        StickerDrawable drawable = new StickerDrawable(mContext.getResources(), bitmap);
        drawable.setAntiAlias(true);
        drawable.setMinSize(StickerDrawableMINW, StickerDrawableMINH);
        MyHighlightView hv = new MyHighlightView(processImage, R.style.AppTheme, drawable);// FIXME
        hv.setIsTextSticker(true);
        hv.setScreenScaleImage(mScaleDegrees);
        hv.setPadding(StickerDrawablePADDING);
        hv.setWidthBase(normalWidthBase);
        hv.setTextSize(paint.getFontSize());
        hv.setTextShadowColor(paint.getShadowcolor());
        hv.setOnDeleteClickListener(new MyHighlightView.OnDeleteClickListener() {

            @Override
            public void onDeleteClick() {
                // 移除贴纸
                processImage.removeHightlightView(hv);
                processImage.invalidate();
            }

            @Override
            public void addText(MyHighlightView myHighlightView) {
            }
        });

        if (paint.isEmpty()) {
            /**
             * 视图可见区域
             */
            Rect visibleRect = new Rect();
            processImage.getLocalVisibleRect(visibleRect);
            /**
             * 当前 view的矩阵
             */
            Matrix mImageMatrix = processImage.getImageViewMatrix();
            int cropWidth, cropHeight;
            int x, y;
            final int width = visibleRect.width();
            final int height = visibleRect.height();
            cropWidth = (int) drawable.getCurrentWidth();
            cropHeight = (int) drawable.getCurrentHeight();
            x = visibleRect.left + (width - cropWidth) / 2;
            y = visibleRect.top + (height - cropHeight) / 2;
            Matrix matrix = new Matrix(mImageMatrix);
            /**
             * 将当前矩阵反转，并且反转后的值存入inverse中，如果当前矩阵不能反转，那么inverse不变，返回false，
             * 反转规则应该是满足
             * 当前矩阵*inverse=标准矩阵，标准矩阵为[1,0,0,0,1,0,0,0,1];不过其实也不用想得那么复杂，
             * 比如当前matrix是setTranslate
             * (10,20)，那么反转后的matrix就是setTranslate(-10,-20);
             */

            matrix.invert(matrix);

            float[] pts = new float[]{x, y, x + cropWidth, y + cropHeight};
            MatrixUtils.mapPoints(matrix, pts);

            RectF cropRect = new RectF(pts[0], pts[1], pts[2], pts[3]);
            Rect imageRect = new Rect(0, 0, width, height);
            paint.setCropRect(pts);
            hv.setup(mContext, mImageMatrix, imageRect, cropRect, false);
            hv.setSticker(paint);
            if (stickerFont != null && FileDownloader.getDownloadFile("http://" + stickerFont.getUrl()) != null)
                hv.setTextType(Typeface.createFromFile(FileDownloader.getDownloadFile("http://" + stickerFont.getUrl()).getFilePath()), stickerFont.getId());
        } else {
            hv.setCanbeMoveDepth(!paint.isLockon());
            /**
             * 视图可见区域
             */
            Rect visibleRect = new Rect();

            int wid = (int) (paint.getWidth() / 2);
            int hei = (int) (paint.getHeight() / 2);
            Log.i("rectWH", wid + ",,," + hei);
            // 获取视图本身可见的坐标区域，坐标以自己的左上角为原点（0，0）
            visibleRect.set((int) (paint.getPosX() - wid), (int) (paint.getPosY() - hei), (int) (paint.getPosX() + wid),
                    (int) (paint.getPosY() + hei));
            /**
             * 当前 view的矩阵
             */
            Matrix mImageMatrix = processImage.getImageViewMatrix();
            int cropWidth, cropHeight;
            int x, y;
            final int width = visibleRect.width();
            final int height = visibleRect.height();
            cropWidth = (int) drawable.getCurrentWidth();
            cropHeight = (int) drawable.getCurrentHeight();
            x = visibleRect.left;
            y = visibleRect.top;
            Matrix matrix = new Matrix(mImageMatrix);
            matrix.invert(matrix);
            float[] pts = new float[]{x, y, x + cropWidth, y + cropHeight};
            MatrixUtils.mapPoints(matrix, pts);
            RectF cropRect = new RectF(pts[0], pts[1], pts[2], pts[3]);
            Rect imageRect = new Rect(0, 0, width, height);
            paint.setCropRect(pts);
            hv.setup(mContext, mImageMatrix, imageRect, cropRect, false);
            hv.setSticker(paint);
            if (stickerFont != null && FileDownloader.getDownloadFile("http://" + stickerFont.getUrl()) != null)
                hv.setTextType(Typeface.createFromFile(FileDownloader.getDownloadFile("http://" + stickerFont.getUrl()).getFilePath()), stickerFont.getId());
        }

        processImage.addHighlightView(hv);
        processImage.setSelectedHighlightView(hv);
        return hv;
    }
}
