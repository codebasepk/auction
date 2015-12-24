package com.byteshaft.auction.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Helpers {

    private static SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(AppGlobals.getContext());
    }

    public static void userLogin(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(AppGlobals.user_login_key, value).apply();
    }

    public static  boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(AppGlobals.user_login_key, false);
    }

    public static void saveLastFragmentOpend(String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(AppGlobals.lastFragment, value).apply();
    }

    public static String getLastFragment() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(AppGlobals.lastFragment, "");
    }

    public static void saveCategoryStatus(String key, boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getCategoryStatue(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(key, false);
    }

    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static Boolean getBooleanValueFromSharedPrefrence(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(key, false);
    }

    public static String getStringDataFromSharedPreference(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }

    public static void sendRegisterData(String email, String password, String userName,
                                        String phoneNumber, String city, String address)
            throws IOException, JSONException {
//        final File uploadFile = new File(imageUri);
        MultiPartUtility http;
        try {
            http = new MultiPartUtility(new URL(AppGlobals.REGISTER_URL));
            http.addFormField("username", userName);
            http.addFormField("password", password);
            http.addFormField("email", email);
            http.addFormField("address", address);
            http.addFormField("city", city);
            http.addFormField("phone_number", phoneNumber);
//            http.addFilePart("photo", uploadFile);
            final byte[] bytes = http.finish();
//            try {
//                OutputStream os = new FileOutputStream(imageUri);
//                os.write(bytes);
//            } catch (IOException e) {
//
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject readResponse(HttpURLConnection connection)
            throws IOException, JSONException {

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        while((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        return new JSONObject(response.toString());
    }

    public static Bitmap getBitMapOfProfilePic(String selectedImagePath) {
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        return bm;
    }

    public static String userExist(String username)
            throws IOException, JSONException {

        String data = String.format("{\"username\" : \"%s\"}", username);
        HttpURLConnection connection = openConnectionForUrl(AppGlobals.USER_EXIST_URL);
        sendRequestData(connection, data);
        return " ";
    }

    private static HttpURLConnection openConnectionForUrl(String path)
            throws IOException {

        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        return connection;
    }

    private static void sendRequestData(HttpURLConnection connection, String body)
            throws IOException {
        System.out.println(body);

        byte[] outputInBytes = body.getBytes("UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write(outputInBytes);
        os.close();
        AppGlobals.setUserExistResponse(connection.getResponseCode());
        Log.i("USER EXIST", String.valueOf(connection.getResponseCode()));
    }

}
