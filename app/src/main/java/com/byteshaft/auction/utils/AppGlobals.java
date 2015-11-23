package com.byteshaft.auction.utils;

import android.app.Application;
import android.content.Context;

public class AppGlobals extends Application{

    private static Context sContext;
    public static final String user_login_key = "user_login";
    public static final String lastFragment = "last_fragment";
    public static final String selectedCategory = "selected_category";
    public static final String detial = "detail";

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
