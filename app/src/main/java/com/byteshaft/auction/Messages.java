package com.byteshaft.auction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Random;


public class Messages extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private ProgressDialog mProgressDialog;
    private int primaryKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_messages_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.messengerListView);
        primaryKey = getIntent().getIntExtra(AppGlobals.PRIMARY_KEY, 0);
        System.out.println(primaryKey);
        String url = AppGlobals.GET_ALL_MESSAGES_URL + Helpers.
                getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME)+ File.separator + "ads" +
                File.separator + primaryKey + File.separator+ "messages" + File.separator + "names";
        new GetAllMessengersTask().execute(url);
        mListView.setOnItemClickListener(this);

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(parent.getItemAtPosition(position));
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(AppGlobals.MESSENGER_USERNAME, parent.getItemIdAtPosition(position));
    }

    class GetAllMessengersTask extends AsyncTask<String, String, ArrayList<String>> {

        private boolean noInternet = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(Messages.this);
            mProgressDialog.setMessage("loading ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> messengers = new ArrayList<>();
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String[] data;
                try {
                     data = Helpers.simpleGetRequest(params[0],
                            Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME),
                            Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD));
                    if (Integer.valueOf(data[0]) == HttpURLConnection.HTTP_OK) {
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = jsonParser.parse(data[1]).getAsJsonObject();
                        JsonArray jsonArray =  jsonObject.get("messengers").getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++)  {
                            messengers.add(jsonArray.get(i).getAsString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                noInternet = true;
                messengers.add(String.valueOf(AppGlobals.NO_INTERNET));
            }
            return messengers;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            if (noInternet) {
                return;
            }
            if (s.size() >= 0 && !noInternet) {
                MessengerAdapter messengerAdapter = new MessengerAdapter(getApplicationContext(),
                        R.layout.single_message_senders, s);
                mListView.setAdapter(messengerAdapter);
            }

        }
    }

    class MessengerAdapter extends ArrayAdapter {

        private ViewHolder holder;
        private ArrayList<String> mArrayList;

        public MessengerAdapter(Context context, int resource, ArrayList<String> arrayList) {
            super(context, resource);
            this.mArrayList = arrayList;
        }

        @Override
        public int getCount () {
            return mArrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.single_message_senders, parent, false);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.username = (TextView) convertView.findViewById(R.id.messenger_username);
                holder.imageView = (CircularImageView) convertView.findViewById(R.id.messenger_image);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String currentUsername = mArrayList.get(position);
            holder.username.setText(currentUsername.toUpperCase());
            int[] array = getResources().getIntArray(R.array.letter_tile_colors);
            final BitmapWithCharacter tileProvider = new BitmapWithCharacter();
            final Bitmap letterTile = tileProvider.getLetterTile(currentUsername,
                    String.valueOf(array[new Random().nextInt(array.length)]), 100, 100);
            holder.imageView.setImageBitmap(letterTile);
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView username;
        public CircularImageView imageView;
    }
}
