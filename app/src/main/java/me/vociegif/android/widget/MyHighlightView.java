package me.vociegif.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.buhuavoice.app.R;
import me.vociegif.android.event.EventStickerTool;
import me.vociegif.android.helper.ConstantsPath;
import me.vociegif.android.helper.StickerService;
import me.vociegif.android.helper.utils.Utils;
import me.vociegif.android.mvp.vo.StickerContentFull;
import me.vociegif.android.mvp.vo.StickerPaint;
import me.vociegif.android.widget.easing.Point2D;

import java.util.List;

import org.greenrobot.eventbus.EventBus;


/**
 * 贴纸 操作类
 *
 * @author Administrator
 */
public class MyHighlightView implements EditableDrawable.OnSizeChange {

    public static final int NONE = 0;
    public static final int ROTATE = 1;

    ;
    public static final int GROW = 2;
    public static final int MOVE = 3;
    static final String LOG_TAG = MyHighlightView.class.getName();
    /**
     * 有效点击范围
     */
    private static final float HIT_TOLERANCE = 40f;
    private static final int[] STATE_SET_NONE = new int[]{};
    private static final int[] STATE_SET_SELECTED = new int[]{android.R.attr.state_selected};
    private static final int[] STATE_SET_SELECTED_PRESSED = new int[]{android.R.attr.state_selected,
            android.R.attr.state_pressed};
    private static final int[] STATE_SET_SELECTED_FOCUSED = new int[]{android.R.attr.state_focused};
    private final RectF mTempRect = new RectF();
    private final float fpoints[] = new float[]{0, 0};
    Paint mRectPaint, textPaint;
    RectF mInvalidateRectF = new RectF();
    Rect mInvalidateRect = new Rect();
    private int STATE_NONE = 1 << 0;
    private int STATE_SELECTED = 1 << 1;
    private int STATE_FOCUSED = 1 << 2;
    private OnDeleteClickListener mDeleteClickListener;
    private boolean isLock = false;
    private boolean mHidden;
    private float textPx;
    private int textcolor = Color.BLACK;
    private int textShadowColor = Color.RED;
    private int mMode;
    private int mState = STATE_NONE;
    /**
     * 转换后的矩形
     */
    private RectF mDrawRect;
    /**
     * 标准横向矩形
     */
    private RectF mCropRect;
    /**
     * 操作按钮区域
     */
    private RectF mAuthorRect;
    /**
     * 初始矩阵
     */
    private Matrix mMatrix;
    private FeatherDrawable mContent;
    private Context context;
    private EditableDrawable mEditableContent;
    private Drawable mAnchorRotate;
    private Drawable mAnchorScale;
    /**
     * 反向
     */
    private Drawable mAnchorReverse;//翻转
    private Drawable mBackgroundDrawable;
    private Drawable mTextButton;
    private int mAnchorScaleWidth;
    private int mAnchorScaleHeight;
    private int mAnchorRotateWidth;
    private int mAnchorRotateHeight;
    private int mAnchorReverseWidth;
    private int mAnchorReverseHeight;
    private int mAnchorTextHeight = 0;
    private int mAnchorTextWidth = 0;
    private int mResizeEdgeMode;
    private boolean mRotateEnabled;
    private boolean mScaleEnabled;
    private boolean mMoveEnabled;
    /**
     * 取值范围为 { mDrawRect.centerX(), mDrawRect.centerY() }与 { mDrawRect.right,
     * mDrawRect.bottom }的角度值X与(X-360)之间
     */
    private float mRotation = 0;
    /**
     * 旋转幅度
     */
    private double mLastAngle = 0;
    /**
     * 宽高比例
     */
    private float mRatio = 1f;
    private Matrix mRotateMatrix = new Matrix();
    private int mPadding = 0;
    private float scaleImg;
    /**
     * 是否显示操作按钮
     */
    private boolean mShowAnchors = true;
    private AlignModeV mAlignVerticalMode = AlignModeV.Center;
    private ImageViewTouch mContext;
    /**
     * 原始宽度
     */
    private float mWidthBase = 0;
    private List<Integer> textlocs;
    private StickerPaint stickerPaint;
    private StickerContentFull stickerContent;
    /**
     * 贴纸文本
     */
    private String text;
    // textSrcRect为气泡贴纸文字区域，textRect是实际有效文字区域
    private RectF textSrcRect;
    private boolean Reverse = false;
    /**
     * 改编时是否可以移动层次
     */
    private boolean canmovedepth = true;
    /**
     * 透明画布
     */
    private MyImageViewDrawableOverlay imageOver;

    private boolean isTextSticker = false;

    public void setIsTextSticker(boolean isTextSticker) {
        this.isTextSticker = isTextSticker;
    }

    public boolean isTextSticker() {
        return isTextSticker;
    }

    // /**
    // * 设置能否移动
    // *
    // * @param moveable
    // */
    // public void setMoveable(boolean moveable) {
    // this.mMoveEnabled = moveable;
    // }
    //
    // /**
    // * 设置能否旋转
    // * @param mRotateEnabled
    // */
    // public void setRonateable(boolean mRotateEnabled) {
    // this.mRotateEnabled = mRotateEnabled;
    // }

    // /**
    // * 设置能否缩放
    // *
    // * @param scaleable
    // */
    // @SuppressWarnings("deprecation")
    // public void setScaleable(boolean scaleable) {
    // this.mScaleEnabled = scaleable;
    // if (scaleable) {
    // mAnchorRotate = MHApplication.getInstance().getResources()
    // .getDrawable(R.drawable.aviary_resize_knob);
    // } else {
    // mAnchorRotate = null;
    // }
    // }
    //
    // /**
    // * 设置能否删除
    // *
    // * @param deleteable
    // */
    // @SuppressWarnings("deprecation")
    // public void setDeleteable(boolean deleteable) {
    // if (deleteable) {
    // mAnchorDelete = MHApplication.getInstance().getResources()
    // .getDrawable(R.drawable.aviary_delete_knob);
    // } else {
    // mAnchorDelete = null;
    // }
    // }

