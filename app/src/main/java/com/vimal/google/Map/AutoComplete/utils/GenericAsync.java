package com.vimal.google.Map.AutoComplete.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;


import com.vimal.google.Map.AutoComplete.map_server.Constant;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GenericAsync extends AsyncTask<Void, Void, Response> {


    private final String accessToken;
    String requestUrl;

    HashMap<String, String> param;

    OnTaskComplete<Response> callback;
    boolean success = true;
    Context context;


    public GenericAsync(Context context, @Nullable String requesturl, @Nullable String accessToken, OnTaskComplete<Response> callback) {

        this.context = context;
        this.requestUrl = requesturl;
        this.callback = callback;
        this.accessToken = accessToken;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //AlertUtils.showCustomProgressDialog(context);
    }

    @Override
    protected Response doInBackground(Void... params) {

        Response response = null;


        DebugLog.e("Access Token::" + accessToken);
        DebugLog.e("Request Url ::" + requestUrl);
        Request request;
        if (accessToken == null) {
            request = new Request.Builder().addHeader(Constant.HEADER_AUTHORIZATION, Constant.AUTHORIZATION_KEY_VALUE)

                    .url(requestUrl).build();
        } else {
            request = new Request.Builder().addHeader(Constant.HEADER_AUTHORIZATION, Constant.AUTHORIZATION_KEY_VALUE).addHeader(Constant.HEADER_ACCEPT_LANGUAGE, Constant.HEADER_ACCEPT_LANGUAGE_KEY_VALUE).addHeader(Constant.HEADER_TOCKEN, accessToken).url(requestUrl).build();
        }

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        try {
            //DebugLog.e("Request request ::" + request.toString());
            response = client.newCall(request).execute();

        } catch (IOException ex) {
            success = false;
            callback.onFailure();
            DebugLog.e("Error " + ex);
        }


        return response;
    }

    @Override
    protected void onPostExecute(Response s) {
        super.onPostExecute(s);


        callback.onSuccess(s, success);
       /* try {


            if(s.code()==401)
            {
                DataToPref.setSharedPreferanceData(context, Constant.STORED_USER, Constant.DATA, "");
                DataToPref.setSharedPreferanceData(context, Constant.STORED_USER, Constant.ISLOGIN, "false");

                Intent intent = new Intent(context, AuthenticationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                if(BaseApp.currentActivity instanceof HomeActivity) {
                    BaseApp.currentActivity.startActivity(intent);
                    BaseApp.currentActivity.finish();
                }
            }
            else
            {
                callback.onSuccess(s, success);
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }*/
    }
}
