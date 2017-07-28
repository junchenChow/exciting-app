package me.vociegif.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.vociegif.android.event.EventFocusCancel;
import me.vociegif.android.event.EventIsStickerPage;
import me.vociegif.android.mvp.vo.StickerPaint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 标签编辑底图
 *
 * @author Administrator
 */
public class MyImageViewDrawableOverlay extends ImageViewTouch {


    public final int CURSOR_BLINK_TIME = 400;

    boolean mScrollStarted;

    ;
    /**
     * 当前开始的一系列连续动作的类型：1默认值，2选中，3滑动
     */
    // int motionType = 1;
    float mLastMotionScrollX, mLastMotionScrollY;
    // 删除的时候会出错
    private List<MyHighlightView> mOverlayViews = new CopyOnWriteArrayList<MyHighlightView>();
    private MyHighlightView mOverlayView, tempView;
    /**
     * 主界面回调
     */
    private OnDrawableEventListener mDrawableListener;
    private boolean mForceSingleSelection = true;
    private Paint mDropPaint;
    private Rect mTempRect = new Rect();
    private boolean mScaleWithContent = false;
    /**
     * 需要重新绘制每个贴纸的编辑框
     */
    private boolean needResetRect = false;
    private boolean returnvalue = false, disreturn = true;

    public MyImageViewDrawableOverlay(Context context) {
        super(context);
    }

    public MyImageViewDrawableOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageViewDrawableOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void panBy(double dx, double dy) {
        RectF rect = getBitmapRect();
        mScrollRect.set((float) dx, (float) dy, 0, 0);
        updateRect(rect, mScrollRect);
        // FIXME 贴纸移动到边缘次数多了以后会爆,原因不明朗 。后续需要好好重写ImageViewTouch
        // postTranslate(mScrollRect.left, mScrollRect.top);
        center(true, true);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledDoubleTapSlop();
        mGestureDetector.setIsLongpressEnabled(false);
        // mDropPaint=new Paint();
        // mDropPaint.setColor(Color.GRAY);
        // mDropPaint.setStyle(Style.FILL);
        EventBus.getDefault().register(this);
    }


    public void unRegisterEvent() {
        EventBus.getDefault().unregister(this);
    }

    public boolean getScaleWithContent() {
        return mScaleWithContent;
    }

    /**
     * How overlay content will be scaled/moved when zomming/panning the base
     * image
     *
     * @param value true if content will scale according to the image
     */
    public void setScaleWithContent(boolean value) {
        mScaleWithContent = value;
    }

    /**
     * If true, when the user tap outside the drawable overlay and there is only
     * one active overlay selection is not changed.
     *
     * @param value the new force single selection
     */
    public void setForceSingleSelection(boolean value) {
        mForceSingleSelection = value;
    }

    /**
     * 设置繪圖监听器
     *
     * @param listener
     */
    public void setOnDrawableEventListener(OnDrawableEventListener listener) {
        mDrawableListener = listener;
    }

    public List<MyHighlightView> getmOverlayViews() {
        return mOverlayViews;
    }


    @Override
    public void setImageDrawable(android.graphics.drawable.Drawable drawable, Matrix initial_matrix, float min_zoom,
                                 float max_zoom) {
        super.setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
    }

    @Override
    protected void onLayoutChanged(int left, int top, int right, int bottom) {
        super.onLayoutChanged(left, top, right, bottom);

        if (getDrawable() != null) {

            Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
            while (iterator.hasNext()) {
                MyHighlightView view = iterator.next();
                view.getMatrix().set(getImageMatrix());
                view.invalidate();
            }
        }
    }