    public MyHighlightView(MyImageViewDrawableOverlay imageOver, int styleId, FeatherDrawable content) {
        this.imageOver = imageOver;
        mContent = content;
        this.context = imageOver.getContext();

        this.mRectPaint = new Paint();
        this.mRectPaint.setStyle(Paint.Style.STROKE);
        // 消除锯齿
        this.mRectPaint.setAntiAlias(true);
        // 设置画笔的颜色
        this.mRectPaint.setColor(ConstantsPath.COMMONCOLOR);
        // 设置paint的外框宽度
        this.mRectPaint.setStrokeWidth(Utils.dip2px(imageOver.getContext(), ConstantsPath.DEFAULTRECTSIZE));

        this.textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setTextSize(ConstantsPath.DEFAULTINPUTTEXTSIZE);
        // 消除锯齿
        this.textPaint.setAntiAlias(true);
        // 设置画笔的颜色
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
//        this.textPaint.setShadowLayer(Utils.dip2px(imageOver.getContext(), ConstantsPath.DEFAULTSHADOWSIZE),
//                Utils.dip2px(imageOver.getContext(), ConstantsPath.DEFAULTSHADOWDXSIZE),
//                Utils.dip2px(imageOver.getContext(), ConstantsPath.DEFAULTSHADOWDXSIZE), this.textShadowColor);

        if (content instanceof EditableDrawable) {
            mEditableContent = (EditableDrawable) content;
            mEditableContent.setOnSizeChangeListener(this);
        } else {
            mEditableContent = null;
        }

        float minSize = -1f;

        Log.i(LOG_TAG, "DrawableHighlightView. styleId: " + styleId);
        mMoveEnabled = true;
        mRotateEnabled = true;
        mScaleEnabled = true;

        mAnchorScale = context.getResources().getDrawable(R.drawable.btn_paster_zoom);
        mAnchorRotate = context.getResources().getDrawable(R.drawable.btn_paster_rotate);
        mTextButton = context.getResources().getDrawable(R.drawable.btn_input_dialog);
        mAnchorReverse = context.getResources().getDrawable(R.drawable.btn_flip_horizontal);

        if (null != mAnchorScale) {
            mAnchorScaleWidth = mAnchorScale.getIntrinsicWidth() / 2;
            mAnchorScaleHeight = mAnchorScale.getIntrinsicHeight() / 2;
        }
        if (null != mAnchorRotate) {
            mAnchorRotateWidth = mAnchorRotate.getIntrinsicWidth() / 2;
            mAnchorRotateHeight = mAnchorRotate.getIntrinsicHeight() / 2;
        }
        if (null != mAnchorReverse) {
            mAnchorReverseWidth = mAnchorReverse.getIntrinsicWidth() / 2;
            mAnchorReverseHeight = mAnchorReverse.getIntrinsicHeight() / 2;
        }

        if (null != mTextButton) {
            mAnchorTextWidth = mTextButton.getIntrinsicWidth() / 2;
            mAnchorTextHeight = mTextButton.getIntrinsicHeight() / 2;
        }

        updateRatio();

        if (minSize > 0) {
            setMinSize(minSize);
        }
    }

    @Deprecated
    public void setAlignModeV(AlignModeV mode) {
        mAlignVerticalMode = mode;
    }

    /**
     * 销毁对象
     */
    public void dispose() {
        mDeleteClickListener = null;
        mContext = null;
        mContent = null;
        mEditableContent = null;
        imageOver = null;
    }

    /**
     * 复制边界
     *
     * @param outRect
     */
    public void copyBounds(RectF outRect) {
        // Log.i(LOG_TAG, LOG_TAG + " copyBounds");
        outRect.set(mDrawRect);
        outRect.inset(-mPadding, -mPadding);
    }

    /**
     * 绘制标签
     *
     * @param canvas
     */
    public void draw(final Canvas canvas) {
        // Log.i(LOG_TAG, "MyHighlightView draw");
        if (mHidden)
            return;

        copyBounds(mTempRect);

        // 关联图片处理类，变形canvas前先保存canvas现场 （Maxtrix等属性）
        final int saveCount = canvas.save();
        canvas.concat(mRotateMatrix);

        if (null != mBackgroundDrawable) {
            // Logger.e("mTempRect:" + mTempRect.toString());
            mBackgroundDrawable.setBounds((int) mTempRect.left, (int) mTempRect.top, (int) mTempRect.right,
                    (int) mTempRect.bottom);
            mBackgroundDrawable.draw(canvas);
        }

        boolean is_selected = isSelected();
        boolean is_focused = isFocused();

        // 将内容 绘制到画布里
        if (mEditableContent != null) {
            mEditableContent.setBounds(mDrawRect.left, mDrawRect.top, mDrawRect.right, mDrawRect.bottom);
        } else {
            /**
             * 设置绘制的范围
             */
            mContent.setBounds((int) mDrawRect.left, (int) mDrawRect.top, (int) mDrawRect.right,
                    (int) mDrawRect.bottom);

            // mRectPaint.setColor(Color.CYAN);
            // canvas.drawRect(mDrawRect, mRectPaint);
        }

        mContent.draw(canvas);

        if (stickerPaint.getType() == ConstantsPath.COMMONBUDDLESTICKER) {
            // 气泡贴纸
            drawText(canvas, textSrcRect);
        }

        /**
         * 恢复上次保存的画笔
         */
        canvas.restoreToCount(saveCount);

        if (stickerPaint.getType() == ConstantsPath.COMMONTEXTSTICKER) {
            // 文字气泡
            drawText(canvas, mDrawRect);
        }

        // mRectPaint.setColor(Color.BLACK);
        // canvas.drawRect(mDrawRect, mRectPaint);

        // 改编贴纸被锁定时，同样显示红色编辑框
        if ((is_selected || is_focused) || canmovedepth == false) {
            if (mShowAnchors) {
                drawAnchors(canvas);
            }
        }
    }

    private void drawAnchors(Canvas canvas) {
        // 获取经matrix映射之后的值
        RectF handleRectF = caclelateRect();
        canvas.drawRect(mAuthorRect, mRectPaint);

        // 绘制操作按钮
        final int left = (int) (handleRectF.left);
        final int right = (int) (handleRectF.right);
        final int top = (int) (handleRectF.top);
        final int bottom = (int) (handleRectF.bottom);

        //前景
        if (stickerPaint.getType() == ConstantsPath.STICKER_TYPE_FOREGROUND) {
            mAnchorScale.draw(canvas);
            return;
        }

        if (mAnchorScale != null && canmovedepth) {
            // 右下角
            mAnchorScale.setBounds(right - mAnchorScaleWidth, bottom - mAnchorScaleHeight,
                    right + mAnchorScaleWidth, bottom + mAnchorScaleHeight);
            mAnchorScale.draw(canvas);
        }

		/*// 左上角
         * setBounds(left - mAnchorDeleteWidth, top - mAnchorDeleteHeight, left + mAnchorDeleteWidth,
					top + mAnchorDeleteHeight)
		 * */

        if (mAnchorReverse != null && canmovedepth && stickerPaint.getType() != ConstantsPath.COMMONTEXTSTICKER) {
            // 左下角
            mAnchorReverse.setBounds(left - mAnchorReverseWidth, bottom - mAnchorReverseHeight,
                    left + mAnchorReverseWidth, bottom + mAnchorReverseHeight);
            mAnchorReverse.draw(canvas);
        }

        if (mTextButton != null && canmovedepth
                && (textSrcRect != null || stickerPaint.getType() == ConstantsPath.COMMONTEXTSTICKER)) {
            // 正下方
            mTextButton.setBounds(left - mAnchorTextWidth + (right - left) / 2, bottom,
                    left + mAnchorTextWidth + (right - left) / 2, bottom + 2 * mAnchorTextHeight);
            mTextButton.draw(canvas);
        }

        if (mAnchorRotate != null && stickerPaint.getType() != ConstantsPath.COMMONTEXTSTICKER) {
            // 右上角
            mAnchorRotate.setBounds(right - mAnchorRotateWidth, top - mAnchorRotateHeight, right + mAnchorRotateWidth,
                    top + mAnchorRotateHeight);
            mAnchorRotate.draw(canvas);
        }
    }

