package com.tangfeng.wifilist.utils;

import android.content.Context;

/**
 * Date :2018/8/4
 * Time :17:49
 * author:moyihen
 */

public class DensityUtil {

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