    @Override
    public void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);

        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            if (getScale() != 1) {
                float[] mvalues = new float[9];
                getImageMatrix().getValues(mvalues);
                final float scale = mvalues[Matrix.MSCALE_X];

                if (!mScaleWithContent)
                    view.getCropRectF().offset(-deltaX / scale, -deltaY / scale);
            }

            view.getMatrix().set(getImageMatrix());
            view.invalidate();
        }
    }

    @Override
    protected void postScale(float scale, float centerX, float centerY) {
        if (mOverlayViews.size() > 0) {
            Iterator<MyHighlightView> iterator = mOverlayViews.iterator();

            Matrix oldMatrix = new Matrix(getImageViewMatrix());
            super.postScale(scale, centerX, centerY);

            while (iterator.hasNext()) {
                MyHighlightView view = iterator.next();
                if (view.isSelected() == false || view.isLock()) {
                    continue;
                }
                if (!mScaleWithContent) {
                    RectF cropRect = view.getCropRectF();
                    RectF rect1 = view.getDisplayRect(oldMatrix, view.getCropRectF());
                    RectF rect2 = view.getDisplayRect(getImageViewMatrix(), view.getCropRectF());

                    float[] mvalues = new float[9];
                    getImageViewMatrix().getValues(mvalues);
                    final float currentScale = mvalues[Matrix.MSCALE_X];

                    cropRect.offset((rect1.left - rect2.left) / currentScale, (rect1.top - rect2.top) / currentScale);
                    cropRect.right += -(rect2.width() - rect1.width()) / currentScale;
                    cropRect.bottom += -(rect2.height() - rect1.height()) / currentScale;

                    view.getMatrix().set(getImageMatrix());
                    view.getCropRectF().set(cropRect);
                } else {
                    view.getMatrix().set(getImageMatrix());
                }
                view.invalidate();
            }
        } else {
            super.postScale(scale, centerX, centerY);
        }
    }

    private void ensureVisible(MyHighlightView hv, float deltaX, float deltaY) {
        RectF r = hv.getDrawRect();
        int panDeltaX1 = 0, panDeltaX2 = 0;
        int panDeltaY1 = 0, panDeltaY2 = 0;

        if (deltaX > 0)
            panDeltaX1 = (int) Math.max(0, getLeft() - r.left);
        if (deltaX < 0)
            panDeltaX2 = (int) Math.min(0, getRight() - r.right);

        if (deltaY > 0)
            panDeltaY1 = (int) Math.max(0, getTop() - r.top);

        if (deltaY < 0)
            panDeltaY2 = (int) Math.min(0, getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //// Logger.d(LOG_TAG + " onTouchEvent " + returnvalue + " " + disreturn
        // + " " + TouchEventUtil.getTouchEvent(event));
        return disreturn;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 事件开始
        disreturn = false;

        returnvalue = super.dispatchTouchEvent(event);
        // //Logger.d(LOG_TAG + " dispatchTouchEvent " + returnvalue + " "
        // + TouchEventUtil.getTouchEvent(event));
        return returnvalue;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // //Logger.d(LOG_TAG + " onSingleTapConfirmed ");

        // iterate the items and post a single tap event to the selected item
        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            if (view.isSelected()) {
                view.onSingleTapConfirmed(e.getX(), e.getY());
                postInvalidate();
            }
        }
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.i(LOG_TAG, LOG_TAG + " onDoubleTap ");
        if (mOverlayView != null) {
            int edge = mOverlayView.getHit(e.getX(), e.getY());
            if ((edge & MyHighlightView.MOVE) == MyHighlightView.MOVE) {
                if (mDrawableListener != null) {
                    // 双击事件
                    mDrawableListener.onDoubleClick(mOverlayView);
                }
                return true;
            }

            mOverlayView.setMode(MyHighlightView.NONE);
            postInvalidate();

            // log.d(LOG_TAG, "selected items: " + mOverlayViews.size());

            if (mOverlayViews.size() != 1) {
                setSelectedHighlightView(null);
            }
        }
        return super.onDoubleTap(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Log.i(LOG_TAG, LOG_TAG + " onDown ");

        mScrollStarted = false;
        mLastMotionScrollX = e.getX();
        mLastMotionScrollY = e.getY();

        // return the item being clicked
        MyHighlightView newSelection = checkSelection(e);

        if (mDrawableListener != null) {
            tempView = newSelection;
            mDrawableListener.onDown(newSelection);
        }

        if (newSelection != null && newSelection.isSelected()) {
            setCheckSelect(e, newSelection);
        } else if (newSelection == null) {
            setCheckSelect(e, null);
        }

        return super.onDown(e);
    }

    private void setCheckSelect(MotionEvent e, MyHighlightView newSelection) {

        setSelectedHighlightView(newSelection);

        if (mOverlayView != null) {
            // 通过触摸区域得到Mode
            int edge = mOverlayView.getHit(e.getX(), e.getY());
            if (edge != MyHighlightView.NONE) {
                disreturn = true;
                mOverlayView.setMode((edge == MyHighlightView.MOVE) ? MyHighlightView.MOVE
                        : (edge == MyHighlightView.ROTATE ? MyHighlightView.ROTATE : MyHighlightView.GROW));
                postInvalidate();
                if (mDrawableListener != null) {
                    // mDrawableListener.onDown(mOverlayView);
                }
            }
        }
    }

    @Override
    public boolean onUp(MotionEvent e) {
        // Log.i(LOG_TAG, LOG_TAG + " onUp");

        // if (motionType != 4) {
        // MyHighlightView newSelection = checkSelection(e);
        // setCheckSelect(e, newSelection);
        // }

        if (mOverlayView != null) {
            mOverlayView.setMode(MyHighlightView.NONE);
            mOverlayView.resetRonate();
            postInvalidate();
        }
        return super.onUp(e);
    }

    // private long clicktime = 0;

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        needResetRect = true;
    }

    /**
     * 轻击触摸屏后，弹起。
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // Log.i(LOG_TAG, LOG_TAG + " onSingleTapUp ");
        if (mOverlayView != null) {
            int edge = mOverlayView.getHit(e.getX(), e.getY());
            if ((edge & MyHighlightView.MOVE) == MyHighlightView.MOVE) {
                if (mDrawableListener != null) {
                    // if ((curtime - clicktime) < 1000) {
                    // // 双击事件
                    // mDrawableListener.onDoubleClick(mOverlayView);
                    // } else {
                    mDrawableListener.onClick(mOverlayView);
                    // }
                }
                // clicktime = curtime;
                return true;
            }

            mOverlayView.setMode(MyHighlightView.NONE);
            postInvalidate();

            // //log.d(LOG_TAG, "selected items: " + mOverlayViews.size());

            if (mOverlayViews.size() != 1) {
                setSelectedHighlightView(null);
            }
        }

        return super.onSingleTapUp(e);
    }

    /**
     * 当手在屏幕上滑动离开屏幕时触发
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Log.i(LOG_TAG, LOG_TAG + " onScroll");

        float dx, dy;

        float x = e2.getX();
        float y = e2.getY();

        if (!mScrollStarted) {
            dx = 0;
            dy = 0;
            mScrollStarted = true;
        } else {
            dx = mLastMotionScrollX - x;
            dy = mLastMotionScrollY - y;
        }

        mLastMotionScrollX = x;
        mLastMotionScrollY = y;

        if (mOverlayView != null && mOverlayView.getMode() != MyHighlightView.NONE) {
            if (mOverlayView.isLock() == false) {
                mOverlayView.onMouseMove(mOverlayView.getMode(), e2, -dx, -dy);
                postInvalidate();

                if (mDrawableListener != null) {
                    mDrawableListener.onMove(mOverlayView);
                }
            }

            if (mOverlayView.getMode() == MyHighlightView.MOVE) {
                if (!mScaleWithContent) {
                    ensureVisible(mOverlayView, distanceX, distanceY);
                }
            }
            return true;
        } else {
            // 当前未选中设备，发生了滑动
            // disreturn = false;
            // motionType = 4;
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     * 当手在屏幕上滑动但手未离开屏幕时触发 MotionEvent e1 手开始触碰屏幕的位置的MotionEvent对象 MotionEvent e2
     * 手结束触碰屏幕的位置的MotionEvent对象 float velocityX 表示手在水平方向的移动速度 float velocityX
     * 表示手在垂直方向的移动速度
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Log.i(LOG_TAG, LOG_TAG + " onFling");

        if (mOverlayView != null && mOverlayView.getMode() != MyHighlightView.NONE)
            return false;
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    public float getmLastMotionScrollX() {
        return mLastMotionScrollX;
    }

    public float getmLastMotionScrollY() {
        return mLastMotionScrollY;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Log.i(LOG_TAG, LOG_TAG + " onDraw");

        boolean shouldInvalidateAfter = false;

        for (int i = 0; i < mOverlayViews.size(); i++) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);

            MyHighlightView current = mOverlayViews.get(i);
            if (needResetRect) {
                current.invalidate();
                needResetRect = false;
            }
            current.draw(canvas);

            // check if we should invalidate again the canvas
            if (!shouldInvalidateAfter) {
                FeatherDrawable content = current.getContent();
                if (content instanceof EditableDrawable) {
                    if (((EditableDrawable) content).isEditing()) {
                        shouldInvalidateAfter = true;
                    }
                }
            }

            canvas.restore();
        }

        if (null != mDropPaint) {
            getDrawingRect(mTempRect);
            canvas.drawRect(mTempRect, mDropPaint);
        }

        if (shouldInvalidateAfter) {
            // 延迟刷新
            postInvalidateDelayed(CURSOR_BLINK_TIME);
        }
    }

    public void clearOverlays() {
        // Log.i(LOG_TAG, "clearOverlays");
        setSelectedHighlightView(null);
        while (mOverlayViews.size() > 0) {
            MyHighlightView hv = mOverlayViews.remove(0);
            hv.dispose();
        }
        mOverlayView = null;
        tempView = null;
    }

    public boolean addHighlightView(MyHighlightView hv) {
        for (int i = 0; i < mOverlayViews.size(); i++) {
            if (mOverlayViews.get(i).equals(hv))
                return false;
        }
        mOverlayViews.add(hv);
        postInvalidate();

        if (mOverlayViews.size() == 1) {
            setSelectedHighlightView(hv);
        }

        return true;
    }

    public int getHighlightCount() {
        return mOverlayViews.size();
    }

    public MyHighlightView getHighlightViewAt(int index) {
        return mOverlayViews.get(index);
    }

    public boolean removeHightlightView(MyHighlightView view) {
        // Log.i(LOG_TAG, "removeHightlightView");
        for (int i = 0; i < mOverlayViews.size(); i++) {
            if (mOverlayViews.get(i).equals(view)) {
                MyHighlightView hv = mOverlayViews.remove(i);
                if (hv.equals(mOverlayView)) {
                    setSelectedHighlightView(null);
                }
                hv.dispose();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onZoomAnimationCompleted(float scale) {
        // Log.i(LOG_TAG, "onZoomAnimationCompleted: " + scale);
        super.onZoomAnimationCompleted(scale);
        if (mOverlayView != null) {
            mOverlayView.setMode(MyHighlightView.MOVE);
            postInvalidate();
        }
    }

    public MyHighlightView getSelectedHighlightView() {
        return mOverlayView;
    }

    int page;

    public void setPage(int p) {
        page = p;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventIsStickerPage page) {
//        unRegisterEvent();
        if (this.page == page.getPage())
            return;
        if (mOverlayView != null) {
            mOverlayView.setSelected(false);
        }

        postInvalidate();

        mOverlayView = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventFocusCancel focusCancel){
        if (mOverlayView != null) {
            mOverlayView.setSelected(false);
        }
        postInvalidate();
    }

    /**
     * @param newView 为null，表示当前没有贴纸被点击
     */
    public void setSelectedHighlightView(MyHighlightView newView) {
        final MyHighlightView oldView = mOverlayView;
        //// Logger.d("setSelectedHighlightView ");
        if (mOverlayView != null && !mOverlayView.equals(newView)) {
            // 取消选中
            mOverlayView.setSelected(false);
        }

        if (newView != null ) {
            newView.setSelected(true);
//            if(!newView.equals(mOverlayView))
            EventBus.getDefault().post(new EventIsStickerPage(page));
        }

        postInvalidate();

        mOverlayView = newView;

        if (mDrawableListener != null) {
            mDrawableListener.onFocusChange(newView, oldView);
        }

    }

    /**
     * 提交内容
     *
     * @param canvas
     */
    @Deprecated
    public void commit(Canvas canvas) {
        MyHighlightView hv;
        for (int i = 0; i < getHighlightCount(); i++) {
            hv = getHighlightViewAt(i);
            FeatherDrawable content = hv.getContent();
            if (content instanceof EditableDrawable) {
                ((EditableDrawable) content).endEdit();
            }

            Matrix rotateMatrix = hv.getCropRotationMatrix();
            Rect rect = hv.getCropRect();

            int saveCount = canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.concat(rotateMatrix);
            content.setBounds(rect);
            content.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    /**
     * 监测当前被点击的是哪个贴纸
     *
     * @param e
     * @return
     */
    private MyHighlightView checkSelection(MotionEvent e) {
        //// Logger.d("checkSelection ");
        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        MyHighlightView selection = null;
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            int edge = view.getHit(e.getX(), e.getY());
            if (edge != MyHighlightView.NONE) {
                if (view.CanBeMoveDepth() == false) {
                    // 被锁定的贴纸无法选中，但要显示红色编辑框

                } else {
                    selection = view;
                }
            }
        }
        return selection;
    }


    /**
     * 贴纸向上
     */
    public void stickerToTop() {
        if (mOverlayView == null)
            return;

        if (mOverlayView.isSelected() && mOverlayView.CanBeMoveDepth()) {
            for (int i = 0; i < mOverlayViews.size(); i++) {
                MyHighlightView highlightView = mOverlayViews.get(i);
                if (highlightView == mOverlayView) {
                    if ((i + 1) == mOverlayViews.size()) {
                        break;
                    }
                    mOverlayViews.remove(i);
                    mOverlayViews.add(i + 1, mOverlayView);
//                    EffectUtil.sticktop(i);
//                    invalidate();
//                    EventBus.getDefault().post(new EventStickerAction(this, CartoonEditorActivity.stickerTop,i));

                    break;
                }
            }

        }
    }
    /**
     * 贴纸向下
     */
    public void stickerToBottom() {
        if (mOverlayView == null)
            return;

        if (mOverlayView.isSelected() && mOverlayView.CanBeMoveDepth()) {
            for (int i = 0; i < mOverlayViews.size(); i++) {
                MyHighlightView highlightView = mOverlayViews.get(i);
                if (highlightView == mOverlayView) {
                    if (i == 0) {
                        break;
                    }
                    mOverlayViews.remove(i);
                    mOverlayViews.add(i - 1, mOverlayView);
//                    EventBus.getDefault().post(new EventStickerAction(this, CartoonEditorActivity.stickerBottom,i));
                    break;
                }
            }
        }
    }

    /*
     * 贴纸删除
     */
    public void stickdelete() {
        if (mOverlayView == null)
            return;

        if (mOverlayView.isSelected()) {
            mOverlayView.stickerdelete();
        }
    }

    /*
     * 贴纸锁定
     */
    public void sticklock() {
        if (mOverlayView == null)
            return;

        if (mOverlayView.isSelected()) {
            mOverlayView.stickerlock();
        }
    }

    public List<StickerPaint> getStickers(int leftpadding, int toppadding, float scaleImg) {
        List<StickerPaint> stickerlist = new ArrayList<StickerPaint>();
        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            StickerPaint sticker = view.getSticker(leftpadding, toppadding, scaleImg);
            sticker.setPos(page);
            sticker.setDepth(i);
            stickerlist.add(sticker);
            i++;
        }
        return stickerlist;
    }

    // /**
    // * 获取贴纸信息
    // *
    // * @return
    // */
    // public List<StickerPaint> getStickers() {
    // List<StickerPaint> stickerlist = new ArrayList<>();
    // Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
    // int i = 0;
    // while (iterator.hasNext()) {
    // MyHighlightView view = iterator.next();
    // StickerPaint sticker = view.getSticker();
    // sticker.setDepth(i);
    // stickerlist.add(sticker);
    // i++;
    // }
    // return stickerlist;
    // }

    public List<StickerPaint> getStickers(float scaleImg) {
        List<StickerPaint> stickerlist = new ArrayList<StickerPaint>();
        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            StickerPaint sticker = view.getSticker(scaleImg);
            sticker.setDepth(i);
            stickerlist.add(sticker);
            i++;
        }
        return stickerlist;
    }

    public StickerPaint getNowStickers(int leftpadding, int toppadding, float scaleImg) {
        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            StickerPaint sticker = view.getSticker(leftpadding, toppadding, scaleImg);
            sticker.setDepth(i);
            if (view.isSelected()) {
                return sticker;
            }
            i++;
        }
        return null;
    }

    public StickerPaint getNowStickers(float scaleImg) {
        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            if (view.isSelected()) {
                StickerPaint sticker = view.getSticker(scaleImg);
                sticker.setDepth(i);
                return sticker;
            }
            i++;
        }
        return null;
    }

    public void getVisibileRect() {

    }

    public void setSelectedHighlightView() {
        if (tempView != null && tempView.isSelected() == false)
            setSelectedHighlightView(tempView);
    }

    public static interface OnDrawableEventListener {
        void onFocusChange(MyHighlightView newFocus, MyHighlightView oldFocus);

        void onDown(MyHighlightView view);

        void onMove(MyHighlightView view);

        void onClick(MyHighlightView view);

        void onDoubleClick(MyHighlightView mOverlayView);
    }

}
