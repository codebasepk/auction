package com.byteshaft.auction.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.byteshaft.auction.utils.MultiPartUtility;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/*
class for user profile which will allow the user to update his/her information
 */
public class UserSettingFragment extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private EditText mUserName;
    private EditText mUserEmail;
    private EditText mUserCurrentPassword;
    private EditText mPhoneNumber;
    private EditText mAddress;
    private EditText mCity;
    private EditText mNewPassword;
    private Button mButtonDone;
    private boolean emailChanged = false;
    private boolean passWordChanged = false;
    private boolean addressChanged = false;
    private boolean cityChanged = false;
    private boolean phoneNumberChanged = false;
    private CircularImageView profilePicImageView;
    private static final int REQUEST_CAMERA = 1212;
    private static final int SELECT_FILE = 1245;
    private boolean profilePictureChanged = false;
    private File destination;
    private String imageUrl = "";
    private Uri selectedImageUri;
    private Bitmap profilePic;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.user_setting_fragment, container, false);
        mUserName = (EditText) mBaseView.findViewById(R.id.user_name);
        mUserEmail = (EditText) mBaseView.findViewById(R.id.user_email);
        mUserCurrentPassword = (EditText) mBaseView.findViewById(R.id.current_password);
        mNewPassword = (EditText) mBaseView.findViewById(R.id.new_password);
        mPhoneNumber = (EditText) mBaseView.findViewById(R.id.user_phone);
        mAddress = (EditText) mBaseView.findViewById(R.id.user_address);
        mCity = (EditText) mBaseView.findViewById(R.id.user_city);
        mButtonDone = (Button) mBaseView.findViewById(R.id.btn_done);
        profilePicImageView = (CircularImageView) mBaseView.findViewById(R.id.profilePic);
        mButtonDone.setOnClickListener(this);
        mButtonDone.setVisibility(View.GONE);
        getValuesFromSharedPreference();
        mUserEmail.addTextChangedListener(new TextWatcher() {
            private boolean textChanged = false;
            private String textBeforeChanged;
            private String textAfterChanged;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println(s.toString());
                textBeforeChanged = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s.toString());
                textChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(s.toString());
                textAfterChanged = s.toString();
                if (!textBeforeChanged.equals(textAfterChanged) &&!s.toString().isEmpty()
                        && textChanged) {
                    emailChanged = true;
                } else {
                    emailChanged = false;
                }
            }
        });
        mNewPassword.addTextChangedListener(new TextWatcher() {
            private boolean textChanged = false;
            private String textBeforeChanged;
            private String textAfterChanged;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBeforeChanged = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                textAfterChanged = s.toString();
                if (!textBeforeChanged.equals(textAfterChanged) &&!s.toString().isEmpty()
                        && textChanged) {
                    passWordChanged = true;
                } else {
                    passWordChanged = false;
                }
            }
        });
        mAddress.addTextChangedListener(new TextWatcher() {
            private boolean textChanged = false;
            private String textBeforeChanged;
            private String textAfterChanged;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBeforeChanged = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                textAfterChanged = s.toString();
                if (!textBeforeChanged.equals(textAfterChanged) &&!s.toString().isEmpty()
                        && textChanged) {
                    addressChanged = true;
                } else {
                    addressChanged = false;
                }
            }
        });
        mCity.addTextChangedListener(new TextWatcher() {
            private boolean textChanged = false;
            private String textBeforeChanged;
            private String textAfterChanged;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBeforeChanged = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                textAfterChanged = s.toString();
                if (!textBeforeChanged.equals(textAfterChanged) &&!s.toString().isEmpty()
                        && textChanged) {
                    cityChanged = true;
                } else {
                    cityChanged = false;
                }
            }
        });
        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            private boolean textChanged = false;
            private String textBeforeChanged;
            private String textAfterChanged;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBeforeChanged = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                textAfterChanged = s.toString();
                if (!textBeforeChanged.equals(textAfterChanged) && !s.toString().isEmpty()
                        && textChanged) {
                    phoneNumberChanged = true;
                } else {
                    phoneNumberChanged = false;
                }
            }
        });
        try {
            if (AppGlobals.getProfilePicBitMap() != null) {
                profilePicImageView.setImageBitmap(AppGlobals.getProfilePicBitMap());
            } else {
                if (Helpers.isUserLoggedIn()) {
                    profilePicImageView.setImageBitmap(Helpers.getBitMapWithNameCharacter());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setHasOptionsMenu(true);
        profilePicImageView.setOnClickListener(this);
        return mBaseView;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Remove photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    profilePictureChanged = true;
                    profilePicImageView.setImageBitmap(Helpers.getBitMapWithNameCharacter());
                }
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
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
                profilePicImageView.setImageBitmap(thumbnail);
                profilePictureChanged = true;
            } else if (requestCode == SELECT_FILE) {
                selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                profilePic = Helpers.getBitMapOfProfilePic(selectedImagePath);
                profilePicImageView.setImageBitmap(profilePic);
                imageUrl = String.valueOf(selectedImagePath);
                profilePictureChanged = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                System.out.println(emailChanged);
                System.out.println(passWordChanged);
                System.out.println(cityChanged);
                System.out.println(addressChanged);
                System.out.println(profilePictureChanged);
                System.out.println(phoneNumberChanged);
                if (passWordChanged && !Helpers.containsDigit(mNewPassword.getText().toString())) {
                    Toast.makeText(getActivity(), "password must contain digit", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (emailChanged || passWordChanged || cityChanged || addressChanged ||
                        profilePictureChanged || phoneNumberChanged) {
                    String password;
                    if (passWordChanged) {
                        password = mNewPassword.getText().toString();
                    } else {
                        password = mUserCurrentPassword.getText().toString();
                    }
                    String[] valueToUpdate = {mUserEmail.getText().toString(),
                            mAddress.getText().toString(), mCity.getText().toString(),
                            mPhoneNumber.getText().toString(), password, imageUrl};
                    new UpdateUserDetailsTask().execute(valueToUpdate);
                }
                break;
            case R.id.profilePic:
                selectImage();
                break;
        }
    }

    public void getValuesFromSharedPreference() {
        if (Helpers.isUserLoggedIn()) {
            mUserName.setText(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME));
            mUserName.setEnabled(false);
            mUserEmail.setText(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_EMAIL));
            mUserEmail.setEnabled(false);
            mUserCurrentPassword.setText(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD));
            mUserCurrentPassword.setEnabled(false);
            mNewPassword.setVisibility(View.GONE);
            mNewPassword.setEnabled(false);
            mPhoneNumber.setText(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PHONE_NUMBER));
            mPhoneNumber.setEnabled(false);
            mAddress.setText(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_ADDRESS));
            mAddress.setEnabled(false);
            mCity.setText(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_CITY));
            mCity.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_profile_button:
                mUserEmail.setEnabled(true);
                mUserEmail.setSelection(mUserEmail.getText().length());
                mUserCurrentPassword.setEnabled(true);
                mUserCurrentPassword.setSelection(mUserCurrentPassword.getText().length());
                mNewPassword.setEnabled(true);
                mNewPassword.setSelection(mNewPassword.getText().length());
                mPhoneNumber.setEnabled(true);
                mPhoneNumber.setSelection(mPhoneNumber.getText().length());
                mAddress.setEnabled(true);
                mAddress.setSelection(mAddress.getText().length());
                mCity.setEnabled(true);
                mCity.setSelection(mCity.getText().length());
                mButtonDone.setEnabled(true);
                mButtonDone.setVisibility(View.VISIBLE);
                mNewPassword.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_user_profile, menu);
    }

    class UpdateUserDetailsTask extends AsyncTask<String, String, String[]> {
        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Updating...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                MultiPartUtility http;
                String username = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                try {
                    http = new MultiPartUtility(new URL(AppGlobals.UPDATE_USER_DETAILS_URL +
                            username + "/"), "PUT", username, password);
                    http.addFormField("username", username);
                    http.addFormField("email", params[0]);
                    http.addFormField("address", params[1]);
                    http.addFormField("city", params[2]);
                    http.addFormField("phone_number", params[3]);
                    http.addFormField("password", params[4]);
                    if (profilePictureChanged && !params[5].trim().isEmpty()) {
                        http.addFilePart("photo", new File(params[5]));
                    }
                    final byte[] bytes = http.finishFilesUpload();
                    try {
                        OutputStream os = new FileOutputStream(params[5]);
                        os.write(bytes);
                    } catch (IOException e) {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new String[]{params[0], params[4], params[3], params[2], params[1]};
            } else {
                noInternet = true;
                return new String[]{String.valueOf(AppGlobals.NO_INTERNET)};
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            mProgressDialog.dismiss();
            if (noInternet) {
                if (Integer.valueOf(strings[0]) == AppGlobals.NO_INTERNET) {
                    Helpers.alertDialog(getActivity(), "No internet", "No internet available");
                    return;
                }
            }
            if (AppGlobals.getPostResponse() == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, strings[0]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PASSWORD, strings[1]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER, strings[2]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_CITY, strings[3]);
                Helpers.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, strings[4]);
                if (profilePic == null) {
                } else {
                    if (profilePictureChanged) {
                        AppGlobals.addBitmapToInternalMemory(profilePic, AppGlobals.profilePicName,
                                AppGlobals.PROFILE_PIC_FOLDER);
                    }
                }
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.container, new Buy());
                tx.commit();
            }
        }
    }
}
