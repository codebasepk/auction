package com.byteshaft.auction.gcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.byteshaft.auction.MainActivity;
import com.byteshaft.auction.R;
import com.byteshaft.auction.SelectedAdDetail;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]

    @Override
    public void onMessageReceived(String from, Bundle data) {
        System.out.println(data);
        String type = data.getString("type");
        System.out.println(data.getString("ad_owner"));
        switch (type) {
            case "new_ad_posted":
                if (!data.getString("ad_owner").equals(Helpers
                        .getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME))) {
                    sendNotification("A new ad is posted in your subscribed category", "New Ad Posted !!",
                            SelectedAdDetail.class, AppGlobals.detail, Integer.valueOf(data.getString("ad_id")));
                }
                break;
            case "half_time_no_bid":
                    sendNotification("Ad is posted 12h ago , you might be interested in this one", "No Bid",
                            SelectedAdDetail.class, AppGlobals.detail, Integer.valueOf(data.getString("ad_id")));
                break;
            case "sold_to_highest_bidder":

                String message;
                Class activity;
                if (data.getString("sold_to").equals(Helpers.getStringDataFromSharedPreference(
                        AppGlobals.KEY_USERNAME))) {
                    message = "You are the winning bidder";
                    activity = SelectedAdDetail.class;
                } else {
                    message = "Product, its sold to highest bidder";
                    activity = MainActivity.class;
                }
                    sendNotification(message, "Product sold",
                            activity, AppGlobals.detail, Integer.valueOf(data.getString("ad_id")));
                if ((Integer) data.get("ad_id") == SelectedAdDetail.adPrimaryKey) {
                    SelectedAdDetail.linearLayout.setVisibility(View.GONE);
                    SelectedAdDetail.soldItem.setVisible(true);
                }
                break;
            case "ad_expired":
                    sendNotification("An ad is expired", "Ad Expired",
                            MainActivity.class, "", 0);
                break;
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title, Class  activity, String key, int value ) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(key, value);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}

