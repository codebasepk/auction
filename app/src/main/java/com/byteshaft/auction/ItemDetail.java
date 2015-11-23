package com.byteshaft.auction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.byteshaft.auction.utils.AppGlobals;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ItemDetail extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final static int cacheSize = maxMemory / 8;
    private LruCache<String, Bitmap> mMemoryCache;

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
        String detail = getIntent().getStringExtra(AppGlobals.detial);
        setTitle(detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.images_recycler_view);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        new GetItemDetailsTask().execute();

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

    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<String> items;
        private CustomView viewHolder;

        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public CustomAdapter(ArrayList<String> categories, Context context, OnItemClickListener listener) {
            this.items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        public CustomAdapter(ArrayList<String> categories) {
            this.items = categories;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detials, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            for (int i = 0; i <= getImageForCategory().length; i++) {
//                viewHolder.imageView.setImageDrawable(getImageForCategory()[i]);
//            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                System.out.println(items == null);
                mListener.onItem(items.get(rv.getChildPosition(childView)));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public interface OnItemClickListener {
            void onItem(String item);
        }
    }

    public static class CustomView extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public CustomView(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.specific_category_image);
        }
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
//            arrayList = new ArrayList<>();
//            arrayList.add("Moto");
//            arrayList.add("Htc");
//            arrayList.add("Samsug");
//            arrayList.add("LG");
//            mAdapter = new CustomAdapter();
//            mRecyclerView.setAdapter(mAdapter);
//            mRecyclerView.addOnItemTouchListener(new CustomAdapter(arrayList, getApplicationContext(),
//                    new CustomAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItem(String item) {
//                            Intent intent = new Intent(getApplicationContext(), ItemDetail.class);
//                            intent.putExtra(AppGlobals.detial, item);
////                            startActivity(intent);
//                        }
//                    }));
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
                System.out.println("OK");
            }
        }

        public Bitmap getBitmapFromMemCache(String key) {
            return mMemoryCache.get(key);
        }
    }


}
