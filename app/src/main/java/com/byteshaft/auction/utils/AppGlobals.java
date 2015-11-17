package com.byteshaft.auction.utils;

import android.app.Application;
import android.content.Context;

public class AppGlobals extends Application{

    private static Context sContext;
    public static final String USER_ROLE_KEY = "user_role";

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
