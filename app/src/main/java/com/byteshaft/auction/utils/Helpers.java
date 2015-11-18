package com.byteshaft.auction.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Helpers {

    private static SharedPreferences getPrefrenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(AppGlobals.getContext());
    }

    public static void saveUserRole(String value) {
        System.out.println(value);
        SharedPreferences sharedPreferences = getPrefrenceManager();
        sharedPreferences.edit().putString(AppGlobals.USER_ROLE_KEY, value).apply();
    }

    public static String getUserRole() {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        return sharedPreferences.getString(AppGlobals.USER_ROLE_KEY, "");
    }

    public static void userRegistered(boolean value) {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        sharedPreferences.edit().putBoolean(AppGlobals.user_register_key, value).apply();
    }

    public static  boolean isUserRegistered() {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        return sharedPreferences.getBoolean(AppGlobals.user_register_key, false);
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

}
