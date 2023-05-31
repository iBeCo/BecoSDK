package com.beco.sdktest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.beco.sdktest.R;

public class BEMultiRouteView extends LinearLayout {

    MultiRouteListener mListener;
    ConstraintLayout constraintLayout;
    CardView cardviewAddedPlaces;
    TextView tvLocStart, tvLocInter, tvLocEnd;
    ImageView imgBack;

    public BEMultiRouteView(Context context) {
        super(context);
        init(context);
    }

    public BEMultiRouteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BEMultiRouteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.cardview_added_places, this);
        cardviewAddedPlaces = findViewById(R.id.cardview_added_places);
        constraintLayout = findViewById(R.id.constraintLayout);
        tvLocStart = findViewById(R.id.tvLocStart);
        tvLocInter = findViewById(R.id.tvLocInter);
        tvLocEnd = findViewById(R.id.tvLocEnd);
        imgBack = findViewById(R.id.img_back1);

        constraintLayout.setOnClickListener(view -> {
            mListener.onReselectRoute();
        });

        imgBack.setOnClickListener(view -> {
            mListener.onBackClick();
        });
    }

    public void show(String startLoc, String interLoc, String endLoc) {
        cardviewAddedPlaces.setVisibility(VISIBLE);
        tvLocStart.setText(startLoc);
        tvLocInter.setText(interLoc);
        tvLocEnd.setText(endLoc);
    }

    public void hide(){
        cardviewAddedPlaces.setVisibility(GONE);
    }

    public void visible(){
        cardviewAddedPlaces.setVisibility(VISIBLE);
    }

    public void setListener(MultiRouteListener listener){
        mListener = listener;
    }

    public interface MultiRouteListener{
        void onReselectRoute();
        void onBackClick();
    }

}
