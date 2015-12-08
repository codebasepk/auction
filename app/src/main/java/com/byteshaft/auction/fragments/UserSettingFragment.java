package com.byteshaft.auction.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.byteshaft.auction.R;

public class UserSettingFragment extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private EditText mUserName;
    private EditText mUserEmail;
    private EditText mUserCurrentPassword;
    private EditText mNewPassword;
    private Button mButtonDone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.user_setting_fragment, container, false);
        mUserName = (EditText) mBaseView.findViewById(R.id.user_name);
        mUserEmail = (EditText) mBaseView.findViewById(R.id.user_email);
        mUserCurrentPassword = (EditText) mBaseView.findViewById(R.id.current_password);
        mNewPassword = (EditText) mBaseView.findViewById(R.id.new_password);
        mButtonDone = (Button) mBaseView.findViewById(R.id.btn_done);
        mButtonDone.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:

                break;
        }
    }
}
