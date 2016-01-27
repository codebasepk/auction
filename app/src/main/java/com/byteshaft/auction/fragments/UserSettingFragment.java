package com.byteshaft.auction.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;


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
        mButtonDone.setOnClickListener(this);
        mButtonDone.setVisibility(View.GONE);
        setHasOptionsMenu(true);
        getValuesFromSharedPreference();
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

    class UpdateUserDetailsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            
            return null;
        }
    }
}
