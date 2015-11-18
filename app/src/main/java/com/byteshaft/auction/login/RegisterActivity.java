package com.byteshaft.auction.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.byteshaft.auction.R;
import com.byteshaft.auction.flagsinputbox.VerifyPhoneFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flags);
        setTitle("Register");
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new VerifyPhoneFragment())
                    .commit();
        }

    }
}
