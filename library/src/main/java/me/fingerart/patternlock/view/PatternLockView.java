package me.fingerart.patternlock.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.fingerart.patternlock.R;
import me.fingerart.patternlock.interf.IPatternLockIndicator;
import me.fingerart.patternlock.interf.PatternLockListener;

import static me.fingerart.patternlock.utils.PatternLockUtils.isInRound;
import static me.fingerart.patternlock.utils.PatternLockUtils.vibrate;

/**
 * 图案解锁
 * Created by FingerArt.me on 2016/7/22.
 */
public class PatternLockView extends View {
    private static final String TAG = PatternLockView.class.getSimpleName();
    private int mHeight;
    private int mWidth;
    /**
     * 所有的点
     */
    protected Point[][] mMatrixPoints;
    /**
     * 连线的点
     */
    protected ArrayList<Point> mLinePoints = new ArrayList<>();
    List<Integer> mIndex = new ArrayList<>();
    private float ex, ey;
    protected float mLineWidth;
    protected float mRingOuterRadius;
    protected float mRingBorder;
    protected float mRingInnerRadius;
    protected int mRingDefaultColor;
    protected int mRingPressedColor;
    protected int mRingErrorColor;
    protected int mLineDefaultColor;
    protected int mLineErrorColor;
    protected int mCircularErrorColor;
    protected int mCircularPressedColor;
    protected Paint mOuterRingPaint;
    protected Paint mLinePaint;
    protected Paint mInnerPaint;
    protected Paint mCircularPaint;
    private long mVibrate;
    protected int mMinPoint;
    protected int mRaw;
    protected boolean mAutoClear;
    private boolean inTouch;
    private boolean initialized = false;
    protected PatternLockListener mLockListener;
    protected IPatternLockIndicator mIndicator;

    public PatternLockView(Context context) {
        this(context, null);
    }