    public void drawText(Canvas canvas, RectF textrect) {
        if (textrect == null)
            return;
        if (TextUtils.isEmpty(text))
            return;
        textPaint.setTextSize(textPx);

//        textPaint.setTypeface(getTypeface());
        FontMetrics fm = textPaint.getFontMetrics();
        // 计算行高
        //float textHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2);
        float textHeight = (int) (fm.bottom - fm.top + 2);
        // int textHeight = (int) ((textrect.bottom - textrect.top - fm.bottom +
        // fm.top)
        // / 2 - fm.top);

        float rectheight = textrect.height();
        if (textHeight > rectheight) {
            return;
        }
        // 字体宽度
        float widthtext = textPaint.measureText(text);
        float rectwidth = textrect.width();
        // 行数
        float line = widthtext / rectwidth;
        float baseline = (textrect.bottom + textrect.top - fm.bottom - fm.top) / 2;
        if (line <= 1) {
            textPaint.setColor(this.textShadowColor);
            canvas.drawText(text, textrect.centerX() - 2, baseline, textPaint);
            canvas.drawText(text, textrect.centerX() + 2, baseline, textPaint);
            canvas.drawText(text, textrect.centerX(), baseline + 2, textPaint);
            canvas.drawText(text, textrect.centerX(), baseline - 2, textPaint);
            textPaint.setColor(this.textcolor);
            canvas.drawText(text, textrect.centerX(), baseline, textPaint);
        } else {
            baseline -= (textHeight/2);
            float[] widths = new float[text.length()];
            textPaint.getTextWidths(text, widths);
            int newline = formatFloatToInt(line);
            // 计算基线
            //float contentHeight = newline * textHeight;
            //if (contentHeight < rectHeight) {
            //    baseline = textRect.top + (rectHeight - contentHeight) / 2 + textHeight;
            //}
            Log.d("textrect.centerX()", "textrect.centerX():"+textrect.centerX());
            float baseWidth = 0;
            // idline为行数
            for (int index = 0, idline = 0, lastindex = 0; index < widths.length; index++) {
                // 本行宽度
                float temp = (baseWidth + widths[index]);
                float dis = rectwidth - temp;
                if (index + 1 != widths.length) {
                    if (dis >= (widths[index + 1])) {
                        baseWidth = temp;
                        continue;
                    }
                    textPaint.setColor(this.textShadowColor);
                    canvas.drawText(text, lastindex, index + 1, (textrect.centerX()) - 2, baseline + idline * textHeight, textPaint);
                    canvas.drawText(text, lastindex, index + 1, (textrect.centerX()) + 2, baseline + idline * textHeight, textPaint);
                    canvas.drawText(text, lastindex, index + 1, (textrect.centerX()), baseline + idline * textHeight + 2, textPaint);
                    canvas.drawText(text, lastindex, index + 1, (textrect.centerX()), baseline + idline * textHeight - 2, textPaint);
                    textPaint.setColor(this.textcolor);
                    canvas.drawText(text, lastindex, index + 1, (textrect.centerX()), baseline + idline * textHeight, textPaint);

                    baseWidth = 0;
                    lastindex = index + 1;
                    idline++;
                    if ((baseline + idline * textHeight) > (textrect.bottom)) {
                        break;
                    }
                    continue;
                }
                if ((baseline + idline * textHeight) > (textrect.bottom)) {
                    break;
                }
                textPaint.setColor(this.textShadowColor);
                canvas.drawText(text, lastindex, index + 1, (textrect.centerX()) - 2, baseline + idline * textHeight, textPaint);
                canvas.drawText(text, lastindex, index + 1, (textrect.centerX()) + 2, baseline + idline * textHeight, textPaint);
                canvas.drawText(text, lastindex, index + 1, (textrect.centerX()), baseline + idline * textHeight + 2, textPaint);
                canvas.drawText(text, lastindex, index + 1, (textrect.centerX()), baseline + idline * textHeight - 2, textPaint);
                textPaint.setColor(this.textcolor);
                canvas.drawText(text, lastindex, index + 1, (textrect.centerX()), baseline + idline * textHeight, textPaint);
            }
        }
    }

    /**
     * 准确计算行数
     *
     * @param line
     * @return
     */
    private int formatFloatToInt(float line) {
        if ((line - (int) line) > 0f) {
            return (int) (line + 1);
        }
        return (int) line;
    }

    /**
     * 计算边框
     *
     * @return
     */
    protected RectF computeLayout() {
        // Log.i(LOG_TAG, LOG_TAG + " computeLayout");
        // 恢复初始矩阵
        return getDisplayRect(mMatrix, mCropRect);
    }

    protected RectF computeLayoutText() {
        // Log.i(LOG_TAG, LOG_TAG + " computeLayoutText");
        // 恢复初始矩阵
        return getDisplayRect(mMatrix, mCropRect);
    }

    /**
     * 刷新编辑框区域
     */
    private RectF FreshRectGet() {
        if (mAuthorRect == null) {
            mAuthorRect = new RectF(mDrawRect);
        } else {
            mAuthorRect.set(mDrawRect);
        }
        mRotateMatrix.mapRect(mAuthorRect);
        return new RectF(mAuthorRect);
    }

