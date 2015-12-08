package com.byteshaft.auction.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.auction.MainActivity;
import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mRegisterButton;
    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setTitle("Login");
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mEditTextUserName = (EditText) findViewById(R.id.username_login);
        mEditTextPassword = (EditText) findViewById(R.id.password_login);
        mLoginButton = (Button) findViewById(R.id.btn_login);
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
                if (!mEditTextUserName.getText().toString().trim().isEmpty()
                        || !mEditTextPassword.getText().toString().trim().isEmpty()) {
                    String userName = mEditTextUserName.getText().toString();
                    String password = mEditTextPassword.getText().toString();
                    String[] loginData = {userName, password};
                    new LoginTask().execute(loginData);
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        MainActivity.getInstance().closeApplication();
    }

    class LoginTask extends AsyncTask<String, String, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage("Processing");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(params[0]);
            arrayList.add(params[1]);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            mProgressDialog.dismiss();
            Helpers.saveDataToSharedPreferences(AppGlobals.KEY_USERNAME, arrayList.get(0));
            Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PASSWORD, arrayList.get(1));
            Helpers.userLogin(true);
            finish();
            AppGlobals.loginSuccessFull = true;
        }
    }
}
