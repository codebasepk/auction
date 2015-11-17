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
}
