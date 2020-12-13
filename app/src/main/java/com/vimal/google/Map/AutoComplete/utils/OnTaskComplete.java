package com.vimal.google.Map.AutoComplete.utils;


import okhttp3.Response;

/**
 * Created by ${hlink} on 18/1/16.
 */
public  interface OnTaskComplete<T> {
    void onSuccess(Response response, boolean success);

    void onFailure();


}
