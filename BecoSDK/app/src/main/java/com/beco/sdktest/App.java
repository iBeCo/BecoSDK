package com.beco.sdktest;


import com.beco.sdk.model.BEPoint;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private static App instance;

    private List<BEPoint> points = new ArrayList<>();
    private Map<Integer,BEPoint> wayPoints= new HashMap<Integer,BEPoint>();
    private Gson mGson;

    public static App getInstance() {
        if (instance== null) {
            synchronized(App.class) {
                if (instance == null)
                    instance = new App();
            }
        }
        return instance;
    }

    public BEPoint findPoint(String name){
        for(BEPoint p : points){
            if(p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }
    public void setPoints(List<BEPoint> points){
        this.points = points;
    }

    public List<BEPoint> getPoints(){
        return points;
    }


    public void addToWayPoint(BEPoint point, int position){
        wayPoints.put(position, point);
    }

    public BEPoint getWayPoint(int position){
        return wayPoints.get(position);
    }

    public int getWayPointCount(){
        return wayPoints.size();
    }

    private App() {
        mGson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public void clearWayPoints(){
        BEPoint point = wayPoints.get(1);
        wayPoints.clear();
        addToWayPoint(point,1);
    }

    public void clearInterMediatePoints(){
        BEPoint point = wayPoints.get(1);
        BEPoint current = wayPoints.get(0);
        wayPoints.clear();
        addToWayPoint(point,1);
        addToWayPoint(current,0);
    }

    public void clearAllPoint(){
        wayPoints.clear();
    }

    public void clearAllExceptCurrentPoint(){
        BEPoint current = wayPoints.get(0);
        wayPoints.clear();
        addToWayPoint(current,0);
    }

    public Gson getGson() {
        return mGson;
    }

}