    /**
     * 计算四个手柄的位置
     *
     * @return
     */
    protected RectF caclelateRect() {
        RectF newrectF = FreshRectGet();
        int paddingvalue = (stickerPaint.getType() == ConstantsPath.COMMONTEXTSTICKER) ? mAnchorTextHeight : 0;
        // 编辑按钮区域
        int btwidth = mAnchorReverseWidth;
        int btheight = mAnchorReverseHeight;

        RectF rectF = new RectF(btwidth, btheight, imageOver.getWidth() - btwidth,
                imageOver.getHeight() - btheight - paddingvalue);

        if (newrectF.left < rectF.left) {
            newrectF.left = rectF.left;
        }
        if (newrectF.right > rectF.right) {
            newrectF.right = rectF.right;
        }
        if (newrectF.bottom > rectF.bottom) {
            newrectF.bottom = rectF.bottom;
        }

        if ((newrectF.top) < rectF.top) {
            newrectF.top = rectF.top;
        }

        if (newrectF.left > (newrectF.right - btwidth - btwidth)) {
            float value = newrectF.right - btwidth - btwidth;
            if ((value - btwidth) < 0) {
                newrectF.left = btwidth;
                newrectF.right = btwidth + newrectF.left + btwidth;
            } else
                newrectF.left = (newrectF.right - btwidth - btwidth);
        }
        if (newrectF.top > (newrectF.bottom - btheight - btheight)) {
            float value = newrectF.bottom - btheight - btheight;
            if ((value - btheight) < 0) {
                newrectF.top = btheight;
                newrectF.bottom = btheight + newrectF.top + btheight;
            } else
                newrectF.top = (newrectF.bottom - btheight - btheight);
        }

        return newrectF;
    }

    /**
     * 设置是否显示编辑框
     *
     * @param value
     */
    public void showAnchors(boolean value) {
        mShowAnchors = value;
    }


    @Deprecated
    public void draw(final Canvas canvas, final Matrix source) {
        final Matrix matrix = new Matrix(source);
        matrix.invert(matrix);

        final int saveCount = canvas.save();
        canvas.concat(matrix);
        canvas.concat(mRotateMatrix);

        mContent.setBounds((int) mDrawRect.left, (int) mDrawRect.top, (int) mDrawRect.right, (int) mDrawRect.bottom);
        mContent.draw(canvas);

        canvas.restoreToCount(saveCount);
    }

    public Rect getCropRect() {
        // Log.i(LOG_TAG, LOG_TAG + " getCropRect");
        return new Rect((int) mCropRect.left, (int) mCropRect.top, (int) mCropRect.right, (int) mCropRect.bottom);
    }

    public RectF getCropRectF() {
        return mCropRect;
    }

    public RectF getTextSrcRectF() {
        return textSrcRect;
    }

    public RectF getPaddingTextSrcRectF() {
        return textSrcRect;
    }

    /**
     * @return
     */
    public Matrix getCropRotationMatrix() {
        // Log.i(LOG_TAG, LOG_TAG + " getCropRotationMatrix");
        final Matrix m = new Matrix();
        m.postTranslate(-mCropRect.centerX(), -mCropRect.centerY());
        /**
         * 先创建设置一个以0,0为原点旋转degrees度的矩阵other，然后将当前的matrix连接到other之后，
         * 并且将连接后的值写入当前matrix。
         */
        m.postRotate(mRotation);
        m.postTranslate(mCropRect.centerX(), mCropRect.centerY());

        float center_x = mCropRect.centerX();
        float center_y = mCropRect.centerY();
        // 翻转
        m.postTranslate(-center_x, 0);
        if (Reverse) {
            m.postScale(-1.0F, 1.0F);
        } else {
            m.postScale(1.0F, 1.0F);
        }
        m.postTranslate(center_x, 0);

        return m;
    }

    public RectF getDisplayRect(final Matrix m, final RectF supportRect) {
        // Log.i(LOG_TAG, LOG_TAG + " getDisplayRect");
        final RectF r = new RectF(supportRect);
        /**
         * 用matrix改变rect的4个顶点的坐标，并将改变后的坐标调整后存储到rect当中
         */
        m.mapRect(r);
        return r;
    }

    public RectF getDisplayRectText(final Matrix m, final RectF supportRect) {
        // Log.i(LOG_TAG, LOG_TAG + " getDisplayRectText");
        if (supportRect == null)
            return null;
        final RectF r = new RectF(supportRect);
        /**
         * 用matrix改变rect的4个顶点的坐标，并将改变后的坐标调整后存储到rect当中
         */
        m.mapRect(r);
        return r;
    }

    /**
     * 获取显示区域
     *
     * @return
     */
    private RectF getDisplayRectF() {
        // Log.i(LOG_TAG, LOG_TAG + " getDisplayRectF");
        final RectF r = new RectF(mDrawRect);
        mRotateMatrix.mapRect(r);
        return r;
    }

    public RectF getDrawRect() {
        // Log.i(LOG_TAG, LOG_TAG + " getDrawRect");
        return mDrawRect;
    }

    /**
     * 判断被点击的区域
     *
     * @param x
     * @param y
     * @return
     */
    public int getHit(float x, float y) {

        if (stickerPaint.getType() == ConstantsPath.STICKER_TYPE_FOREGROUND) {
            return NONE;
        }

        // RectF handleRectF = caclelateRect();
        final RectF rect = new RectF(caclelateRect());
        rect.inset(-mPadding, -mPadding);

        int retval = NONE;
        final boolean verticalCheck = (y >= (rect.top - HIT_TOLERANCE))
                && (y < (rect.bottom + ((textSrcRect != null || stickerPaint.getType() == ConstantsPath.COMMONTEXTSTICKER)
                ? 2 * mAnchorTextHeight : 0) + HIT_TOLERANCE));
        final boolean horizCheck = (x >= (rect.left - HIT_TOLERANCE)) && (x < (rect.right + HIT_TOLERANCE));
        // if horizontal and vertical checks are good then
        // at least the move edge is selected
        if (verticalCheck && horizCheck) {
            retval = MOVE;
        }

        if (mScaleEnabled &&
                (Math.abs(rect.right - x) < HIT_TOLERANCE) && (Math.abs(rect.top - y) < HIT_TOLERANCE) &&
                verticalCheck && horizCheck
                ) {
            retval = GROW;
        }

        if (mRotateEnabled && (Math.abs(rect.right - x) < HIT_TOLERANCE)
                && (Math.abs(rect.bottom - y) < HIT_TOLERANCE) && verticalCheck && horizCheck) {
            retval = ROTATE;
        }

        if (mMoveEnabled && (retval == NONE) && rect.contains((int) x, (int) y)) {
            retval = MOVE;
        }

        Log.e(LOG_TAG, "retValue: " + retval);

        return retval;
    }

