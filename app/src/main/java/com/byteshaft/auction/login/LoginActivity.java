package com.byteshaft.auction.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.byteshaft.auction.MainActivity;
import com.byteshaft.auction.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setTitle("Login");
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        MainActivity.getInstance().closeApplication();
    }
}
