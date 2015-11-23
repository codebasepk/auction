package com.byteshaft.auction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.byteshaft.auction.fragments.ChatActivity;
import com.byteshaft.auction.utils.AppGlobals;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ItemDetail extends AppCompatActivity {

    final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final static int cacheSize = maxMemory / 8;
    private LruCache<String, Bitmap> mMemoryCache;
    private ArrayList<Bitmap> bitmapArrayList;
    private ArrayAdapter adapter;
    private GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detials);
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
        grid = (GridView) findViewById(R.id.grid_view);
        String detail = getIntent().getStringExtra(AppGlobals.detial);
        setTitle(detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bitmapArrayList = new ArrayList<>();
//        new GetItemDetailsTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.chat_button:
                Intent intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_for_user, menu);
        return true;
    }

    class CustomAdapter extends ArrayAdapter<Bitmap> {

        private ArrayList<Bitmap> items;
        private CustomView customView;
        private int mResource;

        public CustomAdapter(Context context, int resource, ArrayList<Bitmap> arrayList) {
            super(context, resource, arrayList);
            items = arrayList;
            mResource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                customView = new CustomView();
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(mResource, parent, false);
                customView.imageView = (ImageView) convertView.findViewById
                        (R.id.grid_item);
            } else {
                customView = (CustomView) convertView.getTag();
            }
            customView.imageView.setImageBitmap(items.get(position));
            return convertView;
        }
    }

    public class CustomView {
        public ImageView imageView;
    }

    class GetItemDetailsTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            getBitmapFromURL("http://ibin.co/2NWjTZqWdF5M");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            bitmapArrayList.add(getBitmapFromMemCache(getTitle().toString()));
            adapter = new CustomAdapter(getApplicationContext(),
                    R.layout.layout_for_horizontal_list_view, bitmapArrayList);
            grid.setAdapter(adapter);
        }

        public void getBitmapFromURL(String src) {
            Bitmap myBitmap = null;
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                //Decryption
                try {
                    InputStream input = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(input);
                    System.out.println("OK");

                } catch (Exception e) {
                    e.fillInStackTrace();
                    Log.v("ERROR", "Errorchence : " + e);
                }
                addBitmapToMemoryCache(getTitle().toString(), myBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        }

        public Bitmap getBitmapFromMemCache(String key) {
            return mMemoryCache.get(key);
        }
    }

}
