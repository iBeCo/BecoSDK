package com.beco.sdktest.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class LayoutUtils {

    public static float dp2px(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        float px = valueInDp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float px2dp(Context context, float valueInPx) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, valueInPx, metrics);
        float dp = valueInPx / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}
