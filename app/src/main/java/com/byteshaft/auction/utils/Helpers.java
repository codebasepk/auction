package com.byteshaft.auction.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringDataFromSharedPreference(String key) {
        SharedPreferences sharedPreferences = getPrefrenceManager();
        return sharedPreferences.getString(key, "");
    }

    public static void getSessionId(String email, String password, String userName,
                                      String phoneNumber, String city, String address)
            throws IOException, JSONException {
        URL url;
        HttpURLConnection urlConnection;
        url = new URL (AppGlobals.REGISTER_URL);
        urlConnection =(HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestMethod("POST");
        urlConnection.connect();
        String data = String.format("{\"username\": \"%s\", \"password\": \"%s\"," +
                "\"email\": \"%s\", \"address\": \"%s\"," +
                "\"city\": \"%s\", \"phone_number\": \"%s\" }", userName, password, email, address, city, phoneNumber);
        byte[] bytes = data.getBytes("UTF-8");
        OutputStream os = urlConnection.getOutputStream();
        os.write(bytes);
        os.close();
        Log.i(AppGlobals.getLogTag(AppGlobals.getContext().getClass()), String.valueOf(urlConnection.getResponseCode()));
    }

}