    /**
     * 點擊事件確定
     *
     * @param x
     * @param y
     */
    public void onSingleTapConfirmed(float x, float y) {
        // Log.w(LOG_TAG, "MyHighlightView onSingleTapConfirmed");
        // caclelateRect();
        final RectF rect = new RectF(caclelateRect());
        rect.inset(-mPadding, -mPadding);

        final boolean verticalCheck = (y >= (rect.top - HIT_TOLERANCE))
                && (y < (rect.bottom + ((textSrcRect != null || stickerPaint.getType() == ConstantsPath.COMMONTEXTSTICKER) ? 2 * mAnchorTextHeight : 0) + HIT_TOLERANCE));
        final boolean horizCheck = (x >= (rect.left - HIT_TOLERANCE)) && (x < (rect.right + HIT_TOLERANCE));

        // //Logger.d("电击" + x + " " + y + " " + (Math.abs(rect.centerX() - x))
        // + " " + (Math.abs(rect.bottom + mAnchorTextHeight - y)) + " "
        // + verticalCheck + " " + horizCheck);

        if ((Math.abs(rect.left - x) < HIT_TOLERANCE) && (Math.abs(rect.top - y) < HIT_TOLERANCE) && verticalCheck
                && horizCheck) {
            // 左上角
            /*
            if (mDeleteClickListener != null && isLock == false) {
				mDeleteClickListener.onDeleteClick();
			}*/
        } else if ((Math.abs(rect.right - x) < HIT_TOLERANCE) && (Math.abs(rect.top - y) < HIT_TOLERANCE)
                && verticalCheck && horizCheck) {
            //右上角
            /*if (mAnchorLock != null && canmovedepth) {
                if (isLock) {
					unlockView();
				} else {
					lockView();
				}
			}*/
        } else if ((Math.abs(rect.left - x) < HIT_TOLERANCE) && (Math.abs(rect.bottom - y) < HIT_TOLERANCE)
                && verticalCheck && horizCheck) {
            //左下角
            if (mAnchorReverse != null && stickerPaint.getType() != ConstantsPath.COMMONTEXTSTICKER) {
                if (this.isLock) {
                    return;
                }
                ReverseView();
            }
        } else if ((Math.abs(rect.centerX() - x) < HIT_TOLERANCE)
                && (Math.abs(rect.bottom + mAnchorTextHeight - y) < HIT_TOLERANCE) && verticalCheck && horizCheck) {
            //中下
            if (mTextButton != null
                    && (mTempRect != null || stickerPaint.getType() == ConstantsPath.COMMONTEXTSTICKER)) {
                mDeleteClickListener.addText(this);
            }
        }
    }

    /**
     * 翻转界面
     */
    public void ReverseView() {
        Reverse = !Reverse;
        invalidate();
    }

    public void stickerdelete() {
        if (mDeleteClickListener != null && isLock == false) {
            mDeleteClickListener.onDeleteClick();
        }
    }

    public void stickerlock() {
        if (isLock) {
            unlockView();
        } else {
            lockView();
        }
        if (imageOver != null)
            imageOver.invalidate();
    }

    public void lockView() {
        this.isLock = true;
        //mAnchorLock = MHApplication.getInstance().getResources().getDrawable(R.drawable.btn_locked);
        // 改成浅一点的红啦
        this.mRectPaint.setColor(context.getResources().getColor(R.color.red_light_f0625e));

        // this.mRectPaint.setColor(Color.RED);
    }

    public void unlockView() {
        this.isLock = false;
        //mAnchorLock = MHApplication.getInstance().getResources().getDrawable(R.drawable.btn_unlock);
        this.mRectPaint.setColor(ConstantsPath.COMMONCOLOR);
    }

