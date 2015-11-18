/*
 * Copyright (c) 2014-2015 Amberfog.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.byteshaft.auction.flagsinputbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;

public class VerifyPhoneFragment extends BaseFlagFragment implements View.OnClickListener {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mCountryCode;
    private EditText mPhoneNumeber;
    private Button mRegisterButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_fragment, container, false);
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCodes(AppGlobals.getContext());
    }

    @Override
    protected void send() {
        hideKeyboard(mPhoneEdit);
        mPhoneEdit.setError(null);
        validate();
//        if (phone == null) {
//            mPhoneEdit.requestFocus();
//            mPhoneEdit.setError(getString(R.string.label_error_incorrect_phone));
//            return;
//        }
//        Toast.makeText(getActivity(), "send to " + phone, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
        }

    }
}
