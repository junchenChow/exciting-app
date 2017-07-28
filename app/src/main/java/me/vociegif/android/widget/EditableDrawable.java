package me.vociegif.android.widget;

import android.graphics.Paint;

public interface EditableDrawable {

    void setOnSizeChangeListener(OnSizeChange paramOnSizeChange);

    void beginEdit();

    void endEdit();

    boolean isEditing();

    CharSequence getText();

    void setText(String paramString);

    void setText(CharSequence paramCharSequence);

    void setTextHint(CharSequence paramCharSequence);

    boolean isTextHint();

    void setTextHint(String paramString);

    void setBounds(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

    int getTextColor();

    void setTextColor(int paramInt);

    float getTextSize();

    float getFontMetrics(Paint.FontMetrics paramFontMetrics);

    int getTextStrokeColor();

    void setTextStrokeColor(int paramInt);

    boolean getStrokeEnabled();

    void setStrokeEnabled(boolean paramBoolean);

    int getNumLines();

    interface OnSizeChange {
        void onSizeChanged(EditableDrawable paramEditableDrawable, float paramFloat1, float paramFloat2,
                           float paramFloat3, float paramFloat4);
    }

}
