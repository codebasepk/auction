package com.byteshaft.auction.fragments.buyer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.auction.utils.Helpers;

public class Buyer extends Fragment {

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mBaseView = inflater.inflate(R.layout.register_fragment, container, false);
        Helpers.saveLastFragmentOpend(getActivity().getClass().getSimpleName());

        return mBaseView;
    }
}
