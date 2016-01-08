package com.byteshaft.auction.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        String[] data = {Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME), token};
        if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
            new sendPushNotificationKey().execute(data);
        }
    }

    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]
    class sendPushNotificationKey extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            URL url;
            try {
                url = new URL(AppGlobals.PUSH_NOTIFICATION_KEY+params[0] + "/push_id");
                System.out.println(AppGlobals.PUSH_NOTIFICATION_KEY+params[0] + "/push_id");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");
                String authString = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME)
                        + ":" + Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
                connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
                String jsonFormattedData = getJsonObjectString(String.valueOf(params[1]));
                sendRequestData(connection, jsonFormattedData);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getJsonObjectString(String pushKey) {
            return String.format("{\"push_key\": \"%s\"}", pushKey);
        }

        private void sendRequestData(HttpURLConnection connection, String body) throws IOException {
            byte[] outputInBytes = body.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write(outputInBytes);
            os.close();
            System.out.println(connection.getResponseCode());
        }
    }


}

