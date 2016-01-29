package com.byteshaft.auction.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class AppGlobals extends Application{

    private static Context sContext;
    public static final String user_login_key = "user_login";
    public static final String lastFragment = "last_fragment";

    public static final String CATEGORY_INTENT_KEY = "category_intent_key";
    public static final String ALL_CATEGORIES = "all_categories";
    public static final String KEY_SELECTED_CATEGORY_BOOLEAN_STATUS = "selected_category_boolean_status";
    public static final String KEY_CATEGORY_BOOLEAN_STATUS = "category_boolean_status";
    public static final String SELECTED_CATEGORIES = "selected_categories";

    public static final String detail = "detail";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY = "city";
    public static boolean loginSuccessFull = false;
    public static String KEY_PROFILE_PIC = "profile_pic";
    public static String PROFILE_PIC_STATUS = "profile_pic_status";
    public static int responseCode;
    public static int userExistResponse;
    public static int NO_INTERNET = 2112;
    public static final String BASE_URL = "http://46.101.75.194:8000";
    public static final String LOGIN_URL = String.format("%s/api/users/", BASE_URL);
    public static final String USER_EXIST_URL = String.format("%s/api/users/", BASE_URL);
    public static final String REGISTER_URL = String.format("%s/api/register", BASE_URL);
    public static int postResponse;
    public static final String CATEGORY_URL = String.format("%s/api/users/", BASE_URL);
    public static String root;
    public static String profilePicName = "profile_pic.png";
    public static final String POST_AD_URL = String.format("%s/api/users/", BASE_URL);
    public static final String ALL_CATEGORIES_URL = String.format("%s/api/ads/categories", BASE_URL);
    public static final String PUSH_NOTIFICATION_KEY = String.format("%s/api/users/", BASE_URL);
    public static final String SELECTED_CATEGORY_DETAIL_URL = String.format("%s/api/ads/?category=",BASE_URL);
    public static final String PROFILE_PIC_FOLDER = "/profilePic";
    public static final String CATEGORIES_FOLDER = "/categories_folder";
    public static final String CATEGORIES_IMAGES_SAVED = "category_images_saved";
    public static final String PROFILE_PIC_IMAGE_URL = "profile_pic_image";
    public static boolean alertDialogShownOneTimeForCategory = false;
    public static boolean sCategoriesFragmentForeGround = true;
    public static boolean sRegisterProcess = false;
    public static final String USER_SPECIFIC_ADS = String.format("%s/api/users/", BASE_URL);
    public static final String USER_SPECIFIC_ADS_APPEND = "/ads/list";
    public static final String SINGLE_AD_DETAILS = String.format("%s/api/users/", BASE_URL);
    public static final String SINGLE_AD_DETAILS_APPEND_END = "ads/";
    public static final String SINGLE_PRODUCT_NAME = "product_name";
    public static String loginResponseMessage;
    public static final String POST_BID_URL = String.format("%s/api/users/",BASE_URL);
    public static final String GET_SPECIFIC_BIDS = String.format("%s/api/users/", BASE_URL);
    public static int postBidResponse = 0;
    public static final String GET_USER_SPECIFIC_BIDS = String.format("%s/api/users/", BASE_URL);
    public static Activity sCurrentActivity;
    public static final String SEARCH_URL = String.format("%s/api/ads/?category=", BASE_URL);
    public static final String UPDATE_USER_DETAILS_URL = String.format("%s/api/users/",BASE_URL);
    public static final String DELETE_UPDATE_BID_URL =  String.format("%s/api/users/", BASE_URL);


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        root = Environment.getExternalStorageDirectory().toString()
                +"/Android/data/" + getPackageName();
    }

    public static void setPostBidResponse(int value) {
        postBidResponse = value;
    }

    public static int getPostBidResponse() {
        return postBidResponse;
    }

    public static Activity getCurrentActivity(){
        return sCurrentActivity;
    }
    public static void setCurrentActivity(Activity activity){
        sCurrentActivity = activity;
    }

    public static void setLoginResponseMessage(String responseMessage) {
        loginResponseMessage = responseMessage;
    }

    public static String getLoginResponseMessage() {
        return loginResponseMessage;
    }

    // Globally set the value for userExistResponse it takes integer value as parameter
    public static void setUserExistResponse(int value) {
        userExistResponse = value;
    }

    // Get the value of UserExist where needed it returns integer value
    public static int getUserExistResponse() {
        return userExistResponse;
    }
    //Globally setResponse while posting data to server
    public static void setResponseCode(int value) {
        responseCode = value;
    }

    // get posting request response
    public static int getResponseCode() {
        return responseCode;
    }

    // Method to get LogTag globally it takes class as a parameter
    public static String getLogTag(Class aClass) {
        return aClass.getName();
    }

    // Method to get context when need specially outside fragment or activity
    public static Context getContext() {
        return sContext;
    }

    // Method to save bitmap to internal storage this method takes bitmap as parameter
    public static void addBitmapToInternalMemory(Bitmap bitmap, String name, String folder) {
        File myDir = new File(root + folder);
        File file;
        myDir.mkdirs();
        file = new File(myDir +"/", name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("saved in internal memory");
    }

    // get profile pic as bitmap when needed
    public static Bitmap getProfilePicBitMap() throws FileNotFoundException {
        File file = new File(root + PROFILE_PIC_FOLDER,profilePicName);
        if (file.exists()) {
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } else {
            return null;
        }
    }

    public static void setPostResponse(int value) {
        postResponse = value;
    }

    public static int getPostResponse() {
        return postResponse;
    }
}
