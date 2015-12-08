package com.byteshaft.auction.utils;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

public class AppGlobals extends Application{

    public static final String APPLICATION_ID = "9MwjJ95wMBfGQE1gtc07viXQpdWS3Wm7LUqK13V2";
    public static final String CLIENT_KEY = "deOcsGXlodkAiuDkCXYlCQgi1SeluIImIiLWgHtM";
    private static Context sContext;
    public static final String user_login_key = "user_login";
    public static final String lastFragment = "last_fragment";
    public static final String selectedCategory = "selected_category";
    public static final String detial = "detail";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static boolean loginSuccessFull = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
//        ParseObject.registerSubclass(Message.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

    }

    public static Context getContext() {
        return sContext;
    }
}
