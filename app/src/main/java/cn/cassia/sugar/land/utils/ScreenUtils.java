package cn.cassia.sugar.land.utils;

import android.content.Context;

/**
 * Written by Mr.QingJie on 2018-1-10 0010.
 */
public final class ScreenUtils {

    private ScreenUtils() {

    }

    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕的高度（单位：px）
     *
     * @return 屏幕高
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


}