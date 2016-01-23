package com.byteshaft.auction.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byteshaft.auction.fragments.AdsDetailFragment;
import com.byteshaft.auction.utils.AppGlobals;

public class MessageForDialog extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case AppGlobals.ACTION_FOR_MY_ADS_DETAIL:
                new AdsDetailFragment.GetAllAdsDetailTask((AppGlobals.getCurrentActivity())).execute();
                break;
            case AppGlobals.ACTION_FOR_SELECTED_CATEGORY:
                break;
        }

    }
}
