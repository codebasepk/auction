package com.byteshaft.auction.login;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mRegisterButton;
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
}
