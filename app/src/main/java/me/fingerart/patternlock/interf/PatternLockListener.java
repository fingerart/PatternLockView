package me.fingerart.patternlock.interf;

import java.util.List;

/**
 * Created by FingerArt.me on 2016/7/25.
 */
public interface PatternLockListener {

    /**
     * 绘制手势错误
     *
     * @param e
     */
    void onError(String e);

    /**
     * 绘制手势成功
     *
     * @param index 指示点
     */
    void onSuccess(List<Integer> index);
}
