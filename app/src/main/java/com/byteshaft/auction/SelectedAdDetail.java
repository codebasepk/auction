package com.byteshaft.auction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;

/**
 * Class to show the details if a product which will include item images, bids, rating of the seller
 * product description, price and option of chatting with the client / buyer
 */
public class SelectedAdDetail extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Bitmap> bitmapArrayList;
    private RecyclerView mRecyclerView;
    private ArrayList<Integer> arrayList;
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
    public static HashMap<Integer, String> userNameHashMap;
    public static HashMap<Integer, String> bidPriceHashMap;
    public BidsAdapter mBidsAdapter;
    private ProductImageView productImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detials);
        productImageView = new ProductImageView();
        adPrimaryKey = getIntent().getIntExtra(AppGlobals.detail, 0);
        String productName = getIntent().getStringExtra(AppGlobals.SINGLE_PRODUCT_NAME);
        descriptionTextView = (TextView) findViewById(R.id.ad_description);
        imagesUrls = new ArrayList<>();
        userNameHashMap = new HashMap<>();
        bidPriceHashMap = new HashMap<>();
        placeBidButton = (Button) findViewById(R.id.place_bid);
        placeBidEditText = (EditText) findViewById(R.id.bid_editText);
        placeBidButton.setOnClickListener(this);
        adPrice = (TextView) findViewById(R.id.ad_price);
        mProgressBar = (ProgressBar) findViewById(R.id.bid_loading_progress);
        setTitle(productName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.bids_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        bitmapArrayList = new ArrayList<>();
        arrayList = new ArrayList<>();
        new GetItemDetailsTask().execute();
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
                    Toast.makeText(SelectedAdDetail.this, "please enter some amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                int productPrice = Integer.valueOf(price.replaceAll(".00", ""));
                int biddingAmount = Integer.valueOf(placeBidEditText.getText().toString());
                if (biddingAmount < productPrice) {
                    Helpers.alertDialog(SelectedAdDetail.this, "", "price is lower than product amount");
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
            LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
            int value = 0;
            for (String url : imagesUrls) {
                ImageView imageView = new ImageView(SelectedAdDetail.this);
                imageView.setPadding(2, 2, 2, 2);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                value++;
                imageView.setTag(value);
                Picasso.with(SelectedAdDetail.this)
                        .load(url)
                        .resize(150, 150)
                        .into(imageView);
                layout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(v.getTag());
                        Intent intent = new Intent(getApplicationContext(), productImageView.getClass());
                        intent.putExtra("url", imagesUrls.get((Integer) v.getTag() - 1));
                        startActivity(intent);
                    }
                });
            }
            new GetBidsTask().execute();
        }
    }

    /**
     * Custom class that extends RecyclerView adapter
     */
    static class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.BidView> {
        private BidView bidView;
        private ArrayList<Integer> items;

        public BidsAdapter(ArrayList<Integer> data) {
            super();
            this.items = data;
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
            bidView.invisibleBidPrimaryKey.setText(String.valueOf(items.get(position)));
            bidView.textView.setText(bidPriceHashMap.get(items.get(position)));
            bidView.bidderTextView.setText(userNameHashMap.get(items.get(position)));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class BidView extends RecyclerView.ViewHolder {
            public TextView textView;
            public TextView bidderTextView;
            public TextView invisibleBidPrimaryKey;

            public BidView(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.bid_text_View);
                bidderTextView = (TextView) itemView.findViewById(R.id.bidder_user_name);
                invisibleBidPrimaryKey = (TextView) itemView.findViewById(R.id.invisible_bid_primary_key);
            }
        }
    }

    class PlaceBidTask extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String url = AppGlobals.POST_BID_URL + userName + "/ads/" + adPrimaryKey + "/bids/post";
                try {
                    Helpers.authPostRequest(url, "bid", params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return AppGlobals.getPostBidResponse();
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            mProgressBar.setVisibility(View.GONE);
            placeBidEditText.setText("");
            if (s == HttpURLConnection.HTTP_CREATED) {
                mRecyclerView.removeAllViews();
                new GetBidsTask().execute();
            } else if (s == HttpURLConnection.HTTP_CONFLICT) {
                Helpers.alertDialog(SelectedAdDetail.this, "Conflict", "your bid is already placed!");
            } else {
                Helpers.alertDialog(SelectedAdDetail.this, "Error", "There was an unexpected error");
            }
        }
    }

    class GetBidsTask extends AsyncTask<String, String, ArrayList<Integer>> {

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            String[] data = new String[0];
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                String url = AppGlobals.GET_SPECIFIC_BIDS + userName + "/ads/" + adPrimaryKey + "/bids/";
                try {
                    data = Helpers.simpleGetRequest(url, userName, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Integer.valueOf(data[0]) == HttpURLConnection.HTTP_OK) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(data[1]).getAsJsonObject();
                    JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject object = jsonArray.get(i).getAsJsonObject();
                        if (!arrayList.contains(object.get("id").getAsInt())) {
                            arrayList.add(object.get("id").getAsInt());
                            userNameHashMap.put(object.get("id").getAsInt(),
                                    object.get("bidder_name").getAsString());
                            bidPriceHashMap.put(object.get("id").getAsInt(),
                                    object.get("bid").getAsString());
                        }
                    }
                }
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> integers) {
            super.onPostExecute(integers);
            mProgressBar.setVisibility(View.GONE);
            mBidsAdapter = new BidsAdapter(integers);
            mRecyclerView.setAdapter(mBidsAdapter);
        }
    }

}

