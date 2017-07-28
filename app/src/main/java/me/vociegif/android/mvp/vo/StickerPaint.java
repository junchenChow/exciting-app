package me.vociegif.android.mvp.vo;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class StickerPaint extends StickerPaintBase implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String text;
    private int fontSize;
    private int type;
    private Bitmap bitmap;
    private int index;
    private String textColor;
    private String shadowColor;
    private boolean isSelector = false;
    private int pos;
    private List<Integer> textRange;
    private int fontId;
    private float[] cropRect;

    public float[] getCropRect() {
        return cropRect;
    }

    public void setCropRect(float[] cropRect) {
        this.cropRect = cropRect;
    }

    public int getFontId() {
        return fontId;
    }

    public void setFontId(int fontId) {
        this.fontId = fontId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public List<Integer> getTextRange() {
        return textRange;
    }

    public void setTextRange(List<Integer> locs) {
        this.textRange = locs;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    // 兼容IOS
    public int getColor() {
        return hxcolorToInt(textColor);
    }

    public void setColor(int color) {
        this.textColor = colorToString(color);
    }

    private int hxcolorToInt(String string) {
        int num = Integer.valueOf(string, 16);
        num = num | 0xff000000;
        return num;
    }

    public int getShadowcolor() {
        return hxcolorToInt(shadowColor);
    }

    public void setShadowcolor(int shadowcolor) {
        this.shadowColor = colorToString(shadowcolor);
    }

    private String colorToString(int color) {
        String values = Integer.toHexString(color);
        if (values.length() == 8) {
            values = values.substring(2);
        }
        if (values.length() == 3) {
            values = values.substring(0, 1) + values.substring(0, 1) + values.substring(1, 2) + values.substring(1, 2)
                    + values.substring(2) + values.substring(2);
        }
        return values;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
    }

    public boolean isEmpty() {
        return ((getWidth() == 0) || (getHeight() == 0));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
