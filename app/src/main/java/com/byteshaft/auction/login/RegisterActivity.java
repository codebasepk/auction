package com.byteshaft.auction.login;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserNameEditText;
    private static final int REQUEST_CAMERA = 1212;
    private static final int SELECT_FILE = 1245;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mRegisterButton;
    private ImageButton dpButton;
    private String[] registrationData;
    private EditText mPhoneNumber;
    private EditText mAddress;
    private EditText mCity;
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_fragment);
        setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUserNameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_login);
        mPhoneNumber = (EditText) findViewById(R.id.user_phone);
        mAddress = (EditText) findViewById(R.id.user_address);
        mCity = (EditText) findViewById(R.id.user_city);
        mRegisterButton = (Button) findViewById(R.id.btn_send);
        dpButton = (ImageButton) findViewById(R.id.button_dp);
        dpButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                registrationData = new String[3];
                if (mUserNameEditText.getText().toString().trim().isEmpty() || mPhoneNumber.getText()
                        .toString().trim().isEmpty() || mAddress.getText().toString().trim().isEmpty()
                        || mCity.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AppGlobals.getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean validEmail = isValidEmail(mEmailEditText.getText().toString());
                if (!validEmail) {
                    Toast.makeText(AppGlobals.getContext(), "please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!containsDigit(mPasswordEditText.getText().toString())) {
                    Toast.makeText(AppGlobals.getContext(), "password must contain 0-9", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPasswordEditText.getText().toString().length() < 6) {
                    Toast.makeText(AppGlobals.getContext(), "password must contain 6 character", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] data = {mUserNameEditText.getText().toString(),
                        mPasswordEditText.getText().toString()};
                new RegistrationTask().execute(data);
                break;
            case R.id.button_dp:
                selectImage();
                break;
        }

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final boolean containsDigit(String s) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class RegistrationTask extends AsyncTask<String, String, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(RegisterActivity.this);
            mProgressDialog.setMessage("Registering");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            boolean response = false;
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(params[0]);
            arrayList.add(params[1]);
            final ParseUser user = new ParseUser();
            user.setPassword(arrayList.get(1));
            user.put("username", arrayList.get(0));
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", arrayList.get(0));
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> parseUsers, ParseException e) {
                    if (e == null) {
                        // Successful Query
                        // User already exists ? then login
                        if (parseUsers.size() > 0) {
                            System.out.println("already present please login");
//                            loginUser(arrayList.get(0), arrayList.get(1));
                        } else {
                            // No user found, so signup
                            signupUser(user);
                        }
                    } else {
                        // Shit happened!
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle("Oops!")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            mProgressDialog.dismiss();
        }

        private void loginUser(String username, String password) {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        // Hooray! The user is logged in.
                        System.out.println("Login");

                    } else {
                        // Login failed!
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle("Oops!")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }

        private void signupUser(ParseUser user) {
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Signup successful!
                        System.out.println("success");
                    } else {
                        // Fail!
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle("Oops!")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }

    private void selectImage() {
        
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Remove photo", "Cancel" };
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
                    System.out.println("worked");
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
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dpButton.setImageBitmap(thumbnail);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
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
                dpButton.setImageBitmap(bm);
            }
        }
    }
}
