package com.byteshaft.auction.utils;

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
    public static final String SELECTED_CATEGORIES = "selected_category";
    public static final String detail = "detail";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_CATEGORIES_SELECTED = "categories_selected";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY = "city";
    public static boolean loginSuccessFull = false;
    public static String cacheSaveLocationForProfilePic = "profile_pic";
    public static String KEY_PROFILE_PIC = "profile_pic";
    public static String PROFILE_PIC_STATUS = "profile_pic_status";
    public static int responseCode;
    public static int userExistResponse;
    public static int NO_INTERNET = 2112;
    public static final String LOGIN_URL = (
            "http://testapp-byteshaft.herokuapp.com/api/users/"
    );
    public static final String USER_EXIST_URL = ("http://testapp-byteshaft.herokuapp.com/api/users/");
    public static final String REGISTER_URL = (
            "http://testapp-byteshaft.herokuapp.com/api/register"
    );

    public static final String CATEGORY_URL = "http://testapp-byteshaft.herokuapp.com/users/";
    private static String root;
    private static String profilePicName = "profile_pic.png";

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        root = Environment.getExternalStorageDirectory().toString()
                +"/Android/data/" + getPackageName()+ "/profilePic";

    }

    public static void setUserExistResponse(int value) {
        userExistResponse = value;
    }

    public static int getUserExistResponse() {
        return userExistResponse;
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

    public static void addBitmapToMemoryCache(Bitmap bitmap) {
        File myDir = new File(root);
        File file;
        myDir.mkdirs();
        file = new File(myDir +"/", profilePicName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getProfilePicBitMap() throws FileNotFoundException {
        File file = new File(root,profilePicName);
        if (file.exists()) {
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } else {
            return null;
        }
    }
}
