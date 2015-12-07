package com.byteshaft.auction.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mRegisterButton;
    private String[] registrationData;
    private EditText mPhoneNumber;
    private EditText mAddress;
    private EditText mCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_fragment);
        setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
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
                System.out.println(mNameEditText.getText().toString());
                if (mNameEditText.getText().toString().trim().isEmpty() || mPhoneNumber.getText()
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
                System.out.println(containsDigit(mPasswordEditText.getText().toString()));
                if (!containsDigit(mPasswordEditText.getText().toString())) {
                    Toast.makeText(AppGlobals.getContext(), "password must contain 0-9", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPasswordEditText.getText().toString().length() < 6) {
                    Toast.makeText(AppGlobals.getContext(), "password must contain 6 character", Toast.LENGTH_SHORT).show();
                    return;
                }
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
}
