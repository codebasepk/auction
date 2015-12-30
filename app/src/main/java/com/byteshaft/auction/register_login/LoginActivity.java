package com.byteshaft.auction.register_login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Login Activity :
 * This class simply include login process
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mRegisterButton;
    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;
    private TextView forgetPassword;
    private String profilePicUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
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
                    String[] loginData = {userName, password};
                    new LoginTask().execute(loginData);
                }
                break;
            case R.id.forget_password:
                showCustomDialog();

                break;
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

    /**Member class
     * Task to send username and password to server and if success it gets back to user data
     * and save user data and profile pic if available
     */
    class LoginTask extends AsyncTask<String, String, ArrayList<Integer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage("Logging in...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            if (Helpers.isNetworkAvailable(getApplicationContext()) && Helpers.isInternetWorking()) {
                String[] data;
                try {
                    data = Helpers.loginProcess(params[0], params[1]);
                    System.out.println(data[0]);
                    if (Integer.valueOf(data[0]).equals(HttpURLConnection.HTTP_OK)) {
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
                        if (String.valueOf(jsonobject.get("photo")).isEmpty() ||
                                String.valueOf(jsonobject.get("photo")) == null) {
                           // new code goes here.
                        } else {
                            profilePicUrl = String.valueOf(jsonobject.get("photo"));
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
            mProgressDialog.dismiss();
            if (arrayList.get(0).equals(HttpURLConnection.HTTP_OK)) {
                Helpers.userLogin(true);
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else if (arrayList.get(0).equals(HttpURLConnection.HTTP_FORBIDDEN)) {
                Helpers.alertDialog(LoginActivity.this, "Authentication Error",
                        "Username or password is incorrect");
            } else if (arrayList.get(0).equals(AppGlobals.NO_INTERNET)) {
                Helpers.alertDialog(LoginActivity.this, "No Internet", "Internet Not Available");
            }
        }
    }

    // class to download image
    class DownloadProfilePic extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            System.out.println(params[0]);
            Bitmap myBitmap = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                System.out.println(connection.getResponseCode());
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
            if (myBitmap != null) {
                AppGlobals.addBitmapToInternalMemory(myBitmap);
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

}
