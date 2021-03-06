package me.fingerart.patternlock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.List;

import me.fingerart.patternlock.interf.IPatternLockIndicator;

/**
 * 手势锁指示器
 * Created by FingerArt.me on 2016/7/26.
 */
public class PatternLockIndicator extends PatternLockView implements IPatternLockIndicator {
    public PatternLockIndicator(Context context) {
        super(context);
    }

    public PatternLockIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PatternLockIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void updateIndicator(List<Integer> index) {
        for (int y = 0; y < mMatrixPoints.length; y++) {
            for (int x = 0; x < mMatrixPoints[y].length; x++) {
                if (index.contains(y * mRaw + x)) {
                    mMatrixPoints[y][x].setState(PointState.POINT_STATE_SELECTED);
                } else {
                    mMatrixPoints[y][x].setState(PointState.POINT_STATE_NORMAL);
                }
            }
        }
        postInvalidate();
    }
}