package com.byteshaft.auction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byteshaft.auction.fragments.ChatActivity;
import com.byteshaft.auction.fragments.seller.ProductImageView;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Class to show the details if a product which will include item images, bids, rating of the seller
 * product description, price and option of chatting with the client / buyer
 */
public class SelectedAdDetail extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Bitmap> bitmapArrayList;
    private GridView mGrid;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> arrayList;
    private int adPrimaryKey;
    public int id;
    public String description;
    public String price;
    public ArrayList<String> imagesUrls;
    public String currency;
    public String title;
    public TextView descriptionTextView;
    public TextView adPrice;
    private ProgressDialog mProgressDialog;
    public EditText placeBidEditText;
    public Button placeBidButton;
    public ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detials);
        final ProductImageView productImageView = new ProductImageView();
        adPrimaryKey = getIntent().getIntExtra(AppGlobals.detail, 0);
        System.out.println(adPrimaryKey);
        String productName = getIntent().getStringExtra(AppGlobals.SINGLE_PRODUCT_NAME);
        descriptionTextView = (TextView) findViewById(R.id.ad_description);
        imagesUrls = new ArrayList<>();
        placeBidButton = (Button) findViewById(R.id.place_bid);
        placeBidEditText = (EditText) findViewById(R.id.bid_editText);
        placeBidButton.setOnClickListener(this);
        adPrice = (TextView) findViewById(R.id.ad_price);
        mProgressBar = (ProgressBar) findViewById(R.id.bid_loading_progress);
        setTitle(productName);
        mGrid = (GridView) findViewById(R.id.grid_view);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), productImageView.getClass());
                intent.putExtra("url", imagesUrls.get(position));
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.bids_recycler_view);
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
        new GetItemDetailsTask().execute();

//        mBidsAdapter = new BidsAdapter(arrayList);
//        mRecyclerView.setAdapter(mBidsAdapter);
//        mRecyclerView.addOnItemTouchListener(new BidsAdapter(arrayList, getApplicationContext()
//                , new BidsAdapter.OnItemClickListener() {
//            @Override
//            public void onItem(String item) {
//            }
//        }));
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.place_bid:
                if (placeBidEditText.getText().toString().trim().isEmpty()) {
                    return;
                }
                if (!placeBidEditText.getText().toString().trim().isEmpty() &&
                    TextUtils.isDigitsOnly(placeBidEditText.getText().toString())) {
                    String bid = placeBidEditText.getText().toString();
                    new PlaceBidTask().execute(bid);
                }


                break;
        }
    }

    /**
     * Custom Member class for to show images in grid view
     */
    class CustomAdapter extends BaseAdapter {

        private ArrayList<String> images;


        public CustomAdapter(ArrayList<String> urlsList) {
            this.images = urlsList;
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(2, 4, 2, 4);
            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(SelectedAdDetail.this)
                    .load(images.get(position))

                    .resize(640, 480)
                    .centerCrop()
                    .into(imageView);
            return imageView;
        }
    }

    class GetItemDetailsTask extends AsyncTask<String, String, ArrayList<Bitmap>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(SelectedAdDetail.this);
            mProgressDialog.setMessage("loading ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                try {
                    String[] strings = Helpers.simpleGetRequest(AppGlobals.SINGLE_AD_DETAILS + userName
                                    + File.separator + AppGlobals.SINGLE_AD_DETAILS_APPEND_END + adPrimaryKey + "/",
                            userName, password);
                    if (HttpURLConnection.HTTP_OK == Integer.valueOf(strings[0])) {
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = jsonParser.parse(strings[1]).getAsJsonObject();
                        id = jsonObject.get("id").getAsInt();
                        description = jsonObject.get("description").getAsString();
                        price = jsonObject.get("price").getAsString();
                        title = jsonObject.get("title").getAsString();
                        currency = jsonObject.get("currency").getAsString();

                        for (int i = 1; i < 9; i++) {
                            String photoCounter = ("photo") + i;
                            if (!jsonObject.get(photoCounter).isJsonNull()) {
                                imagesUrls.add((AppGlobals.BASE_URL +
                                        jsonObject.get(photoCounter).getAsString()));
                            }
                        }
                        JsonArray jsonArray = jsonObject.getAsJsonArray("bids");
                        System.out.println("bids"+jsonArray);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmap) {
            super.onPostExecute(bitmap);
            mProgressDialog.dismiss();
            descriptionTextView.setText("Description: \n \n" + description);
            adPrice.setText(price + currency);
            mGrid.setAdapter(new CustomAdapter(imagesUrls));
            new GetBidsTask().execute();
        }
    }

    /**
     * Custom class that extends RecyclerView adapter
     */
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
//            View childView = rv.findChildViewUnder(e.getX(), e.getY());
//            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
//                mListener.onItem(items.get(rv.getChildPosition(childView)));
//                return true;
//            }
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

    class PlaceBidTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
            String url = AppGlobals.POST_BID_URL + userName + "/ads/" + adPrimaryKey + "/bids/post";
            try {
                Helpers.authPostRequest(url, "bid", params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class GetBidsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String[] data = new String[0];
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                String url = AppGlobals.GET_SPECIFIC_BIDS + userName + "/ads/" + adPrimaryKey +"/bids/";
                try {
                    data = Helpers.simpleGetRequest(url, userName, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(data[0]);
                System.out.println(data[1]);
            }
            return null;
        }
    }

}

