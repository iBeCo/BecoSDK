package com.beco.sdktest.widget;

import com.beco.sdk.model.BEPoint;

public class StopsItemModel {

    private long mId;
    private String mText;
    private boolean currentLoc;
    private boolean showClear;
    private BEPoint bePoint;

    public StopsItemModel(long mId, String mText, boolean currentLoc, boolean showClear) {
        this.mId = mId;
        this.mText = mText;
        this.currentLoc = currentLoc;
        this.showClear = showClear;
        this.bePoint = null;
    }


    public StopsItemModel(long mId, BEPoint bePoint, boolean currentLoc, boolean showClear) {
        this.mId = mId;
        if(bePoint!= null) this.mText = bePoint.getName();
        else
            this.mText="";
        this.currentLoc = currentLoc;
        this.showClear = showClear;
        this.bePoint = bePoint;
    }


    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public String getmText() {
        return mText;
    }

    public void setData(BEPoint point){
        if(point!= null){
            this.mText = point.getName();
        }else {
            this.mText = "";
        }
        this.bePoint = point;
    }

    public boolean isCurrentLoc() {
        return currentLoc;
    }

    public void setCurrentLoc(boolean currentLoc) {
        this.currentLoc = currentLoc;
    }

    public boolean isShowClear() {
        return showClear;
    }

    public void setShowClear(boolean showClear) {
        this.showClear = showClear;
    }

    public BEPoint getBePoint() {
        return bePoint;
    }

    public void setBePoint(BEPoint bePoint) {
        this.bePoint = bePoint;
    }
}
