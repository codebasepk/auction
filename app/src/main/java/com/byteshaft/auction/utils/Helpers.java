package com.byteshaft.auction.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Helpers {

    private static SharedPreferences getPrefrenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(AppGlobals.getContext());
    }

    public static void userLogin(boolean value) {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        sharedPreferences.edit().putBoolean(AppGlobals.user_login_key, value).apply();
    }

    public static  boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        return sharedPreferences.getBoolean(AppGlobals.user_login_key, false);
    }

    public static void saveLastFragmentOpend(String value) {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        sharedPreferences.edit().putString(AppGlobals.lastFragment, value).apply();
    }

    public static String getLastFragment() {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        return sharedPreferences.getString(AppGlobals.lastFragment, "");
    }

    public static void saveCategoryStatus(String key, boolean value) {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getCategoryStatue(String key) {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        return sharedPreferences.getBoolean(key, false);
    }


}
