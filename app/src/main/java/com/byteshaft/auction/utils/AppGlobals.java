package com.byteshaft.auction.utils;

import android.app.Application;
import android.content.Context;

public class AppGlobals extends Application{

    public static final String APPLICATION_ID = "ebT08jXzK3aUuzwKAM36XVAUltLWKBVQhrCaYXgA";
    public static final String CLIENT_KEY = "Vlcn6jDAuoS2lxlDD2IWgxTJLIkZkWnS0iBJWPWs";
    private static Context sContext;
    public static final String user_login_key = "user_login";
    public static final String lastFragment = "last_fragment";
    public static final String selectedCategory = "selected_category";
    public static final String detial = "detail";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_CATEGORIES_SELECTED = "categories_selected";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY = "city";
    public static boolean loginSuccessFull = false;
    public static String cacheSaveLocationForProfilePic = "/"+ "profile_pic"+"/";
    public static int responseCode;
    public static int userExistResponce;
    public static final String LOGIN_URL = (
            "LOGIN_URL"
    );
    public static final String USER_EXIST_URL = ("http://testapp-byteshaft.herokuapp.com/user/");
    public static final String REGISTER_URL = (
            "http://testapp-byteshaft.herokuapp.com/users/"
    );

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static void setUserExistResponse(int value) {
        userExistResponce = value;
    }

    public static int getUserExistResponse() {
        return userExistResponce;
    }
    public static void setResponseCode(int value) {
        responseCode = value;
    }

    public static int getResponseCode() {
        return responseCode;
    }

    public static String getLogTag(Class aClass) {
        return aClass.getName();
    }

    public static Context getContext() {
        return sContext;
    }
}
