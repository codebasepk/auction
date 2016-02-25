package com.byteshaft.auction;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.BitmapWithCharacter;
import com.byteshaft.auction.utils.Helpers;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton buttonSend;
    private EditText editTextMessage;
    private String contactName;
    private String mContextUserTable;
    public ChatArrayAdapter adapter;
    public String userNameToFetchMessages;
    private int adPrimaryKey;
    private ListView mBubbleList;
    private static String sNextUrl = "";
    private static HashMap<Integer, String> sDirectionHashMap;
    private static HashMap<Integer, String> sSenderName;
    private static HashMap<Integer, String> sMessages;
    private static boolean loadMore = false;
    private int mLastFirstVisibleItem;
    private boolean scrollingUp = false;
    private int preLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userNameToFetchMessages = getIntent().getStringExtra(AppGlobals.MESSENGER_USERNAME);
        adPrimaryKey = getIntent().getIntExtra(AppGlobals.PRIMARY_KEY, 0);
        editTextMessage = (EditText) findViewById(R.id.et_chat);
        buttonSend = (ImageButton) findViewById(R.id.button_chat_send);
        buttonSend.setOnClickListener(this);
        mBubbleList = (ListView) findViewById(R.id.lv_chat);
        setTitle(userNameToFetchMessages);
        String url = AppGlobals.GET_USER_SPECIFIC_MESSAGES + Helpers
                .getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME) + File.separator + "ads" +
                File.separator + adPrimaryKey + File.separator + "messages";
        sMessages = new HashMap<>();
        sSenderName = new HashMap<>();
        sDirectionHashMap = new HashMap<>();
        new FetchMessages().execute(url);
        mBubbleList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final ListView lw = mBubbleList;

                if (view.getId() == lw.getId()) {
                    final int currentFirstVisibleItem = lw.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        scrollingUp = false;
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        scrollingUp = true;
                         System.out.println("UP");
                    }
                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                switch (view.getId()) {
                    case R.id.lv_chat:
                        final int lastItem = firstVisibleItem + visibleItemCount;
                        if (lastItem == totalItemCount) {
                            if (preLast != lastItem && !loadMore) { //to avoid multiple calls for last item
                                Log.d("Last", "Last");
                                preLast = lastItem;
                                loadMore = true;
                                if (!sNextUrl.trim().isEmpty()) {
//                                    new GetSoundDetailsTask().execute();
                                }
                            }
                        } else if (lastItem == (totalItemCount)) {
                            System.out.println("scrollingUp" + scrollingUp);
                            if (!scrollingUp) {
                                System.out.println("OKKKK");
                            }
                        }
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

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

        public ChatArrayAdapter(Context context, int resource, ArrayList<Integer> idsList) {
            super(context, resource, idsList);
            this.idsList = idsList;
        }

        @Override
        public int getCount () {
            return idsList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.singlemessage_chat, parent, false);
                holder = new ViewHolder();
                holder.layout = (LinearLayout) convertView.findViewById(R.id.singleMessageContainer);
                holder.title = (TextView) convertView.findViewById(R.id.singleMessage);
                holder.invisibleTextView = (TextView) convertView.findViewById(R.id.messageId);
                holder.imageView = (CircularImageView) convertView.findViewById(R.id.messenger);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.invisibleTextView.setText(String.valueOf(idsList.get(position)));
            holder.title.setText(sMessages.get(idsList.get(position)));
            final BitmapWithCharacter tileProvider = new BitmapWithCharacter();
            Bitmap letterTile;
            if (sDirectionHashMap.get(idsList.get(position)).equals("incoming")) {
                holder.layout.setGravity(Gravity.LEFT);
                letterTile = tileProvider.getLetterTile(sSenderName.get(idsList.get(position)),
                        "#2093cd", 100, 100);
            } else {
                holder.layout.setGravity(Gravity.RIGHT);
                letterTile = tileProvider.getLetterTile(sSenderName.get(idsList.get(position)),
                        "#f58559", 100, 100);
            }
            holder.imageView.setImageBitmap(letterTile);
            return convertView;
        }
    }

    // custom Member class for viewHolder to access xml elements
    static class ViewHolder {
        public TextView title;
        public TextView invisibleTextView;
        public CircularImageView imageView;
        public LinearLayout layout;
    }

    class FetchMessages extends AsyncTask<String, String, ArrayList<Integer>> {

        private boolean noInternet = false;

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String[] data;
                try {
                    data = Helpers.simpleGetRequest(params[0],
                            Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME),
                            Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD));
                    if (Integer.valueOf(data[0]) == HttpURLConnection.HTTP_OK) {
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = jsonParser.parse(data[1]).getAsJsonObject();
                        System.out.println(jsonObject);
                        if (!jsonObject.get("next").isJsonNull()) {
                            sNextUrl = jsonObject.get("next").getAsString();
                        }
                        JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();
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
                } catch (IOException e) {
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
            if (!noInternet && integers.size() > 0) {
                adapter = new ChatArrayAdapter(
                        getApplicationContext(), R.layout.singlemessage_chat, integers);
                mBubbleList.setAdapter(adapter);
            }
        }
    }
}
