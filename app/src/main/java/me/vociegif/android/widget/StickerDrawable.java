package me.vociegif.android.widget;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

/**
 * 贴纸控件
 *
 * @author Administrator
 */
public class StickerDrawable extends BitmapDrawable implements FeatherDrawable {
    BlurMaskFilter mBlurFilter;
    Paint mShadowPaint;
    Paint mRectPaint;
    boolean mDrawShadow = true;
    private float minWidth = 0.0F;
    private float minHeight = 0.0F;
    private boolean revearse;

    public StickerDrawable(Resources resources, Bitmap bitmap) {
        super(resources, bitmap);
        this.mBlurFilter = new BlurMaskFilter(5.0F, BlurMaskFilter.Blur.OUTER);
        this.mRectPaint = new Paint();
        this.mRectPaint.setStyle(Paint.Style.STROKE);
        // 消除锯齿
        this.mRectPaint.setAntiAlias(true);
        // 设置画笔的颜色
        this.mRectPaint.setColor(Color.RED);
        // 设置paint的外框宽度
        this.mRectPaint.setStrokeWidth(2);
        this.mShadowPaint = new Paint(1);
        this.mShadowPaint.setMaskFilter(this.mBlurFilter);

    }

    public int getBitmapWidth() {
        return getBitmap().getWidth();
    }

    public int getBitmapHeight() {
        return getBitmap().getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    /**
     * @param canvas
     * @param reverse 是否翻转
     */
    @Override
    @Deprecated
    public void draw(Canvas canvas, Matrix matrix, boolean reverse) {
        if (reverse == false) {
            draw(canvas);
            return;
        }

        // Bitmap dstbmp = Bitmap.createBitmap(getBitmap(), 0, 0,
        // getBitmapWidth(), getBitmapHeight());
        // Canvas newCanvas=new Canvas(dstbmp);
        //// 定义矩阵对象
        // Matrix matrix1 = new Matrix(matrix);
        // matrix1.reset();
        //// 翻转
        // matrix1.postTranslate((float) (-getBitmapWidth()/2.0), 0);
        // matrix1.postScale(-1.0F, 1.0F);
        // matrix1.postTranslate((float) (getBitmapWidth()/2.0), 0);
        //// newCanvas.setMatrix(matrix1);
        // int flag=newCanvas.save();
        // newCanvas.concat(matrix1);
        // if (this.mDrawShadow) {
        // copyBounds(this.mTempRect);
        // newCanvas.drawBitmap(this.mShadowBitmap, null, this.mTempRect, null);
        // }
        // super.draw(newCanvas);
        // newCanvas.restoreToCount(flag);
        //
        // Bitmap newbmp = Bitmap.createBitmap(dstbmp, 0, 0,
        // getBitmapWidth(), getBitmapHeight(), null, false);
        // canvas.drawBitmap(newbmp, null, getBounds(), null);

    }

    /**
     * 是否删除背景
     *
     * @param value
     */
    public void setDropShadow(boolean value) {
        this.mDrawShadow = value;
        invalidateSelf();
    }

    /**
     * rect的区域是否符合条件（都大于等于做小宽高值）
     */
    @Override
    public boolean validateSize(RectF rect) {
        return (rect.width() >= this.minWidth) && (rect.height() >= this.minHeight);
    }

    @Override
    public void setMinSize(float w, float h) {
        this.minWidth = w;
        this.minHeight = h;
    }

    @Override
    public float getMinWidth() {
        return this.minWidth;
    }

    @Override
    public float getMinHeight() {
        return this.minHeight;
    }

    @Override
    public float getCurrentWidth() {
        return getIntrinsicWidth();
    }

    @Override
    public float getCurrentHeight() {
        return getIntrinsicHeight();
    }

    @Override
    public void revearse(boolean revearse) {
        this.revearse = revearse;
    }
}
