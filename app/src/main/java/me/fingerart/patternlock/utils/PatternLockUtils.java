package me.fingerart.patternlock.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * PatternLock工具
 */
public class PatternLockUtils {
    /**
     * 点(x2,y2)是否在以(x1,y1)为圆心，r为半径的圆内
     */
    public static boolean isInRound(double x1, double y1, double r, double x2, double y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) < r * r;
    }

    /**
     * 震动
     *
     * @param context
     * @param milliseconds
     */
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
}