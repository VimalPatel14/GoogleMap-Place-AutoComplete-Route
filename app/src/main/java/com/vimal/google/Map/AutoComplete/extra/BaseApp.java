package com.vimal.google.Map.AutoComplete.extra;

import android.app.Activity;
import android.app.Application;

import com.vimal.google.Map.AutoComplete.model.CurrentRide;

import java.util.ArrayList;

public class BaseApp extends Application {

    private static BaseApp singleton;

    public static BaseApp getInstance() {
        return singleton;
    }

    public static CurrentRide currentRide = new CurrentRide();

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public static ArrayList<Activity> activity_List = new ArrayList<Activity>();
    public static Activity currentActivity;


    public static void setCurrentActivity(Activity mCurrentActivity) {
        try {
            activity_List.add(mCurrentActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearActivity() {

        try {
            if (activity_List.size() > 0) {
                for (int i = 0; i < activity_List.size(); i++) {
                    activity_List.get(i).finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