    public Rect getInvalidationRect() {
        // Log.i(LOG_TAG, "getInvalidationRect");
        mInvalidateRectF.set(mDrawRect);
        mInvalidateRectF.inset(-mPadding, -mPadding);
        mRotateMatrix.mapRect(mInvalidateRectF);

        mInvalidateRect.set((int) mInvalidateRectF.left, (int) mInvalidateRectF.top, (int) mInvalidateRectF.right,
                (int) mInvalidateRectF.bottom);

        int w = mAnchorRotateWidth;
        int h = mAnchorRotateHeight;

        mInvalidateRect.inset(-w * 2, -h * 2);
        return mInvalidateRect;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(final int mode) {
        // Log.i(LOG_TAG, "setMode: " + mode);
        if (mode != mMode) {
            if (isLock) {
                mMode = MyHighlightView.NONE;
                return;
            }
            mMode = mode;
            updateDrawableState();
        }
    }

    public Matrix getRotationMatrix() {
        return mRotateMatrix;
    }

    protected void growBy(final float dx) {
        // Log.i(LOG_TAG, "growBy dx");
        growBy(dx, dx / mRatio, true);
    }

    protected void growBy(final float dx, final float dy, boolean checkMinSize) {
        // Log.i(LOG_TAG, "growBy dx dy checkMinSize");
        if (!mScaleEnabled)
            return;

        final RectF r = new RectF(mCropRect);

        if (mAlignVerticalMode == AlignModeV.Center) {
            r.inset(-dx, -dy);
        } else if (mAlignVerticalMode == AlignModeV.Top) {
            r.inset(-dx, 0);
            r.bottom += dy * 2;
        } else {
            r.inset(-dx, 0);
            r.top -= dy * 2;
        }

        RectF testRect = getDisplayRect(mMatrix, r);

        if (!mContent.validateSize(testRect) && checkMinSize) {
            return;
        }

        mCropRect.set(r);
        invalidate();
    }

    /**
     * 发生移动事件
     *
     * @param edge
     * @param event2
     * @param dx
     * @param dy
     */
    public void onMouseMove(int edge, MotionEvent event2, float dx, float dy) {
        //Log.e(LOG_TAG, "onMouseMove mode:" + edge);
        if (stickerPaint.getType() == ConstantsPath.STICKER_TYPE_FOREGROUND)
            return;
        if (isLock)
            return;
        if (edge == NONE)
            return;

        fpoints[0] = dx;
        fpoints[1] = dy;

        float xDelta;
        float yDelta;

        switch (edge) {
            case NONE:
            case MOVE: {
                moveBy(dx * (mCropRect.width() / mDrawRect.width()), dy * (mCropRect.height() / mDrawRect.height()));
            }
            break;
            case ROTATE: {
                dx = fpoints[0];
                dy = fpoints[1];
                // xDelta = dx ;
                // yDelta = dy;
                xDelta = dx * (mCropRect.width() / mDrawRect.width());
                yDelta = dy * (mCropRect.height() / mDrawRect.height());
                //rotateBy(event2.getX(), event2.getY(), dx, dy);
                scaleBy(event2.getX(), event2.getY(), dx, dy);
                Log.i("xxx", "" + dx + "____" + dy + "____" + event2.getX() + "___" + event2.getY());

                //setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics()));
                invalidate();
            }
            break;
            case GROW: {
                // 转动
                // Log.i(LOG_TAG, "onMouseMove ROTATE");
                dx = fpoints[0];
                dy = fpoints[1];
                // xDelta = dx ;
                // yDelta = dy;
                xDelta = dx * (mCropRect.width() / mDrawRect.width());
                yDelta = dy * (mCropRect.height() / mDrawRect.height());
                rotateBy(event2.getX(), event2.getY(), dx, dy);

                invalidate();
            }
            break;
        }
    }

    void onMove(float dx, float dy) {
        // Log.i(LOG_TAG, LOG_TAG + " onMove dx dy");
        moveBy(dx * (mCropRect.width() / mDrawRect.width()), dy * (mCropRect.height() / mDrawRect.height()));
    }

    /**
     * 刷新绘图区域（改变的部分：rect和矩阵）
     */
    public void invalidate() {
        // Log.i(LOG_TAG, LOG_TAG + " invalidate");
        mDrawRect = computeLayout(); // true
        // caclelateRect();
        if (textSrcRect != null)
            freshTextRect();

        if (mDrawRect != null && mDrawRect.left > 1200) {
            Log.e(LOG_TAG, "computeLayout: " + mDrawRect);
        }
        float center_x = mDrawRect.centerX();
        float center_y = mDrawRect.centerY();
        /**
         * 重置一个matrix对象
         */
        mRotateMatrix.reset();
        mRotateMatrix.postTranslate(-mDrawRect.centerX(), -mDrawRect.centerY());
        mRotateMatrix.postRotate(mRotation);
        mRotateMatrix.postTranslate(mDrawRect.centerX(), mDrawRect.centerY());

        // 翻转
        mRotateMatrix.postTranslate(-center_x, 0);
        if (Reverse) {
            mRotateMatrix.postScale(-1.0F, 1.0F);
        } else {
            mRotateMatrix.postScale(1.0F, 1.0F);
        }
        mRotateMatrix.postTranslate(center_x, 0);
    }

    /**
     * 贴纸 移动
     *
     * @param dx
     * @param dy
     */
    void moveBy(final float dx, final float dy) {
        Log.i(LOG_TAG, LOG_TAG + " moveBy dx dy");
        if (mMoveEnabled) {
            /**
             * 给坐标一个补偿值，值可以使正的也可以是负的。
             */
            mCropRect.offset(dx, dy);
            invalidate();
        }
    }

    /**
     * 转动
     *
     * @param dx    点击事件横坐标
     * @param dy    点击事件纵坐标
     * @param diffx
     * @param diffy
     */

    void rotateBy(final float dx, final float dy, float diffx, float diffy) {
        // Log.i(LOG_TAG, "rotateBy");
        if (!mRotateEnabled)
            return;

        if (stickerPaint.getType() == ConstantsPath.COMMONTEXTSTICKER) {
            return;
        }

        final float pt1[] = new float[]{mDrawRect.centerX(), mDrawRect.centerY()};
        final float pt2[] = new float[]{mDrawRect.right, mDrawRect.top};
        final float pt3[] = new float[]{dx, dy};

        // 左上角与中心点的初始角度（固定值）
        final double angle1 = Point2D.angleBetweenPoints(pt2, pt1);
        // 侧滑角度
        final double angle2 = Point2D.angleBetweenPoints(pt3, pt1);
        final float newRotation1 = -(float) (angle2 - angle1);
        if (mRotateEnabled) {
            double value = (angle2 - angle1) - this.mLastAngle;

            if (!Reverse)
                value = -value;

            mRotation = (float) (value + mRotation) % 360;
        }
        this.mLastAngle = angle2 - angle1;
        //Log.e(LOG_TAG, "this.mRotation:"+mRotation);
    }

    public void scaleBy(final float dx, final float dy, float diffx, float diffy) {
        if (!mScaleEnabled)
            return;

        if (stickerPaint.getType() == ConstantsPath.COMMONTEXTSTICKER || stickerPaint.getType() == ConstantsPath.STICKER_TYPE_FOREGROUND) {
            if (mScaleEnabled)
                growBy(diffx, diffy, true);
            return;
        }

        final float pt1[] = new float[]{mDrawRect.centerX(), mDrawRect.centerY()};
        final float pt2[] = new float[]{mDrawRect.right, mDrawRect.bottom};
        final float pt3[] = new float[]{dx, dy};

        final Matrix rotateMatrix = new Matrix();
        //rotateMatrix.postRotate(-newRotation1);
        // rotateMatrix.postRotate(-mRotation);

        final float points[] = new float[]{diffx, diffy};
        rotateMatrix.mapPoints(points);

        diffx = points[0];
        diffy = points[1];

        final float xDelta = diffx * (mCropRect.width() / mDrawRect.width());
        final float yDelta = diffy * (mCropRect.height() / mDrawRect.height());

        final float pt4[] = new float[]{mDrawRect.right + xDelta, mDrawRect.bottom + yDelta};
        final double distance1 = Point2D.distance(pt1, pt2);
        final double distance2 = Point2D.distance(pt1, pt4);
        final float distance = (float) (distance2 - distance1);
        growBy(distance);
    }

    @Deprecated
    void onRotateAndGrow(double angle, float scaleFactor) {
        // Log.i(LOG_TAG, "onRotateAndGrow");
        if (!mRotateEnabled)
            mRotation -= (float) (angle);

        if (mRotateEnabled) {
            mRotation -= (float) (angle);
            growBy(scaleFactor * (mCropRect.width() / mDrawRect.width()));
        }

        invalidate();
    }

    public void setHidden(final boolean hidden) {
        mHidden = hidden;
    }

    public void setMinSize(final float size) {
        // Log.i(LOG_TAG, "setMinSize");
        if (mRatio >= 1) {
            mContent.setMinSize(size, size / mRatio);
        } else {
            mContent.setMinSize(size * mRatio, size);
        }
    }

    public boolean isPressed() {
        return isSelected() && mMode != NONE;
    }

    /**
     * 更新状态
     */
    protected void updateDrawableState() {
        // Log.i(LOG_TAG, "updateDrawableState " + (mBackgroundDrawable ==
        // null));
        if (null == mBackgroundDrawable)
            return;

        boolean is_selected = isSelected();
        boolean is_focused = isFocused();
        Log.i("isSelect", is_focused + "");
        if (is_selected) {
            if (mMode == NONE) {
                if (is_focused) {
                    // 为不同的状态存取不同的Drawable，通过指定状态的id值，可以取得如获得焦点，失去焦点等时的不同图像
                    mBackgroundDrawable.setState(STATE_SET_SELECTED_FOCUSED);
                } else {
                    mBackgroundDrawable.setState(STATE_SET_SELECTED);
                }

            } else {
                mBackgroundDrawable.setState(STATE_SET_SELECTED_PRESSED);
            }

        } else {
            // normal state
            mBackgroundDrawable.setState(STATE_SET_NONE);
        }
    }

    public void setOnDeleteClickListener(final OnDeleteClickListener listener) {
        mDeleteClickListener = listener;
    }

    public boolean isSelected() {
        return (mState & STATE_SELECTED) == STATE_SELECTED;
    }

    public void setSelected(boolean selected) {
        // Log.e(LOG_TAG, "setSelected: " + selected);
        EventBus.getDefault().post(new EventStickerTool(selected));
        boolean is_selected = isSelected();
        // 选择状态有变化
        if (is_selected != selected) {
            mState ^= STATE_SELECTED;
            updateDrawableState();
        }
    }

    public boolean isFocused() {
        return (mState & STATE_FOCUSED) == STATE_FOCUSED;
    }

    public void setFocused(final boolean value) {
        // Log.e(LOG_TAG, "setFocused: " + value);
        boolean is_focused = isFocused();
        if (is_focused != value) {
            mState ^= STATE_FOCUSED;

            if (null != mEditableContent) {
                if (value) {
                    mEditableContent.beginEdit();
                } else {
                    mEditableContent.endEdit();
                }
            }
            updateDrawableState();
        }
    }

    public void setup(final Context context, final Matrix m, final Rect imageRect, final RectF cropRect,
                      final boolean maintainAspectRatio) {
        mMatrix = new Matrix(m);
        mRotation = 0;
        mRotateMatrix = new Matrix();
        mCropRect = cropRect;
        setMode(NONE);
        invalidate();
    }

    @Deprecated
    public void setup(final Context context, final Matrix m, final Rect imageRect, final RectF cropRect,
                      final boolean maintainAspectRatio, final float rotation) {
        mMatrix = new Matrix(m);
        mRotation = rotation;
        mRotateMatrix = new Matrix();
        mCropRect = cropRect;
        setMode(NONE);
        invalidate();
    }

    @Deprecated
    public void update(final Matrix imageMatrix, final Rect imageRect) {
        setMode(NONE);
        mMatrix = new Matrix(imageMatrix);
        mRotation = 0;
        mRotateMatrix = new Matrix();
        invalidate();
    }

    /**
     * 设置文字贴纸范围
     *
     * @param textRect
     */
    public void setTextRect(RectF textRect) {
        this.textSrcRect = textRect;
    }

    public FeatherDrawable getContent() {
        return mContent;
    }

    private void updateRatio() {
        // Log.i(LOG_TAG, "updateRatio");
        final float w = mContent.getCurrentWidth();
        final float h = mContent.getCurrentHeight();
        mRatio = w / h;
    }

    /**
     * 强制刷新
     *
     * @return
     */
    public boolean forceUpdate() {
        // Log.i(LOG_TAG, "forceUpdate");

        RectF cropRect = getCropRectF();
        RectF drawRect = getDrawRect();

        if (mEditableContent != null) {

            final float textWidth = mContent.getCurrentWidth();
            final float textHeight = mContent.getCurrentHeight();

            updateRatio();

            RectF tempRect = new RectF(cropRect);
            getMatrix().mapRect(tempRect);

            float dx = textWidth - tempRect.width();
            float dy = textHeight - tempRect.height();

            float[] fpoints = new float[]{dx, dy};

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(-mRotation);

            dx = fpoints[0];
            dy = fpoints[1];

            float xDelta = dx * (cropRect.width() / drawRect.width());
            float yDelta = dy * (cropRect.height() / drawRect.height());

            if (xDelta != 0 || yDelta != 0) {
                growBy(xDelta / 2, yDelta / 2, false);
            }

            invalidate();
            return true;
        }
        return false;
    }

    /**
     * 设置内容填充
     *
     * @param value
     */
    @Deprecated
    public void setPadding(int value) {
        mPadding = value;
    }

    @Override
    public void onSizeChanged(EditableDrawable content, float left, float top, float right, float bottom) {
        // Log.i(LOG_TAG, "onSizeChanged: " + left + ", " + top + ", " + right
        // + ", " + bottom);
        // 大小发生变化
        if (content.equals(mEditableContent) && null != mContext) {
            if (mDrawRect.left != left || mDrawRect.top != top || mDrawRect.right != right
                    || mDrawRect.bottom != bottom) {
                if (forceUpdate()) {
                    mContext.invalidate(getInvalidationRect());
                } else {
                    mContext.postInvalidate();
                }
            }
        }
    }

    /**
     * 画边框
     */
    @Deprecated
    public void addLine() {
        if (isLock) {

        } else {

        }
    }

    public StickerContentFull getStickerParam() {
        return this.stickerContent;
    }

    public StickerPaint getStickerPaint() {
        return this.stickerPaint;
    }


    /**
     * 获取贴纸
     *
     * @return
     */
    @Deprecated
    public StickerPaint getSticker() {
        StickerPaint stickerPaint = new StickerPaint();
        stickerPaint.setLockon(isLock);
        stickerPaint.setRotate(getRadian());
        stickerPaint.setScale(getScale());
        stickerPaint.setStickerid(this.stickerPaint.getStickerid());
        stickerPaint.setSeriesid(this.stickerPaint.getSeriesid());
        stickerPaint.setType(this.stickerPaint.getType());
        stickerPaint.setPosX((int) this.mCropRect.centerX());
        stickerPaint.setPosY((int) this.mCropRect.centerY());
        stickerPaint.setText(text);
        stickerPaint.setWidth(this.mCropRect.width());
        stickerPaint.setHeight(this.mCropRect.height());
        stickerPaint.setFontId(getFontId());
        return stickerPaint;
    }

    public void setSticker(StickerPaint paint) {
        this.stickerPaint = paint;
        stickerContent = StickerService.getStickerById(paint.getStickerid(), paint.getSeriesid());

        if (stickerPaint.getType() == ConstantsPath.COMMONBUDDLESTICKER) {
            setTextSize(paint.getFontSize());
            setTextColor(paint.getColor());

            List<Integer> locs = paint.getTextRange();
            if (locs != null) {
                // 获取文字区域
                setRange(locs);
                freshTextRect();
                setText(paint.getText());
            }
        }
        setRadian(paint.getRotate());
        if (paint.isFlip())
            ReverseView();
        if (paint.isLockon())
            lockView();
    }

    public void setSticker(StickerPaint paint, int rotate) {
        this.stickerPaint = paint;
        stickerContent = StickerService.getStickerById(paint.getStickerid(), paint.getSeriesid());

        if (stickerPaint.getType() == ConstantsPath.COMMONBUDDLESTICKER) {
            setTextSize(paint.getFontSize());
            setTextColor(paint.getColor());

            List<Integer> locs = paint.getTextRange();
            if (locs != null) {
                // 获取文字区域
                setRange(locs);
                freshTextRect();
                setText(paint.getText());
            }
        }
        if (rotate == 0)
            setRadian(paint.getRotate());
        else
            setRotation(paint.getRotate());
        if (paint.isFlip())
            ReverseView();
        if (paint.isLockon())
            lockView();
    }

    /**
     * 获取以弧度为单位的角度
     *
     * @return
     */
    public float getRadian() {
        return (float) (((this.mRotation) % 360) / 180.0 * Math.PI);
    }

    public void setRadian(float radian) {
        double ra = radian / Math.PI * 180;
        setRotation((float) (ra));
    }


    public float getRotation() {
        return mRotation;
    }

    /**
     * 设置旋转角度
     *
     * @param rotation
     */
    public void setRotation(float rotation) {
        this.mRotation = rotation;
        Log.i("mRotation", mRotation+"");
        invalidate();
    }

    public StickerPaint getSticker(int leftpadding, int toppadding, float scaleImg) {
        StickerPaint stickerPaint = new StickerPaint();
        stickerPaint.setLockon(isLock);
        stickerPaint.setRotate(getRadian());
        stickerPaint.setScale(getScale());
        stickerPaint.setStickerid(this.stickerPaint.getStickerid());
        stickerPaint.setSeriesid(this.stickerPaint.getSeriesid());
        stickerPaint.setType(this.stickerPaint.getType());
        // 加进误差，并转为720坐标系
        stickerPaint.setPosX((this.mCropRect.centerX() + leftpadding) * scaleImg);
        stickerPaint.setPosY((this.mCropRect.centerY() + toppadding) * scaleImg);
        stickerPaint.setFlip(Reverse);
        stickerPaint.setText(text);
        stickerPaint.setWidth(this.mCropRect.width() * scaleImg);
        stickerPaint.setHeight(this.mCropRect.height() * scaleImg);

        stickerPaint.setColor(textcolor);
        stickerPaint.setShadowcolor(textShadowColor);
        stickerPaint.setFontSize(Utils.px2dip(context, textPx));
        stickerPaint.setFontId(getFontId());
        return stickerPaint;
    }

    public StickerPaint getSticker(float scaleImg) {
        StickerPaint stickerPaint = new StickerPaint();
        stickerPaint.setLockon(isLock);
        stickerPaint.setRotate(getRadian());
        stickerPaint.setScale(getScale());
        stickerPaint.setStickerid(this.stickerPaint.getStickerid());
        stickerPaint.setSeriesid(this.stickerPaint.getSeriesid());
        stickerPaint.setType(this.stickerPaint.getType());
        stickerPaint.setPos(this.stickerPaint.getPos());
        // 加进误差，并转为720坐标系
        float centerx = this.mCropRect.centerX();
        float centery = this.mCropRect.centerY();
        stickerPaint.setPosX((centerx) * scaleImg);
        stickerPaint.setPosY((centery) * scaleImg);
        stickerPaint.setFlip(Reverse);
        stickerPaint.setText(text);
        stickerPaint.setWidth(this.mCropRect.width() * scaleImg);
        stickerPaint.setHeight(this.mCropRect.height() * scaleImg);
        stickerPaint.setColor(textcolor);
        stickerPaint.setShadowcolor(textShadowColor);
        stickerPaint.setFontSize(Utils.px2dip(context, textPx));
        stickerPaint.setFontId(getFontId());
        return stickerPaint;
    }


    /**
     * 获取缩放比例
     *
     * @return
     */
    public float getScale() {
        return this.mCropRect.width() / this.mWidthBase;
    }

    /**
     * 是否被锁定
     *
     * @return
     */
    public boolean isLock() {
        return this.isLock;
    }

    /**
     * 是否为气泡贴纸
     *
     * @return
     */
    public boolean isBubbleSticker() {
        return (stickerPaint.getType() == ConstantsPath.COMMONBUDDLESTICKER);
    }

    public int getTextColor() {
        return this.textcolor;
    }

    public void setTextColor(int color) {
        this.textcolor = color;
        this.textPaint.setColor(color);
    }

    public void setTextType(Typeface textType, int id) {
        this.textPaint.setTypeface(textType);
        this.stickerPaint.setFontId(id);
    }


    public int getFontId() {
        return this.stickerPaint.getFontId();
    }

    public void setTextShadowColor(int color) {
        this.textShadowColor = color;
        this.textPaint.setShadowLayer(5, 5, 5, color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    /**
     * 重置角度
     */
    public void resetRonate() {
        this.mLastAngle = 0;
    }

    /**
     * 设置正常宽度，用于计算缩放比例
     *
     * @param normalWidthBase
     */
    public void setWidthBase(float normalWidthBase) {
        this.mWidthBase = normalWidthBase;
    }

    public void setRange(List<Integer> textlocs) {
        this.textlocs = textlocs;
    }

    /**
     * 刷新文本区域
     */
    public void freshTextRect() {
        if (textlocs == null)
            return;
        float scale = getScale();
        float tx = ((float) (textlocs.get(0)) / scaleImg * scale);
        float ty = ((float) (textlocs.get(1)) / scaleImg * scale);
        float twidth = ((float) (textlocs.get(2)) / scaleImg * scale);
        float theight = ((float) (textlocs.get(3)) / scaleImg * scale);
        RectF textRect = new RectF(mCropRect.left + tx, mCropRect.top + ty, mCropRect.left + tx + twidth,
                mCropRect.top + ty + theight);
        setTextRect(textRect);
    }

    public boolean isReverse() {
        return Reverse;
    }

    public void setCanbeMoveDepth(boolean canmovedepth) {
        this.canmovedepth = canmovedepth;
    }

    public boolean CanBeMoveDepth() {
        return this.canmovedepth;
    }

    public float getScreenScaleImage() {
        return this.scaleImg;
    }

    public void setScreenScaleImage(float scaleImg) {
        this.scaleImg = scaleImg;
    }

    public float getTextSize() {
        return textPx;
    }

    public void setTextSize(float textsize) {
        this.textPx = Utils.dip2px(context, textsize);
        this.textPaint.setTextSize(textsize);
    }

    public static enum AlignModeV {
        Top, Bottom, Center
    }

    public interface OnDeleteClickListener {
        void onDeleteClick();

        void addText(MyHighlightView myHighlightView);

    }

}
