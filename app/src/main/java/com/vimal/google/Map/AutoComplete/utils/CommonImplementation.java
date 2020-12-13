package com.vimal.google.Map.AutoComplete.utils;

import android.content.Context;


import com.vimal.google.Map.AutoComplete.extra.BaseApp;

import java.util.HashMap;

import okhttp3.Response;

public class CommonImplementation implements CommonInterface {

    String requesturl;
    Context context = BaseApp.getInstance();

    public static CommonImplementation ourInstance = new CommonImplementation();
    OnTaskComplete onTaskComplete;


    public static CommonImplementation getInstance() {
        return ourInstance;
    }

    private CommonImplementation() {

    }

    @Override
    public void doGetLocation(HashMap<String, String> param, String url, final OnTaskComplete onTaskComplete) {
        new GenericAsync(context, url, null, new OnTaskComplete() {
            @Override
            public void onSuccess(Response response, boolean success) {

                onTaskComplete.onSuccess(response, success);
            }

            @Override
            public void onFailure() {
                onTaskComplete.onFailure();
            }
        }).execute();

    }
}
