package com.byteshaft.auction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.auction.fragments.ChatActivity;
import com.byteshaft.auction.utils.AppGlobals;

import java.util.ArrayList;

public class ItemDetail extends AppCompatActivity {

    final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final static int cacheSize = maxMemory / 8;
    private LruCache<String, Bitmap> mMemoryCache;
    private ArrayList<Bitmap> bitmapArrayList;
    private GridView mGrid;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BidsAdapter mBidsAdapter;
    private ArrayList<String> arrayList;

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
//        mGrid = (GridView) findViewById(R.id.grid_view);
        String detail = getIntent().getStringExtra(AppGlobals.detail);
        setTitle(detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.bids_recycler_view);
        System.out.println(mRecyclerView == null);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_for_item_detail);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green,
                R.color.colorPrimary, R.color.gray);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        bitmapArrayList = new ArrayList<>();
        arrayList = new ArrayList<>();
        arrayList.add("s9iper1");
        arrayList.add("shg8rb");
        arrayList.add("om26er");
        arrayList.add("fi8er1");
        arrayList.add("hussi");
        arrayList.add("humza");
        arrayList.add("talha");
        arrayList.add("falak");
        arrayList.add("jamal");
        arrayList.add("that");
//        new GetItemDetailsTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBidsAdapter = new BidsAdapter(arrayList);
        mRecyclerView.setAdapter(mBidsAdapter);
        mRecyclerView.addOnItemTouchListener(new BidsAdapter(arrayList, getApplicationContext()
                , new BidsAdapter.OnItemClickListener() {
            @Override
            public void onItem(String item) {
                System.out.println(item);
            }
        }));

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
//            customView.imageView.setImageBitmap(items.get(position));
            return convertView;
        }

    }

    public class CustomView {
        public ImageView imageView;
    }

    class GetItemDetailsTask extends AsyncTask<String, String, ArrayList<Bitmap>> {

        @Override
        protected ArrayList<Bitmap> doInBackground(String... params) {
            ArrayList<Bitmap> myBitmap = null;
//            Bitmap bitmap = null;
//            try {
//                URL url = new URL(params[0]);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                try {
//                    InputStream input = connection.getInputStream();
//                    bitmap = BitmapFactory.decodeStream(input);
//                    File file = new File(getCacheDir(), File.separator + params[1] + File.separator
//                            + "images" + File.separator);
//                    if (!file.exists()) {
//                        file.mkdirs();
//                    }
//                } catch (Exception e) {
//                    e.fillInStackTrace();
//                    Log.v("ERROR", "Errorchence : " + e);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return myBitmap;
        }

        private int getBitmapNameLocally(int num) {
            return num + 1;
        }


        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmap) {
            super.onPostExecute(bitmap);
//            bitmapArrayList.add(getBitmapFromMemCache(getTitle().toString()));
//            adapter = new CustomAdapter(getApplicationContext(),
//                    R.layout.layout_for_horizontal_list_view, bitmapArrayList);
//            mGrid.setAdapter(adapter);

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

    static class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.BidView> implements
            RecyclerView.OnItemTouchListener {

        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;
        private BidView bidView;
        private ArrayList<String> items;

        public interface OnItemClickListener {
            void onItem(String item);
        }

        public BidsAdapter(ArrayList<String> data) {
            super();
            this.items = data;
        }

        public BidsAdapter(ArrayList<String> categories, Context context, OnItemClickListener listener) {
            this.items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public BidView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bids_layout, parent, false);
            bidView = new BidView(view);
            return bidView;
        }

        @Override
        public void onBindViewHolder(BidView holder, int position) {
            holder.setIsRecyclable(false);
//            bidView.textView.setText(String.valueOf(position));
            bidView.bidderTextView.setText(items.get(position));
        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
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

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class BidView extends RecyclerView.ViewHolder {
            public TextView textView;
            public TextView bidderTextView;

            public BidView(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.bid_text_View);
                bidderTextView = (TextView) itemView.findViewById(R.id.bidder_user_name);
            }
        }
    }

}

