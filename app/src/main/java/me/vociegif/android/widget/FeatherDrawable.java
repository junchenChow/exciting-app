package me.vociegif.android.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

public interface FeatherDrawable {
    void setMinSize(float paramFloat1, float paramFloat2);

    float getMinWidth();

    float getMinHeight();

    boolean validateSize(RectF paramRectF);

    void draw(Canvas paramCanvas);

    void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    void copyBounds(Rect paramRect);

    Rect copyBounds();

    Rect getBounds();

    void setBounds(Rect paramRect);

    int getChangingConfigurations();

    void setChangingConfigurations(int paramInt);

    void setDither(boolean paramBoolean);

    void setFilterBitmap(boolean paramBoolean);

    void setCallback(Drawable.Callback paramCallback);

    void invalidateSelf();

    void scheduleSelf(Runnable paramRunnable, long paramLong);

    void unscheduleSelf(Runnable paramRunnable);

    void setAlpha(int paramInt);

    void setColorFilter(ColorFilter paramColorFilter);

    void setColorFilter(int paramInt, PorterDuff.Mode paramMode);

    void clearColorFilter();

    boolean isStateful();

    boolean setState(int[] paramArrayOfInt);

    int[] getState();

    Drawable getCurrent();

    boolean setLevel(int paramInt);

    int getLevel();

    boolean setVisible(boolean paramBoolean1, boolean paramBoolean2);

    boolean isVisible();

    int getOpacity();

    Region getTransparentRegion();

    float getCurrentWidth();

    float getCurrentHeight();

    int getMinimumWidth();

    int getMinimumHeight();

    boolean getPadding(Rect paramRect);

    Drawable mutate();

    void draw(Canvas mCanvas, Matrix matrix, boolean reverse);

    /**
     * 翻转位图
     */
    void revearse(boolean revearse);
}