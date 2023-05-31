package com.beco.sdktest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.beco.sdk.model.BEPoint;
import com.beco.sdktest.R;

public class BESearchBarView extends LinearLayout {

    private SearchBarListener listener;
    private EditText searchEditText;
    private ImageView closeImageView;
    private RelativeLayout directionImageView;
    private BEPoint destination;

    public BESearchBarView(Context context) {
        super(context);
        init(context);
    }

    public BESearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BESearchBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.beco_search_bar_view, this);
        searchEditText = findViewById(R.id.edt_search);
        directionImageView = findViewById(R.id.rel_direction);
        closeImageView = findViewById(R.id.imgClose);

        directionImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNavigateButtonClick();
            }
        });

        closeImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCloseButtonClick(v);
            }
        });

        searchEditText.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchClick(v);
            }
        });
    }

    public void setListener(SearchBarListener listener) {
        this.listener = listener;
    }

    public void showCloseImage(){
        closeImageView.setVisibility(VISIBLE);
    }

    public void hideCloseImage(){
        closeImageView.setVisibility(GONE);
    }

    public void showDirectionImage(){
        directionImageView.setVisibility(VISIBLE);
    }

    public void hideDirectionImage(){
        directionImageView.setVisibility(GONE);
    }

    public void setPoint(BEPoint point){
        if(point==null){
            searchEditText.setText("");
            destination = null;
        }else {
            searchEditText.setText(point.getName());
            destination = point;
        }

    }

    public BEPoint getPoint(){
        return destination;
    }

    public interface SearchBarListener {
        void onCloseButtonClick(View view);
        void onNavigateButtonClick();
        void onSearchClick(View view);
    }
}