    public PatternLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PatternLockView, defStyleAttr, 0);
        mRaw = typedArray.getInt(R.styleable.PatternLockView_raw, 3);
        mVibrate = (long) typedArray.getFloat(R.styleable.PatternLockView_vibrate_long, 10);
        mMinPoint = typedArray.getInt(R.styleable.PatternLockView_min_point, 3);
        mAutoClear = typedArray.getBoolean(R.styleable.PatternLockView_auto_clear, false);
        mRingOuterRadius = typedArray.getDimension(R.styleable.PatternLockView_ring_outer_radius, 70);
        mRingInnerRadius = typedArray.getDimension(R.styleable.PatternLockView_ring_inner_radius, 25);
        mRingBorder = typedArray.getDimension(R.styleable.PatternLockView_ring_border, 3);
        mRingDefaultColor = typedArray.getColor(R.styleable.PatternLockView_ring_default, getColor(R.color.ring_default));
        mRingPressedColor = typedArray.getColor(R.styleable.PatternLockView_ring_pressed, getColor(R.color.ring_pressed));
        mRingErrorColor = typedArray.getColor(R.styleable.PatternLockView_ring_error, getColor(R.color.ring_error));
        mCircularErrorColor = typedArray.getColor(R.styleable.PatternLockView_circular_error, getColor(R.color.circular_error));
        mCircularPressedColor = typedArray.getColor(R.styleable.PatternLockView_circular_pressed, getColor(R.color.circular_pressed));
        mLineDefaultColor = typedArray.getColor(R.styleable.PatternLockView_line_default, getColor(R.color.line_default));
        mLineErrorColor = typedArray.getColor(R.styleable.PatternLockView_line_error, getColor(R.color.line_error));
        mLineWidth = typedArray.getDimension(R.styleable.PatternLockView_line_width, 8);
        typedArray.recycle();
    }

    private int getColor(int color) {
        return getResources().getColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initDataIfNot();
        drawPoints(canvas);
        drawLine(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        inTouch = true;
        ex = event.getX();
        ey = event.getY();
        Point p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                p = getCurrentTouchPoint(ex, ey);
                break;
            case MotionEvent.ACTION_UP:
                inTouch = false;
                validatePassword();
                break;
        }

        if (p != null) {
            p.setState(PointState.POINT_STATE_SELECTED);
            if (isNewPoint(p)) {
                mLinePoints.add(p);
                vibrate(getContext(), mVibrate);
            }
        }
        invalidate();
        return true;
    }

    /**
     * 校验手势的合法性
     */
    private void validatePassword() {
        if (mLinePoints.size() < mMinPoint) {
            handleError();
        } else {
            handleSuccess();
        }
    }

    private void handleSuccess() {
        mIndex.clear();
        for (Point p : mLinePoints) {
            mIndex.add(Integer.parseInt(p.getValue()));
        }
        if (mLockListener != null) {
            mLockListener.onSuccess(mIndex);
        }
        if (mAutoClear) {
            delayedClearPatternLock();
        }
    }

    @SuppressLint("StringFormatMatches")
    private void handleError() {
        switchState(PointState.POINT_STATE_SELECTED, PointState.POINT_STATE_SELECTED_ERROR);
        if (mLockListener != null) {
            mLockListener.onError(getResources().getString(R.string.error_size_not_satisfied, mMinPoint));
        }
        delayedClearPatternLock();
    }

    private void delayedClearPatternLock() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                clearPatternLock();
            }
        }, 500);
    }

    /**
     * 切换点的状态
     *
     * @param from
     * @param to
     */
    private void switchState(PointState from, PointState to) {
        for (Point[] py : mMatrixPoints) {
            for (Point p : py) {
                if (from == null || p.getState() == from) {
                    p.setState(to);
                }
            }
        }
    }

    private boolean isNewPoint(Point p) {
        return !mLinePoints.contains(p);
    }

    /**
     * 获取当前触摸到的点
     *
     * @param ex
     * @param ey
     * @return
     */
    private Point getCurrentTouchPoint(float ex, float ey) {
        for (Point[] px : mMatrixPoints) {
            for (Point point : px) {
                if (isInRound(point.getX(), point.getY(), mRingOuterRadius, ex, ey))
                    return point;
            }
        }
        return null;
    }

    private void initDataIfNot() {
        if (initialized) return;

        mWidth = getWidth();
        mHeight = getHeight();
        int offsetX = 0, offsetY = 0;

        if (mWidth > mHeight) {
            offsetX = (mWidth - mHeight) / 2;
            mWidth = mHeight;
        } else {
            offsetY = (mHeight - mWidth) / 2;
            mHeight = mWidth;
        }

        mMatrixPoints = new Point[mRaw][mRaw];

        for (int x = 0; x < mMatrixPoints.length; x++) {
            float py = offsetY + (x + 1) * mWidth / mRaw - mWidth / mRaw / 2;
            for (int y = 0; y < mMatrixPoints[x].length; y++) {
                float px = offsetX + (y + 1) * mHeight / mRaw - mHeight / mRaw / 2;
                mMatrixPoints[x][y] = new Point(px, py, generatePointPassword(x * mRaw + y));
            }
        }

        mOuterRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterRingPaint.setStyle(Paint.Style.STROKE);
        mOuterRingPaint.setStrokeWidth(mRingBorder);

        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setStyle(Paint.Style.FILL);

        mCircularPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircularPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mLineWidth);

        initialized = true;
    }

    /**
     * 绘制所有的点
     *
     * @param canvas
     */
    private void drawPoints(Canvas canvas) {
        for (int x = 0; x < mMatrixPoints.length; x++) {
            for (int y = 0; y < mMatrixPoints[x].length; y++) {
                Point p = mMatrixPoints[x][y];
                switch (p.getState()) {
                    case POINT_STATE_NORMAL:
                        mInnerPaint.setColor(Color.TRANSPARENT);
                        mOuterRingPaint.setColor(mRingDefaultColor);
                        break;
                    case POINT_STATE_SELECTED:
                        mCircularPaint.setColor(mCircularPressedColor);
                        mOuterRingPaint.setColor(mRingPressedColor);
                        mInnerPaint.setColor(mLineDefaultColor);
                        break;
                    case POINT_STATE_SELECTED_ERROR:
                        mCircularPaint.setColor(mCircularErrorColor);
                        mOuterRingPaint.setColor(mRingErrorColor);
                        mInnerPaint.setColor(mLineErrorColor);
                        break;
                }
                if (p.getState() != PointState.POINT_STATE_NORMAL) {
                    canvas.drawCircle(p.getX(), p.getY(), mRingOuterRadius, mCircularPaint);//绘制最底层圆形
                }
                canvas.drawCircle(p.getX(), p.getY(), mRingOuterRadius, mOuterRingPaint);//外部圆环
                canvas.drawCircle(p.getX(), p.getY(), mRingInnerRadius, mInnerPaint);//内部圆形
            }
        }
    }

    /**
     * 绘制连线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        if (mLinePoints.size() < 1) return;

        Point tp = mLinePoints.get(0);

        switch (tp.getState()) {
            case POINT_STATE_SELECTED_ERROR:
                mLinePaint.setColor(mLineErrorColor);
                break;
            default:
                mLinePaint.setColor(mLineDefaultColor);
        }

        for (int i = 1; i < mLinePoints.size(); i++) {
            Point p = mLinePoints.get(i);
            canvas.drawLine(tp.getX(), tp.getY(), p.getX(), p.getY(), mLinePaint);
            tp = p;
        }

        if (inTouch) {
            canvas.drawLine(tp.getX(), tp.getY(), ex, ey, mLinePaint);
        }
    }

    /**
     * 获取单个点的密码值
     *
     * @param v
     * @return
     */
    protected String generatePointPassword(int v) {
        return v + "";
    }

    /**
     * 设置手势锁监听器
     *
     * @param lockListener
     */
    public void setPatternLockListener(PatternLockListener lockListener) {
        mLockListener = lockListener;
    }

    /**
     * 设置手势锁指示器
     *
     * @param indicator
     */
    public void setPatternLockIndicator(IPatternLockIndicator indicator) {
        mIndicator = indicator;
    }

    /**
     * 清除所有手势锁
     */
    public void clearPatternLock() {
        mLinePoints.clear();
        switchState(null, PointState.POINT_STATE_NORMAL);
        postInvalidate();
    }

    /**
     * 通知指示器改变手势点
     */
    public void notifyPatternChanged() {
        if (mIndicator != null) {
            mIndicator.updateIndicator(mIndex);
        }
    }

    /**
     * 改变指定数据的点
     * 1 2 3
     * 4 5 6
     * 7 8 9
     *
     * @param index
     */
    public void setPatternPoint(List<Integer> index) {
        mIndex = index;
        invalidate();
    }
}