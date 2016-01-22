package com.byteshaft.auction.register_login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.auction.MainActivity;
import com.byteshaft.auction.R;
import com.byteshaft.auction.fragments.CategoriesFragment;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Login Activity :
 * This class simply include login process
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mRegisterButton;
    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private Button mLoginButton;
    public static ProgressDialog sProgressDialog;
    private TextView forgetPassword;
    private String profilePicUrl = "";
    private static LoginActivity sInstance;
    String[] loginData;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE2 = 1;
    public static LoginActivity getLoginActivityInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sInstance = this;
        setTitle("Login");
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mEditTextUserName = (EditText) findViewById(R.id.username_login);
        mEditTextPassword = (EditText) findViewById(R.id.password_login);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                if (ContextCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                }
                break;
            case R.id.btn_login:
                if (mEditTextUserName.getText().toString().trim().isEmpty()
                        || mEditTextPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields must be filled",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Helpers.containsDigit(mEditTextPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "password must contain 0-9", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mEditTextUserName.getText().toString().trim().isEmpty()
                        || !mEditTextPassword.getText().toString().trim().isEmpty()) {
                    String userName = mEditTextUserName.getText().toString();
                    String password = mEditTextPassword.getText().toString();
                    loginData = new String[]{userName, password};
                    // check for external storage permission before login task
                    if (ContextCompat.checkSelfPermission(LoginActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE2);
                    } else {
                        // if the permission granted execute the login task
                        new LoginTask().execute(loginData);
                    }
                }
                break;
            case R.id.forget_password:
                showCustomDialog();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this code will be executed if the permissions are either denied or given by the user
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied!"
                    , Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new LoginTask().execute(loginData);
                }
            }
        }

    }

    // password recovery dialog
    private void showCustomDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Password Recovery");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 5, 30, 0);
        EditText textBox = new EditText(LoginActivity.this);
        textBox.setHint("Enter Your Email");
        layout.addView(textBox, params);
        alert.setView(layout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
                dialog.dismiss();
            }
        });
        alert.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        MainActivity.getInstance().closeApplication();
    }

    /**
     * Member class
     * Task to send username and password to server and if success it gets back to user data
     * and save user data and profile pic if available
     */
    class LoginTask extends AsyncTask<String, String, ArrayList<Integer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sProgressDialog = new ProgressDialog(LoginActivity.this);
            sProgressDialog.setMessage("Logging in...");
            sProgressDialog.setIndeterminate(false);
            sProgressDialog.setCancelable(false);
            sProgressDialog.show();
        }

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String[] data;
                try {
                    data = Helpers.simpleGetRequest(AppGlobals.LOGIN_URL + params[0] + "/",
                            params[0], params[1]);
                    if (Integer.valueOf(data[0]).equals(HttpURLConnection.HTTP_OK)) {
                        AppGlobals.sCategoriesFragmentForeGround = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new CategoriesFragment.GetCategoriesTask(LoginActivity.this).execute();
                            }
                        });
                        System.out.println(data[1]);
                        JSONObject jsonobject = new JSONObject(data[1]);
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_USERNAME,
                                String.valueOf(jsonobject.get("username")));
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PASSWORD, params[1]);
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL,
                                String.valueOf(jsonobject.get("email")));
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS,
                                String.valueOf(jsonobject.get("address")));
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER,
                                String.valueOf(jsonobject.get("phone_number")));
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_CITY,
                                String.valueOf(jsonobject.get("city")));
                        Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PROFILE_PIC,
                                String.valueOf(jsonobject.get("photo")));
                        getCategoriesAndSave(params[0], params[1]);
                        if (String.valueOf(jsonobject.get("photo")).isEmpty() ||
                                String.valueOf(jsonobject.get("photo")) == null) {
                            // new code goes here.
                        } else {
                            profilePicUrl = String.valueOf(jsonobject.get("photo"));
                            System.out.println(profilePicUrl);
                            Helpers.saveDataToSharedPreferences(AppGlobals.PROFILE_PIC_IMAGE_URL,
                                    profilePicUrl);
                        }
                        arrayList.add(HttpURLConnection.HTTP_OK);
                    } else {
                        arrayList.add(HttpURLConnection.HTTP_FORBIDDEN);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return arrayList;
            }
            arrayList.add(AppGlobals.NO_INTERNET);
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> arrayList) {
            super.onPostExecute(arrayList);
            if (!profilePicUrl.trim().isEmpty()) {
                new DownloadProfilePic().execute(profilePicUrl);
            }
//            System.out.println(arrayList.get(0));
            if (arrayList.get(0).equals(HttpURLConnection.HTTP_OK)) {
                Helpers.userLogin(true);
            } else if (arrayList.get(0).equals(HttpURLConnection.HTTP_FORBIDDEN)) {
                sProgressDialog.dismiss();
                Helpers.alertDialog(LoginActivity.this, "Login Error",
                        AppGlobals.getLoginResponseMessage(), "");
            } else if (arrayList.get(0).equals(AppGlobals.NO_INTERNET)) {
                sProgressDialog.dismiss();
                Helpers.alertDialog(LoginActivity.this, "No Internet", "Internet Not Available", "");
            }
        }
    }

    // class to download image
     public static class DownloadProfilePic extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Bitmap myBitmap;
            myBitmap = Helpers.downloadImage(params[0]);
            if (myBitmap != null) {
                AppGlobals.addBitmapToInternalMemory(myBitmap, AppGlobals.profilePicName,
                        AppGlobals.PROFILE_PIC_FOLDER);
                Helpers.saveBooleanToSharedPreference(AppGlobals.PROFILE_PIC_STATUS, true);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s) {
                Log.i(AppGlobals.getLogTag(getClass()), "Image downloaded");
            }
        }
    }

    private void getCategoriesAndSave(String userName, String password) throws IOException {
        boolean categoriesAvailable = false;
        String[] categoryList = Helpers.simpleGetRequest(AppGlobals.CATEGORY_URL + userName +
                        "/interests/", userName, password);
        if (Integer.valueOf(categoryList[0]) == HttpURLConnection.HTTP_OK) {
            try {
                JSONObject jsonObject = new JSONObject((categoryList[1]));
                String categories = String.valueOf(jsonObject.get("interests"));
                String[] categoriesArray = categories.split(",");
                Set<String> categoriesSet = new HashSet<>();
                for (String category : categoriesArray) {
                    categoriesSet.add(category);
                    if (!category.isEmpty()) {
                        categoriesAvailable = true;
                    }
                }
                Helpers.saveCategories(categoriesSet);
                if (categoriesAvailable) {
                    Helpers.saveBooleanToSharedPreference(AppGlobals.KEY_CATEGORIES_SELECTED, true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
