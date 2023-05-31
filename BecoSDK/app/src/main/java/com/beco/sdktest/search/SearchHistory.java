package com.beco.sdktest.search;

import android.content.Context;

import com.beco.sdktest.App;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchHistory {
    private static final int MAX_SIZE = 15;
    private static final String SEARCH_HISTORY = "search_history";
    private static final String PREFERENCE_NAME = "prefs";

    public SearchHistory() {
        super();
    }

    public static List<String> loadList(Context context) {
        String json = context.getSharedPreferences(PREFERENCE_NAME,0).getString(SEARCH_HISTORY, "[]");
        Type type = new TypeToken<List<String>>(){}.getType();
        return App.getInstance().getGson().fromJson(json, type);
    }

    public static void clear(Context context){
        context.getSharedPreferences(PREFERENCE_NAME,0).edit().putString(SEARCH_HISTORY,"[]").apply();
    }

    public static void addList(String place, Context context) {

        if (place == null || place.isEmpty())
            return;

        List<String> history = loadList(context);
        if (history == null || history.isEmpty())
            history = new ArrayList<>();

        if(history.size() == MAX_SIZE) {
            history.remove(MAX_SIZE - 1);
        }


        boolean isFound = false;
        for(String p : history){
            if(p.equals(place)) isFound = true;
        }

        if(!isFound){
            history.add(0,place);
        }
        String json = App.getInstance().getGson().toJson(history);
        context.getSharedPreferences(PREFERENCE_NAME,0).edit().putString(SEARCH_HISTORY, json).apply();

    }
}
