package com.byteshaft.auction.fragments.seller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.byteshaft.auction.MainActivity;
import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.Helpers;


public class Seller extends Fragment {

    private EditText itemTitle;
    private EditText itemDescription;
    private Button submintButton;
    private Spinner categorySpinner;

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.seller_fragment, container, false);
        Helpers.saveLastFragmentOpend(getClass().getSimpleName());

        itemTitle = (EditText) mBaseView.findViewById(R.id.item_title);
        itemDescription = (EditText) mBaseView.findViewById(R.id.item_description);
        submintButton = (Button) mBaseView.findViewById(R.id.btn_submit);

        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.isLastFragmentAvailable) {
            MainActivity.loginButton.setVisibility(View.INVISIBLE);
            MainActivity.registerButton.setVisibility(View.INVISIBLE);
            MainActivity.loginButton.setEnabled(false);
            MainActivity.registerButton.setEnabled(false);
        }
    }
}
