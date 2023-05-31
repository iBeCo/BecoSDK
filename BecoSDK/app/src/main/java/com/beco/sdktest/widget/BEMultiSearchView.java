package com.beco.sdktest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beco.sdk.model.BEPoint;
import com.beco.sdktest.R;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

public class BEMultiSearchView extends LinearLayout implements StopLocationAdapter.OnItemClickListener {

    private static final String TAG = "BEMultiSearchView";
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = 180.0f;

    private MultiSearchBarListener listener;
    private CardView multiSearchCard;
    private RecyclerView rvStops;
    private StopDataProvider mDataProvider;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private StopLocationAdapter mStopLocationAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private ImageView imgReverse;
    private ImageView imgAddStops;
    private RotateAnimation rotateAnimation;
    private boolean isInAddState = false;
    private RelativeLayout relativeLayoutTotalDistance;
    private int maximumLimit = 3;

    public BEMultiSearchView(Context context) {
        super(context);
        init(context);
    }

    public BEMultiSearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BEMultiSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.cardview_add_places, this);
        multiSearchCard = findViewById(R.id.cardview_add_places);
        rvStops = findViewById(R.id.rvStops);
        imgReverse = findViewById(R.id.imgReverse);
        imgAddStops = findViewById(R.id.imgAddStops);
        relativeLayoutTotalDistance = findViewById(R.id.rel_total_dist);
        TextView tvDone = findViewById(R.id.tvDone);
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            if (listener != null) {
                isInAddState = false;
                StopsItemModel model = null;
                if(mDataProvider.hasCurrentLocation()){
                    model = mDataProvider.getCurrentLocation();
                }
                mDataProvider.clearItems();
                if(model != null){
                    mDataProvider.addCurrentLocation(model);
                }else {
                    mDataProvider.addItemData(mDataProvider.getCount(), "Select Source", true, false);
                }

                mStopLocationAdapter.notifyDataSetChanged();
                listener.onBackButtonClick();
                multiSearchCard.setVisibility(GONE);
            }
        });

        tvDone.setOnClickListener(view -> {
            if(mDataProvider.getCount() == 3 && mDataProvider.containsEmptyStringItem()){
                onClearItem(2);
            }else{
                clearEmptyItem();
            }
            listener.onRouteDone(getTextFromFirstVisibleLoc(), getStopCount(), getTextFromLastVisibleLoc());
        });
        imgReverse.setOnClickListener(v -> {
            //TODO Bug with text view icon reverse
            rotateAnimation = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration((long) 300);
            rotateAnimation.setRepeatCount(0);
            imgReverse.startAnimation(rotateAnimation);
            mStopLocationAdapter.onMoveItem(0, 1);
        });

        imgAddStops.setOnClickListener(view -> {
            imgAddStops.setVisibility(View.GONE);
            imgReverse.setVisibility(View.GONE);
            mDataProvider.addItemData(mDataProvider.getCount(), "", false, false);
            mStopLocationAdapter.notifyDataSetChanged();
            isInAddState = true;
        });
        initDataProvider();
    }

    private void initDataProvider(){
        mDataProvider = new StopDataProvider();
        mDataProvider.addItemData(mDataProvider.getCount(), "Select Source", true, false);
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setInitiateOnMove(true);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);
        mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
        mRecyclerViewDragDropManager.setDraggingItemScale(1.2f);

        mStopLocationAdapter = new StopLocationAdapter(mDataProvider, this);
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mStopLocationAdapter);
        GeneralItemAnimator animator = new DraggableItemAnimator();

        rvStops.setLayoutManager(mLayoutManager);
        rvStops.setAdapter(mWrappedAdapter);
        rvStops.setItemAnimator(animator);

        mRecyclerViewDragDropManager.attachRecyclerView(rvStops);
    }

    public void show(BEPoint point){
        mDataProvider.addItemData(mDataProvider.getCount(), point, false, false);
        mStopLocationAdapter.notifyDataSetChanged();
        multiSearchCard.setVisibility(VISIBLE);
    }

    public void visible(){
        multiSearchCard.setVisibility(VISIBLE);
    }

    public void clear(){
        StopsItemModel model = null;
        if(mDataProvider.hasCurrentLocation()){
            model = mDataProvider.getCurrentLocation();
        }
        mDataProvider.clearItems();

        if(model != null){
            mDataProvider.addCurrentLocation(model);
        }else {
            mDataProvider.addItemData(mDataProvider.getCount(), "Select Source", true, false);
        }
        mStopLocationAdapter.notifyDataSetChanged();

    }

    public void clearAndHide(){
        clear();
        hide();
    }

    public void setLimit(int limit){
        maximumLimit = limit;
    }

    public void hide(){
        multiSearchCard.setVisibility(GONE);
    }

    public void setCurrentLocation(BEPoint point){
        mDataProvider.updateCurrentLocation(point);
        mStopLocationAdapter.notifyDataSetChanged();
    }

    public boolean hasCurrentLocation(){
        return mDataProvider.hasCurrentLocation();
    }

    private String getTextFromLastVisibleLoc() {
        if (mDataProvider.getCount() > 2) {
            return mDataProvider.getItem(mDataProvider.getCount() - 1).getmText();
        } else
            return "error";
    }

    private String getTextFromFirstVisibleLoc() {
        return mDataProvider.getItem(0).getmText();
    }

    private String getStopCount() {
        for (int i = 0; i < mDataProvider.getCount(); i++) {
            if (mDataProvider.getItem(i).getmText().equals("")) {
                mDataProvider.removeItem(i);
            }
        }

        if(mDataProvider.getCount()>3){
            return mDataProvider.getCount()-2+" Stops";
        }else if(mDataProvider.getCount()==3){
            return mDataProvider.getItem(1).getmText();
        }else{
            return "error";
        }
    }

    @Override
    public void onStopClick(int position) {
        Log.d(TAG,"onStopClick");
        if (listener != null) {
            listener.onStopAddButtonClick(position);
        }
    }

    public void setData(int position, BEPoint point){
        try {
            mDataProvider.getItem(position).setData(point);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mDataProvider.getCount() < maximumLimit && !mDataProvider.containsEmptyStringItem() && isInAddState) {
         BEPoint point1 = null;
            mDataProvider.addItemData(mDataProvider.getCount(), point1, false, false);
        }

        rvStops.smoothScrollToPosition(mDataProvider.getCount() - 1);
        mStopLocationAdapter.notifyDataSetChanged();
    }

    private void clearEmptyItem() {
        if (mDataProvider.containsEmptyStringItem()) {
            for (int i = 0; i < mDataProvider.getCount(); i++) {
                if (mDataProvider.getItem(i).getmText().equals("")) {
                    mDataProvider.removeItem(i);
                }
            }
        }
    }

    @Override
    public void onClearItem(int position) {
        if (mDataProvider.getCount() == maximumLimit && !mDataProvider.containsEmptyStringItem()) {
            mDataProvider.getItem(mDataProvider.getCount() - 1).setData(null);
        } else {
            mDataProvider.removeItem(position);
        }
        if(mDataProvider.getCount() == 2){
            listener.resetRouteRequest();
        }
        mStopLocationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMinItemCountReached() {
//        if (mDataProvider.containsEmptyStringItem())
//            imgAddStops.setVisibility(View.GONE);
//        else
//            imgAddStops.setVisibility(View.VISIBLE);
//        imgReverse.setVisibility(View.VISIBLE);

        relativeLayoutTotalDistance.setVisibility(View.GONE);
    }

    @Override
    public void onMinItemCountExceed() {
        imgAddStops.setVisibility(View.GONE);
        imgReverse.setVisibility(View.GONE);
        relativeLayoutTotalDistance.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStartCurrentLocation() {

    }

    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
    }

    public void onDestroy() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (rvStops != null) {
            rvStops.setItemAnimator(null);
            rvStops.setAdapter(null);
            rvStops = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }

        mLayoutManager = null;
    }

    public void setListener(MultiSearchBarListener listener) {
        this.listener = listener;
    }

    public interface MultiSearchBarListener {
        void onBackButtonClick();
        void resetRouteRequest();
        void onStopAddButtonClick(int position);
        void onRouteDone(String startLoc, String interStops, String lastLoc);
    }
}
