package com.byteshaft.auction.register_login;


import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.fragments.CategoriesFragment;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Register Activity:
 * This activity will allow user to register on server.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserNameEditText;
    private static final int REQUEST_CAMERA = 1212;
    private static final int SELECT_FILE = 1245;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfPasswordEditText;
    private Button mRegisterButton;
    private boolean mPasswordMatched = false;
    private ImageButton dpButton;
    private String[] registrationData;
    private EditText mPhoneNumber;
    private EditText mAddress;
    private EditText mCity;
    public static ProgressDialog sRegisterProgressDialog;
    private boolean userAlreadyExists = false;
    private Uri selectedImageUri;
    private String imageUrl = "";
    private File destination;
    private Bitmap profilePic;
    private boolean userNotExist = false;
    private static RegisterActivity sRegisterActivityInstance;

    public static RegisterActivity getInstance() {
        return sRegisterActivityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        setTitle("Register");
        sRegisterActivityInstance = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUserNameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_login);
        mConfPasswordEditText = (EditText) findViewById(R.id.password_cof);
        mPhoneNumber = (EditText) findViewById(R.id.user_phone);
        mAddress = (EditText) findViewById(R.id.user_address);
        mCity = (EditText) findViewById(R.id.user_city);
        mRegisterButton = (Button) findViewById(R.id.btn_send);
        dpButton = (ImageButton) findViewById(R.id.button_dp);
        dpButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mConfPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mPasswordMatched = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPasswordEditText.getText().toString().equals
                        (mConfPasswordEditText.getText().toString())) {
                    mConfPasswordEditText.setCompoundDrawables(null, null, null, null);
                } else {
                    mConfPasswordEditText.setError("password does not match");
                    mPasswordMatched = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mPasswordEditText.getText().toString().equals
                        (mConfPasswordEditText.getText().toString())) {
                    Drawable drawable = getResources().getDrawable(R.drawable.tick);
                    drawable.setBounds(0, 0, 20, 20);
                    mConfPasswordEditText.setCompoundDrawables(null, null, drawable, null);
                    mPasswordMatched = true;
                }
            }
        });
        mUserNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!mUserNameEditText.getText().toString().trim().isEmpty()) {
                        new CheckUserExistTask().execute(mUserNameEditText.getText().toString());
                    }
                }
            }
        });
        mUserNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserNameEditText.setCompoundDrawables(null, null, null, null);
                userNotExist = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                registrationData = new String[3];
                if (mUserNameEditText.getText().toString().trim().isEmpty() || mPhoneNumber.getText()
                        .toString().trim().isEmpty() || mAddress.getText().toString().trim().isEmpty()
                        || mCity.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mUserNameEditText.getText().toString().contains(" ")) {
                    Toast.makeText(getApplicationContext(), "username should not contain any spaces",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean validEmail = isValidEmail(mEmailEditText.getText().toString());
                if (!validEmail) {
                    Toast.makeText(getApplicationContext(), "please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Helpers.containsDigit(mPasswordEditText.getText().toString()) ||
                        !Helpers.containsDigit(mConfPasswordEditText.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "password must contain 0-9", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mConfPasswordEditText.getText().toString().isEmpty()) {
                    if (mPasswordEditText.getText().toString().equals
                            (mConfPasswordEditText.getText().toString())) {
                        mPasswordMatched = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "password does not match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (mPasswordEditText.getText().toString().length() < 6 &&
                        mConfPasswordEditText.getText().toString().length() < 6) {
                    Toast.makeText(getApplicationContext(), "password must contain at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!userAlreadyExists && userNotExist && mPasswordMatched) {
                    String[] data = {mEmailEditText.getText().toString(),
                            mPasswordEditText.getText().toString(),
                            mUserNameEditText.getText().toString(),
                            mPhoneNumber.getText().toString(), mCity.getText().toString(),
                            mAddress.getText().toString(), imageUrl};
                    new RegistrationTask().execute(data);
                }
                break;
            case R.id.button_dp:
                selectImage();
                break;
        }

    }

    // Method to check if email is valid or not.
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Dialog with option to capture image or choose from gallery
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Remove photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                } else if (items[item].equals("Remove photo")) {
                    dpButton.setImageDrawable(null);
                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                imageUrl = destination.getAbsolutePath();
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profilePic = Helpers.getBitMapOfProfilePic(destination.getAbsolutePath());
                dpButton.setImageBitmap(thumbnail);
            } else if (requestCode == SELECT_FILE) {
                selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                profilePic = Helpers.getBitMapOfProfilePic(selectedImagePath);
                dpButton.setImageBitmap(profilePic);
                imageUrl = String.valueOf(selectedImagePath);
            }
        }
    }

    // Member class to send registration data to server and check
    class RegistrationTask extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sRegisterProgressDialog = new ProgressDialog(RegisterActivity.this);
            sRegisterProgressDialog.setMessage("Registering...");
            sRegisterProgressDialog.setIndeterminate(false);
            sRegisterProgressDialog.setCancelable(false);
            sRegisterProgressDialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                    Helpers.sendRegisterData(params[0], params[1], params[2], params[3],
                            params[4], params[5], params[6]);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return new String[]{params[0], params[1], params[2], params[3], params[4], params[5]};
            }
            return new String[]{String.valueOf(AppGlobals.NO_INTERNET)};
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            Log.i(AppGlobals.getLogTag(AppGlobals.getContext().getClass()),
                    String.valueOf(AppGlobals.getResponseCode()));

            if (Integer.valueOf(AppGlobals.getResponseCode()).equals(201)) {
                AppGlobals.sRegisterProcess = true;
                Helpers.userLogin(true);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, result[0]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PASSWORD, result[1]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_USERNAME, result[2]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER, result[3]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_CITY, result[4]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, result[5]);
                if (profilePic == null) {
                } else {
                    AppGlobals.addBitmapToInternalMemory(profilePic, AppGlobals.profilePicName,
                            AppGlobals.PROFILE_PIC_FOLDER);
                }
                AppGlobals.sCategoriesFragmentForeGround = false;
                new CategoriesFragment.GetCategoriesTask(RegisterActivity.this).execute();
            } else if (AppGlobals.getResponseCode() == 0) {
                sRegisterProgressDialog.dismiss();
                Helpers.alertDialog(RegisterActivity.this, "Request Failed", "There was an issue, " +
                        "Please try again");
            } else if (Integer.valueOf(result[0]).equals(AppGlobals.NO_INTERNET)) {
                sRegisterProgressDialog.dismiss();
                Helpers.alertDialog(RegisterActivity.this, "No Internet", "Internet Not Available");
            } else if (AppGlobals.getResponseCode() == 500) {
                sRegisterProgressDialog.dismiss();
                Helpers.alertDialog(RegisterActivity.this, "500", "Internal server error, try after few moments");
            }
        }
    }

    // Member background task to check if user exist.
    class CheckUserExistTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                    Helpers.userExist(params[0]);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return AppGlobals.getUserExistResponse();
            }
            return AppGlobals.NO_INTERNET;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 404) {
                Drawable drawable = getResources().getDrawable(R.drawable.tick);
                drawable.setBounds(0, 0, 20, 20);
                mUserNameEditText.setCompoundDrawables(null, null, drawable, null);
                userNotExist = true;
            } else if (integer == 200) {
                mUserNameEditText.setError("Username already exist");
            } else if (integer == AppGlobals.NO_INTERNET) {
                Helpers.alertDialog(RegisterActivity.this, "No Internet", "Internet Not Available");
            }
        }
    }
}
