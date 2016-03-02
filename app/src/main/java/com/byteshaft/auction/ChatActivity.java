package com.byteshaft.auction;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.BitmapWithCharacter;
import com.byteshaft.auction.utils.Helpers;
import com.byteshaft.auction.utils.List;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class ChatActivity extends Activity implements View.OnClickListener {

    private ImageButton buttonSend;
    private EditText editTextMessage;
    public ChatArrayAdapter adapter;
    public String userNameToFetchMessages;
    private int adPrimaryKey;
    private com.byteshaft.auction.utils.List mBubbleList;
    private static String sNextUrl = "";
    private static HashMap<Integer, String> sDirectionHashMap;
    private static HashMap<Integer, String> sSenderName;
    private static HashMap<Integer, String> sMessages;
    private static boolean loadMore = false;
    private boolean isScrollingUp = false;
    private ArrayList<Integer> arrayList;
    private ProgressBar mProgressBar;
    private Toolbar toolbar;
    private String productOwner = "";
    private String messageReceiver = "";
    private String messageDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
//        toolbar = (Toolbar) findViewById(R.id.tool);
//        setActionBar(toolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        adPrimaryKey = getIntent().getIntExtra(AppGlobals.PRIMARY_KEY, 0);
        productOwner = getIntent().getStringExtra(AppGlobals.PRODUCT_OWNER);
        messageReceiver = getIntent().getStringExtra(AppGlobals.MESSAGE_RECEIVER);
        editTextMessage = (EditText) findViewById(R.id.et_chat);
        buttonSend = (ImageButton) findViewById(R.id.button_chat_send);
        mProgressBar = (ProgressBar) findViewById(R.id.load_messages);
        buttonSend.setOnClickListener(this);
        mBubbleList = (com.byteshaft.auction.utils.List) findViewById(R.id.lv_chat);
        setTitle(userNameToFetchMessages);
        String url;
        String messagesTarget;
        if (messageReceiver != null && !messageReceiver.equals("")) {
            messagesTarget = messageReceiver;
            messageDirection = "incoming";
            System.out.println("messageReceiver");
        } else {
            System.out.println("username");
            messageDirection = "outgoing";
            messagesTarget = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
        }
        url = AppGlobals.GET_USER_SPECIFIC_MESSAGES + messagesTarget + File.separator + "ads" +
                File.separator + adPrimaryKey + File.separator + "messages";
        sMessages = new HashMap<>();
        sSenderName = new HashMap<>();
        sDirectionHashMap = new HashMap<>();
        new FetchMessages().execute(url);
        mBubbleList.setOnDetectScrollListener(new List.OnDetectScrollListener() {
            @Override
            public void onUpScrolling() {
                if (!isScrollingUp && !loadMore) {
                    isScrollingUp = true;
                    if (!sNextUrl.trim().isEmpty()) {
                        new FetchMessages().execute(sNextUrl);
                    }
                }
            }

            @Override
            public void onDownScrolling() {
                isScrollingUp = false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_chat_send:
                if (!editTextMessage.getText().toString().trim().isEmpty()) {
                    new SendMessageTask().execute(editTextMessage.getText().toString());
                }
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    // custom raw chat interface
    private class ChatArrayAdapter extends ArrayAdapter {

        private ArrayList<Integer> idsList;
        private ViewHolder holder;
        private String direction;

        public ChatArrayAdapter(Context context, int resource, ArrayList<Integer> idsList,
                                String direction) {
            super(context, resource, idsList);
            this.idsList = idsList;
            this.direction = direction;

        }

        @Override
        public Integer getItem(int position) {
            return idsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return idsList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.singlemessage_chat, parent, false);
                holder = new ViewHolder();
                holder.layout = (RelativeLayout) convertView.findViewById(R.id.singleMessageContainer);
                holder.messageBody = (TextView) convertView.findViewById(R.id.singleMessage);
                holder.invisibleTextView = (TextView) convertView.findViewById(R.id.messageId);
                holder.imageView = (CircularImageView) convertView.findViewById(R.id.messenger);
                holder.myMessage = (CircularImageView) convertView.findViewById(R.id.myMessage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (sDirectionHashMap.get(idsList.get(position)).equals(direction)) {
                RelativeLayout.LayoutParams layoutParams = new
                        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(150,0,0,0);
                holder.messageBody.setText(null);
                holder.invisibleTextView.setText(String.valueOf(idsList.get(position)));
                holder.layout.setGravity(Gravity.RIGHT);
                holder.layout.setGravity(Gravity.END);
                holder.messageBody.setBackgroundResource(R.drawable.bubble_b);
                holder.myMessage.setVisibility(View.INVISIBLE);
                holder.imageView.setVisibility(View.VISIBLE);
                BitmapWithCharacter tileProvider = new BitmapWithCharacter();
                Bitmap letterTile;
                letterTile = tileProvider.getLetterTile(sSenderName.get(idsList.get(position)),
                        "#2093cd", 100, 100);

                holder.imageView.setImageBitmap(letterTile);
                holder.messageBody.setText(sMessages.get(idsList.get(position)));
                holder.messageBody.setLayoutParams(layoutParams);

            } else {
                holder.messageBody.setText(null);
                RelativeLayout.LayoutParams layoutParams = new
                        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(430, 5, 0, 0);
                holder.invisibleTextView.setText(String.valueOf(idsList.get(position)));
                holder.layout.setGravity(Gravity.LEFT);
                holder.layout.setGravity(Gravity.START);
                holder.messageBody.setBackgroundResource(R.drawable.bubble_a);
                holder.imageView.setVisibility(View.INVISIBLE);
                holder.myMessage.setVisibility(View.VISIBLE);
                BitmapWithCharacter tileProvider = new BitmapWithCharacter();
                Bitmap letterTile;
                letterTile = tileProvider.getLetterTile(sSenderName.get(idsList.get(position)),
                        "#f58559", 100, 100);
                holder.myMessage.setImageBitmap(letterTile);
                holder.messageBody.setText(sMessages.get(idsList.get(position)));
                holder.messageBody.setLayoutParams(layoutParams);

            }
            return convertView;
        }
    }

    // custom Member class for viewHolder to access xml elements
    static class ViewHolder {
        public TextView messageBody;
        public TextView invisibleTextView;
        public CircularImageView imageView;
        public RelativeLayout layout;
        public CircularImageView myMessage;
    }

    class FetchMessages extends AsyncTask<String, String, ArrayList<Integer>> {

        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            if (isScrollingUp) {
                loadMore = true;
            } else {
                arrayList = new ArrayList<>();
            }
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String[] data;
                try {
                    data = Helpers.simpleGetRequest(params[0], Helpers
                                    .getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME),
                            Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD));
                    if (Integer.valueOf(data[0]) == HttpURLConnection.HTTP_OK) {
                        JsonParser jsonParser = new JsonParser();
                        Object json = new JSONTokener(data[1]).nextValue();
                        if (json instanceof JSONObject) {
                            JsonObject jsonObject = jsonParser.parse(data[1]).getAsJsonObject();
                            if (!jsonObject.get("next").isJsonNull()) {
                                sNextUrl = jsonObject.get("next").getAsString();
                            }
                            JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject jObject = jsonArray.get(i).getAsJsonObject();
                                if (!arrayList.contains(jObject.get("id").getAsInt())
                                        && (jObject.get("sender_name").getAsString()
                                        .equals(productOwner) || jObject.get("sender_name")
                                        .getAsString().equals(messageReceiver))) {
                                    int currentId = jObject.get("id").getAsInt();
                                    arrayList.add(currentId);
                                    sDirectionHashMap.put(currentId, jObject.get("direction")
                                            .getAsString());
                                    sSenderName.put(currentId, jObject.get("sender_name").getAsString());
                                    sMessages.put(currentId, jObject.get("message").getAsString());
                                }
                            }
                        } else if (json instanceof JSONArray) {
                            JsonArray jsonArray = (JsonArray) jsonParser.parse(data[1]);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject jObject = jsonArray.get(i).getAsJsonObject();
                                if (!arrayList.contains(jObject.get("id").getAsInt())) {
                                    int currentId = jObject.get("id").getAsInt();
                                    arrayList.add(currentId);
                                    sDirectionHashMap.put(currentId, jObject.get("direction")
                                            .getAsString());
                                    sSenderName.put(currentId, jObject.get("sender_name").getAsString());
                                    sMessages.put(currentId, jObject.get("message").getAsString());
                                }
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                noInternet = true;
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> integers) {
            super.onPostExecute(integers);
            mProgressBar.setVisibility(View.GONE);
            if (loadMore && isScrollingUp) {
                adapter = null;
                ArrayList<Integer> tempElements = new ArrayList<>(integers);
                Collections.reverse(tempElements);
                adapter = new ChatArrayAdapter(
                        getApplicationContext(), R.layout.singlemessage_chat, tempElements,
                        messageDirection);
                mBubbleList.setAdapter(adapter);
                isScrollingUp = false;
                loadMore = false;
            } else {
                if (!noInternet) {
                    ArrayList<Integer> tempElements = new ArrayList<>(integers);
                    Collections.reverse(tempElements);
                    adapter = new ChatArrayAdapter(
                            getApplicationContext(), R.layout.singlemessage_chat, tempElements,
                            messageDirection);
                    mBubbleList.setAdapter(adapter);
                }
            }
        }
    }

    class SendMessageTask extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String parsedString;
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String passWord = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                String url = AppGlobals.SEND_MESSAGE_URL + productOwner + File.separator +
                        "ads" + File.separator + adPrimaryKey + File.separator + "messages";
                URL link;
                try {
                    link = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) link.openConnection();
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod("POST");
                    String authString = userName
                            + ":" + passWord;
                    String authStringEncoded = Base64.encodeToString(authString.getBytes(),
                            Base64.DEFAULT);
                    connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
                    String sendMessageTo;
                    if (messageReceiver != null && !messageReceiver.equals("")) {
                        sendMessageTo = messageReceiver;
                    } else {
                        sendMessageTo = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                    }
                    String jsonFormattedData = getJsonObjectString(sendMessageTo, params[0]);
                    Helpers.sendRequestData(connection, jsonFormattedData);
                    InputStream is = connection.getInputStream();
                    parsedString = Helpers.convertInputStreamToString(is);
                    return new String[]{String.valueOf(connection.getResponseCode()), params[0],
                            parsedString};
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new String[]{String.valueOf(AppGlobals.NO_INTERNET)};
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            mProgressBar.setVisibility(View.GONE);
            if (Integer.valueOf(strings[0]) == HttpURLConnection.HTTP_OK) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(strings[2]).getAsJsonObject();
                if (!arrayList.contains(jsonObject.get("id").getAsInt())) {
                    int currentId = jsonObject.get("id").getAsInt();
                    arrayList.add(0, currentId);
                    sDirectionHashMap.put(currentId, jsonObject.get("direction")
                            .getAsString());
                    Log.i("SEND", sDirectionHashMap.toString());
                    sSenderName.put(currentId, jsonObject.get("sender_name").getAsString());
                    sMessages.put(currentId, jsonObject.get("message").getAsString());
                    adapter = null;
                    ArrayList<Integer> tempElements = new ArrayList<>(arrayList);
                    Collections.reverse(tempElements);
                    adapter = new ChatArrayAdapter(
                            getApplicationContext(), R.layout.singlemessage_chat, tempElements,
                            messageDirection);
                    mBubbleList.setAdapter(adapter);
                    editTextMessage.setText("");
                }
            }
        }
    }

    private static String getJsonObjectString(String messenger_name, String message) {
        return String.format("{\"bidder_name\": \"%s\" , \"message\": \"%s\"}",
                messenger_name, message);
    }
}
