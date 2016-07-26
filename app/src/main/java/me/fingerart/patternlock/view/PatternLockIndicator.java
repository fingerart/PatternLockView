package me.fingerart.patternlock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import me.fingerart.patternlock.R;
import me.fingerart.patternlock.interf.PatternLockIndicatorInterf;

/**
 * 手势锁指示器
 * Created by FingerArt.me on 2016/7/26.
 */
public class PatternLockIndicator extends PatternLockView implements PatternLockIndicatorInterf {
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
    public void setIndicator(List<Integer> p) {
        for (int y = 0; y < mPoints.length; y++) {
            for (int x = 0; x < mPoints[y].length; x++) {
                if (p.contains(y * mRaw + x)) {
                    mPoints[y][x].setState(PointState.POINT_STATE_SELECTED);
                } else {
                    mPoints[y][x].setState(PointState.POINT_STATE_NORMAL);
                }
            }
        }
        postInvalidate();
    }
}
