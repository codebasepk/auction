package com.byteshaft.auction.fragments.seller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

        String[] list = { "Books","Life Style" ,"Education","Electronics", "Entertainment", "Games"};


        itemTitle = (EditText) mBaseView.findViewById(R.id.item_title);
        itemDescription = (EditText) mBaseView.findViewById(R.id.item_description);
        submintButton = (Button) mBaseView.findViewById(R.id.btn_submit);
        categorySpinner = (Spinner) mBaseView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list);
        categorySpinner.setAdapter(adapter);


        return mBaseView;
    }
}
