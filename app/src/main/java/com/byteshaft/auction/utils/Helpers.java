package com.byteshaft.auction.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Base64;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Helpers {

    // get default sharedPreferences.
    private static SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(AppGlobals.getContext());
    }

    // save boolean value for login status of user , takes boolean value as parameter
    public static void userLogin(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(AppGlobals.user_login_key, value).apply();
    }

    // get user login status and manipulate app functions by its returned boolean value
    public static boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(AppGlobals.user_login_key, false);
    }

    /**
     * save the last opened fragment and open that fragment when app runs next time if person has
     * is mostly intrested in buyer than buyer fragment will be opened else seller
     * this only works for seller and buyer not other fragments
     * It takes String value as parameter
     *
     */
    public static void saveLastFragmentOpened(String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(AppGlobals.lastFragment, value).apply();
    }

    // Get the last fragment that user opened return string value
    public static String getLastFragment() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(AppGlobals.lastFragment, "");
    }

    /** Method to save String type data to sharedPreferences Requires String key
     * and value as parameter*
     */
    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    /** Method to save boolean type data to sharedPreferences Requires String key
     * and boolean value as parameter*
     */
    public static void saveBooleanToSharedPreference(String key, boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    // Method to get boolean value from sharedPreference requires key as parameter
    public static Boolean getBooleanValueFromSharedPreference(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(key, false);
    }

    // Method to get String value from sharedPreference requires key as parameter
    public static String getStringDataFromSharedPreference(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }

    /**
     * Void Method to send Data to server
     * Requires email, password, username, city, address, profilePic as parameter
     */
    public static void sendRegisterData(String email, String password, String userName,
                                        String phoneNumber, String city, String address, String imageUri)
            throws IOException, JSONException {
        final File uploadFile = new File(imageUri);
        MultiPartUtility http;
        try {
            http = new MultiPartUtility(new URL(AppGlobals.REGISTER_URL));
            http.addFormField("username", userName);
            http.addFormField("password", password);
            http.addFormField("email", email);
            http.addFormField("address", address);
            http.addFormField("city", city);
            http.addFormField("phone_number", phoneNumber);
            http.addFilePart("photo", uploadFile);
            final byte[] bytes = http.finish();
            try {
                OutputStream os = new FileOutputStream(imageUri);
                os.write(bytes);
            } catch (IOException e) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get bitmap of a image it requires image path as parameter
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

    /**
     * Method to check if user Exist or not
     * @param username
     * @throws IOException
     * @throws JSONException
     */
    public static void userExist(String username)
            throws IOException, JSONException {
        HttpURLConnection connection =
                openConnectionForUrl(AppGlobals.USER_EXIST_URL + username +"/"+ "exists", "GET");
        AppGlobals.setUserExistResponse(connection.getResponseCode());
    }

    /**
     * Methof to
     * @param targetUrl
     * @param method etc "POST", "GET"
     * @return
     * @throws IOException
     */
    private static HttpURLConnection openConnectionForUrl(String targetUrl, String method)
            throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod(method);
        return connection;
    }

    /**
     * Method to check if password contains digit or not
     * @param s
     * @return
     */
    public static final boolean containsDigit(String s) {
        boolean containsDigit = false;

        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
        }
        return containsDigit;
    }

    /**
     * Method for the user Login
     * @param userName
     * @param password
     * @return String[] which include user details
     * @throws IOException
     */
    public static String[] loginProcess(String userName, String password)
            throws IOException {
        String parsedString = "";
        URL url = new URL(AppGlobals.LOGIN_URL+ userName + "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        String authString = userName + ":" + password;
        System.out.println("auth string: " + authString);
        String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
        System.out.println("Base64 encoded auth string: " + authStringEncoded);
        connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = connection.getInputStream();
            parsedString = convertInputStreamToString(is);
            return new String[]{String.valueOf(connection.getResponseCode()), parsedString};
        }else {
            return new String[]{String.valueOf(connection.getResponseCode()), parsedString};
        }

    }

    /**
     * convert the InputStream to String, Basically its a JsonObject than we can get user details
     * @param is
     * @return
     * @throws IOException
     */
    public static String convertInputStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * Just an Alert Dialog
     * @param activity
     * @param title
     * @param msg
     */
    public static void alertDialog(final Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Saves the Set of categories selected by user
     * @param stringSet
     */
    public static void saveCategories(Set<String> stringSet) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putStringSet(AppGlobals.SELECTED_CATEGORIES, stringSet).apply();
    }

    // Returns the set of categories selected by user
    public static Set<String> getCategories() {
        Set<String> set = new HashSet<>();
        set.add("nothing");
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getStringSet(AppGlobals.SELECTED_CATEGORIES, set);
    }

    // Check if network is available
    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // ping the google server to check if internet is really working or not
    public static boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
}
