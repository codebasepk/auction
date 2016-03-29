package com.byteshaft.auction.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;

import com.byteshaft.auction.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import java.util.Random;
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

    /**
     * Method to save String type data to sharedPreferences Requires String key
     * and value as parameter*
     */
    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * Method to save boolean type data to sharedPreferences Requires String key
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
            System.out.println(address);
            http.addFormField("address", address);
            http.addFormField("city", city);
            http.addFormField("phone_number", phoneNumber);
            if (!imageUri.trim().isEmpty()) {
                http.addFilePart("photo", uploadFile);
            }
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
     *
     * @param username
     * @throws IOException
     * @throws JSONException
     */
    public static void userExist(String username)
            throws IOException, JSONException {
        HttpURLConnection connection =
                openConnectionForUrl(AppGlobals.USER_EXIST_URL + username + "/" + "exists", "GET");
        AppGlobals.setUserExistResponse(connection.getResponseCode());
    }

    /**
     * Methof to
     *
     * @param targetUrl
     * @param method    etc "POST", "GET"
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnectionForUrl(String targetUrl, String method)
            throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod(method);
        return connection;
    }

    /**
     * Method to check if password contains digit or not
     *
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
     * Method for the simeple get request here i am using this for login, get interest.
     *
     * @param userName
     * @param password
     * @return String[] which include user details
     * @throws IOException
     */
    public static String[] simpleGetRequest(String link, String userName, String password)
            throws IOException {
        String parsedString;
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        String authString = userName + ":" + password;
        String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
        connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = connection.getInputStream();
            parsedString = convertInputStreamToString(is);
            return new String[]{String.valueOf(connection.getResponseCode()), parsedString};
        } else {
            InputStream is = connection.getErrorStream();
            parsedString = convertInputStreamToString(is);
            System.out.println(parsedString);
            if (!parsedString.contains("Forbidden")) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(parsedString).getAsJsonObject();
                AppGlobals.setLoginResponseMessage(jsonObject.get("detail").getAsString());
            }
            return new String[]{String.valueOf(connection.getResponseCode()), parsedString};
        }

    }

    /**
     * convert the InputStream to String, Basically its a JsonObject than we can get user details
     *
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
     *
     * @param activity
     * @param title
     * @param msg
     */
    public static void alertDialog(final Activity activity, String title, String msg) {
        final Intent intent = new Intent();
        final String action = "";
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
     *
     * @param stringSet
     */
    public static void saveCategories(Set<String> stringSet) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putStringSet(AppGlobals.SELECTED_CATEGORIES, stringSet).commit();
    }

    // Returns the set of categories selected by user
    public static Set<String> getCategories() {
        Set<String> set = new HashSet<>();
        set.add("nothing");
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getStringSet(AppGlobals.SELECTED_CATEGORIES, set);
    }

    // Check if network is available
    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                AppGlobals.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static String getValidatedUrl(String url) {
        if (!URLUtil.isValidUrl(url) || !Patterns.WEB_URL.matcher(url).matches()) {
            url = AppGlobals.BASE_URL + url;
        }
        return url;
    }

    public static Bitmap downloadImage(String link) {
        Bitmap myBitmap = null;
        try {
            URL url = new URL(getValidatedUrl(link));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            try {
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);

            } catch (Exception e) {
                e.fillInStackTrace();
                Log.v("ERROR", "Errorchence : " + e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myBitmap;
    }

    //method to remove user information when logout
    public static void removeUserData() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().clear().commit();
        File file = new File(AppGlobals.root);
        if (file.exists()) {
            File[] folders = file.listFiles();
            for (File folder : folders) {
                if (folder.exists()) {
                    removeFiles(folder.getAbsolutePath());
                }
            }
        }

    }

    public static void removeFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveStringSet(String key, Set<String> value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putStringSet(key, value).apply();
    }

    public static Set<String> getSavedStringSet(String key) {
        Set<String> set = new HashSet<>();
        set.add("nothing");
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getStringSet(key, set);
    }

    public static int getCategoriesImagesCount() {
        File file = new File(AppGlobals.root + AppGlobals.CATEGORIES_FOLDER);
        File[] files = file.listFiles();
        return files.length;
    }

    public static void authPostRequest(String link, String key, String value) throws IOException {
        URL url;
        url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        String authString = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME)
                + ":" + Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
        String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
        connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
        String jsonFormattedData = getJsonObjectString(key, value);
        sendRequestData(connection, jsonFormattedData);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Log.i("Log", "connection:" + connection.getResponseCode());
        } else if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
            Log.i("Log", "created" + connection.getResponseCode());
            AppGlobals.setPostBidResponse(connection.getResponseCode());
        }
        AppGlobals.setPostBidResponse(connection.getResponseCode());
    }

    private static String getJsonObjectString(String key, String pushKey) {
        return String.format("{\"%s\": \"%s\"}", key, pushKey);
    }

    public static void sendRequestData(HttpURLConnection connection, String body) throws IOException {
        byte[] outputInBytes = body.getBytes("UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write(outputInBytes);
        os.close();
    }

    public static Bitmap getBitMapWithNameCharacter() {
//        final Resources res = AppGlobals.getContext().getResources();
        int[] array = AppGlobals.getContext().getResources().getIntArray(R.array.letter_tile_colors);
        final BitmapWithCharacter tileProvider = new BitmapWithCharacter();
        final Bitmap letterTile = tileProvider.getLetterTile(Helpers.
                        getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME),
                String.valueOf(array[new Random().nextInt(array.length)]), 100, 100);
        return letterTile;
    }

    public static int simpleDeleteRequest(String link)
            throws IOException {
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        String authString = getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME)
                + ":" + getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
        String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
        connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
        connection.setRequestMethod("DELETE");
        return connection.getResponseCode();
    }

    public static int simplePutRequest(String link, String key, String value)
            throws IOException {
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        String authString = getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME)
                + ":" + getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
        String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
        connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
        connection.setRequestMethod("PUT");
        String jsonFormattedData = getJsonObjectString(key, value);
        sendRequestData(connection, jsonFormattedData);
        return connection.getResponseCode();
    }

    public static void saveBooleanForReview(int key, boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(String.valueOf(key), value).apply();
    }

    // Method to get boolean value from sharedPreference requires key as parameter
    public static Boolean getBooleanValueForReview(int key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(String.valueOf(key), false);
    }
}
