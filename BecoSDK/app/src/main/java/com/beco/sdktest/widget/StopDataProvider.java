package com.beco.sdktest.widget;

import com.beco.sdk.model.BEPoint;

import java.util.LinkedList;
import java.util.List;

public class StopDataProvider {

    private List<StopsItemModel> mData;

    StopDataProvider(){
        mData = new LinkedList<>();
    }

    int getCount(){
        return mData.size();
    }

    StopsItemModel getItem(int index){
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }
        return mData.get(index);
    }

    void removeItem(int position){
        mData.remove(position);
    }

    void clearItems(){
        mData.clear();
    }

    void moveItem(int fromPosi, int toPosi){
        if(fromPosi == toPosi){
            return;
        }

        StopsItemModel item = mData.remove(fromPosi);

        mData.add(toPosi, item);
    }

    public void addItem(StopsItemModel item){
        mData.add(item);
    }

    public void addItems(List<StopsItemModel> items){
        mData.addAll(items);
    }

    void addItemData(long id, String text, boolean currentLoc, boolean showClear){
        mData.add(new StopsItemModel(id, text, currentLoc, showClear));
    }

    void addCurrentLocation(StopsItemModel stopsItemModel){
        mData.add(stopsItemModel);
    }

    void addItemData(long id, BEPoint point, boolean currentLoc, boolean showClear){
        mData.add(new StopsItemModel(id, point, currentLoc, showClear));
    }

    boolean containsEmptyStringItem(){
        for(StopsItemModel s: mData){
            if(s.getmText().equals(""))
                return true;
        }
        return false;
    }

    void updateCurrentLocation(BEPoint point){
        for(StopsItemModel s: mData){
            if(s.isCurrentLoc()){
                s.setBePoint(point);
                s.setmText(point.getName());
            }
        }
    }

    boolean hasCurrentLocation(){
        for(StopsItemModel s: mData){
            if(s.isCurrentLoc() && s.getBePoint() != null){
                return true;
            }
        }
        return false;
    }

    StopsItemModel getCurrentLocation(){
        for(StopsItemModel s: mData){
            if(s.isCurrentLoc() && s.getBePoint() != null){
                return s;
            }
        }
        return null;
    }

    public boolean containsEmptyItem(){
        return false;
    }

}