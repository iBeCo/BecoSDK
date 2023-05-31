package com.beco.sdktest.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MaxHeightRecyclerView extends RecyclerView {
    private static final int MAX_HEIGHT_DP= 250;
    private Context mContext;
    public MaxHeightRecyclerView(Context context) {
        super(context);
        mContext = context;
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = View.MeasureSpec.makeMeasureSpec((int) LayoutUtils.dp2px(mContext, MAX_HEIGHT_DP), View.MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}