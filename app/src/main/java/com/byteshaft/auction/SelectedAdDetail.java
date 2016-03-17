package com.byteshaft.auction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to show the details if a product which will include item images, bids, rating of the seller
 * product description, price and option of chatting with the client / buyer
 */
public class SelectedAdDetail extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ArrayList<Integer> arrayList;
    public static int adPrimaryKey;
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
    public static int sBidItemPrimaryKey = 0;
    public static int itemInArray = 0;
    public static int myBidPrimaryKey = 0;
    public boolean mCanUpdate = false;
    private MenuItem item;
    public static MenuItem soldItem;
    public String productOwner = "";
    private TextView deliveryTimeTextView;
    private String delivery_time;
    private String productStatus = "";
    public static LinearLayout linearLayout;
    private int ratingValue = 0;
    private ArrayList<Integer> reviewIdList;
    private HashMap<Integer, Integer> starsSet;
    private android.support.v7.widget.AppCompatRatingBar ratingBar;
    public String winner = "";
    public ArrayList<Double> bidsList;
    public static SelectedAdDetail instance;

    public static SelectedAdDetail getInstance() {
        return instance;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detials);
        instance = this;
        productImageView = new ProductImageView();
        adPrimaryKey = getIntent().getIntExtra(AppGlobals.detail, 0);
        String productName = getIntent().getStringExtra(AppGlobals.SINGLE_PRODUCT_NAME);
        if (productName != null) {
            setTitle(productName.toLowerCase());
        }
        descriptionTextView = (TextView) findViewById(R.id.ad_description);
        deliveryTimeTextView = (TextView) findViewById(R.id.delivery_time);
        linearLayout = (LinearLayout) findViewById(R.id.post_bid_layout);
        imagesUrls = new ArrayList<>();
        userNameHashMap = new HashMap<>();
        bidPriceHashMap = new HashMap<>();
        reviewIdList = new ArrayList<>();
        starsSet = new HashMap<>();
        bidsList = new ArrayList<>();
        placeBidButton = (Button) findViewById(R.id.place_bid);
        placeBidEditText = (EditText) findViewById(R.id.bid_editText);
        placeBidEditText.setHint(R.string.place_bid);
        placeBidButton.setOnClickListener(this);
        adPrice = (TextView) findViewById(R.id.ad_price);
        mProgressBar = (ProgressBar) findViewById(R.id.bid_loading_progress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.bids_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
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
            case R.id.user_info_button:
                userInfoDialog();
                break;
            case R.id.chat_button:
                Intent intent = new Intent(getApplicationContext(), Messages.class);
                System.out.println(adPrimaryKey);
                intent.putExtra(AppGlobals.PRIMARY_KEY, adPrimaryKey);
                intent.putExtra(AppGlobals.PRODUCT_OWNER, productOwner);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_for_user, menu);
        item = menu.findItem(R.id.chat_button);
        item.setVisible(false);
        soldItem = menu.findItem(R.id.sold);
        soldItem.setVisible(false);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.place_bid:
                System.out.println(placeBidEditText.getText().toString().trim().isEmpty());
                if (placeBidEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SelectedAdDetail.this, "please enter some amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                String onlyAmount = price.replace(".00", "");
                System.out.println(onlyAmount);
                int productPrice = Integer.valueOf(onlyAmount);
                int biddingAmount = Integer.valueOf(placeBidEditText.getText().toString());
                if (biddingAmount <= productPrice) {
                    Helpers.alertDialog(SelectedAdDetail.this, "", "price must be higher " +
                            "than product price");
                    return;
                }

                for (Double bids : bidsList) {
                    System.out.println(bids.intValue());
                    System.out.println(biddingAmount);
                    if (biddingAmount <= bids.intValue()) {
                        Toast.makeText(SelectedAdDetail.this, "Bidding amount must be higher " +
                                "than other bids", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (!placeBidEditText.getText().toString().trim().isEmpty() &&
                        TextUtils.isDigitsOnly(placeBidEditText.getText().toString()) && !mCanUpdate) {
                    setHideSoftKeyboard(placeBidEditText);
                    String bid = placeBidEditText.getText().toString();
                    new PlaceBidTask().execute(bid);
                }
                if (!placeBidEditText.getText().toString().trim().isEmpty() &&
                        TextUtils.isDigitsOnly(placeBidEditText.getText().toString()) && mCanUpdate) {
                    String bid = placeBidEditText.getText().toString();
                    setHideSoftKeyboard(placeBidEditText);
                    new UpdateBidTask().execute(bid);
                }
                break;
        }
    }

    private void setHideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Task to get single ad details like, title, description, images etc
     */
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
                                    + File.separator + AppGlobals.SINGLE_AD_DETAILS_APPEND_END
                                    + adPrimaryKey + "/",
                            userName, password);
                    if (HttpURLConnection.HTTP_OK == Integer.valueOf(strings[0])) {
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = jsonParser.parse(strings[1]).getAsJsonObject();
                        System.out.println(jsonObject);
                        id = jsonObject.get("id").getAsInt();
                        description = jsonObject.get("description").getAsString();
                        price = jsonObject.get("price").getAsString();
                        title = jsonObject.get("title").getAsString();
                        currency = jsonObject.get("currency").getAsString();
                        productOwner = jsonObject.get("owner").getAsString();
                        if (!jsonObject.get("sold").isJsonNull()) {
                            productStatus = jsonObject.get("sold").getAsString();
                        }
                        if (!jsonObject.get("sold_to").isJsonNull()) {
                            winner = jsonObject.get("sold_to").getAsString();
                        }
                        delivery_time = jsonObject.get("delivery_time").getAsString();
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
            String link = AppGlobals.REVIEW_URL + productOwner + File.separator + "reviews"
                    + File.separator;
            new GetSellerRating().execute(link);
            descriptionTextView.setText("Description: \n \n" + description);
            adPrice.setText(price + currency);
            deliveryTimeTextView.setText(delivery_time + "H");
            LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
            setTitle(title);
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
            System.out.println(productOwner == null);
            if (productOwner.equals(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME))) {
                item.setVisible(true);
                linearLayout.setVisibility(View.GONE);
            }
            new GetBidsTask().execute();
            if (productStatus.equals("true") || !winner.trim().isEmpty()) {
                linearLayout.setVisibility(View.GONE);
                soldItem.setVisible(true);
            }
        }
    }

    /**
     * Custom class that extends RecyclerView adapter
     */
    class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.BidView>
            implements Button.OnClickListener {
        private BidView bidView;
        private ArrayList<Integer> items;
        private Activity mActivity;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete_bid_button:
                    AlertDialog.Builder alertDialogBuilder =
                            new AlertDialog.Builder(mActivity);
                    alertDialogBuilder.setTitle("Delete Bid");
                    alertDialogBuilder
                            .setMessage("Are you sure you want to delete your bid?")
                            .setCancelable(false)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new DeleteBidTask().execute();
                                    dialog.dismiss();
                                }
                            });
                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;
            }

        }

        /**
         * Custom bids adapter to display bids on ad.
         *
         * @param data
         * @param activity
         */
        public BidsAdapter(ArrayList<Integer> data, Activity activity) {
            super();
            this.items = data;
            this.mActivity = activity;
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
            if (!bidPriceHashMap.get(items.get(position)).contains(".00")) {
                bidView.textView.setText(bidPriceHashMap.get(items.get(position)) + ".00");
            } else {
                bidView.textView.setText(bidPriceHashMap.get(items.get(position)));
            }
            bidView.bidderTextView.setText(userNameHashMap.get(items.get(position)));
            if (Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME)
                    .equalsIgnoreCase(userNameHashMap.get(items.get(position)))) {
                bidView.deleteBidButton.setVisibility(View.VISIBLE);
                bidView.deleteBidButton.setOnClickListener(this);
                sBidItemPrimaryKey = position;
                itemInArray = items.get(position);
                placeBidEditText.setHint(R.string.update_bid);
                myBidPrimaryKey = items.get(position);
                mCanUpdate = true;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class BidView extends RecyclerView.ViewHolder {
            public TextView textView;
            public TextView bidderTextView;
            public TextView invisibleBidPrimaryKey;
            public Button deleteBidButton;

            public BidView(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.bid_text_View);
                bidderTextView = (TextView) itemView.findViewById(R.id.bidder_user_name);
                invisibleBidPrimaryKey = (TextView) itemView.findViewById(R.id.invisible_bid_primary_key);
                deleteBidButton = (Button) itemView.findViewById(R.id.delete_bid_button);
            }
        }
    }


    /**
     * Task to place a bid on ad
     */
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
                System.out.println(url);
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
                Helpers.alertDialog(SelectedAdDetail.this, "Conflict", "please place your bid again");
            } else {
                Helpers.alertDialog(SelectedAdDetail.this, "Error", "There was an unexpected error");
            }
        }
    }

    /**
     * Task to get ad specific bids
     */
    class GetBidsTask extends AsyncTask<String, String, ArrayList<Integer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

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
                            if (!productOwner
                                    .equals(Helpers.getStringDataFromSharedPreference
                                            (AppGlobals.KEY_USERNAME))) {
                                arrayList.add(object.get("id").getAsInt());
                                userNameHashMap.put(object.get("id").getAsInt(),
                                        object.get("bidder_name").getAsString());
                                bidPriceHashMap.put(object.get("id").getAsInt(),
                                        object.get("bid").getAsString());
                                bidsList.add(object.get("bid").getAsDouble());
                            }
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
            mBidsAdapter = new BidsAdapter(arrayList, SelectedAdDetail.this);
            mRecyclerView.setAdapter(mBidsAdapter);
        }
    }

    /*
    dialog to show info about ad.
     */
    public void userInfoDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(SelectedAdDetail.this);
        final View promptView = layoutInflater.inflate(R.layout.user_info_rating_bar, null);
        TextView sellerName = (TextView) promptView.findViewById(R.id.seller_user_name);
        TextView textView = (TextView) promptView.findViewById(R.id.write_review);
        System.out.println(winner);
        System.out.println(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME));
        System.out.println((winner.equals(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME))));
        System.out.println(Helpers.getBooleanValueForReview(adPrimaryKey));
        if (winner.equals(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME)) &&
                !Helpers.getBooleanValueForReview(adPrimaryKey)) {
            textView.setVisibility(View.VISIBLE);

        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog();
            }
        });
        sellerName.setText(productOwner);
        Button contactButton = (Button) promptView.findViewById(R.id.contact_button);
        ratingBar =
                (android.support.v7.widget.AppCompatRatingBar) promptView.findViewById(R.id.ratingBar);
        if (reviewIdList.size() > 0) {
            int rating = ratingValue / reviewIdList.size();
            Log.i("divided", String.valueOf(rating));
            ratingBar.setRating(Float.parseFloat(String.valueOf(rating)));
        }
        if (productOwner.equals(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME))) {
            contactButton.setVisibility(View.GONE);
        }
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                System.out.println(productOwner);
                intent.putExtra(AppGlobals.PRODUCT_OWNER, productOwner);
                intent.putExtra(AppGlobals.PRIMARY_KEY, adPrimaryKey);
                startActivity(intent);
            }
        });
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectedAdDetail.this);
        alertDialog.setView(promptView);
        alertDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    /*
    dialog to get review and sent to seller.
     */
    public void reviewDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(SelectedAdDetail.this);
        final View promptView = layoutInflater.inflate(R.layout.review_dialog_layout, null);
        final RatingBar reviewRatingBar = (RatingBar) promptView.findViewById(R.id.review_bar);
        final EditText reviewEditText = (EditText) promptView.findViewById(R.id.review_edittext);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectedAdDetail.this);
        alertDialog.setView(promptView);
        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (reviewEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "please write a review", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!reviewEditText.getText().toString().trim().isEmpty()) {
                    String[] reviewData = {String.valueOf(reviewRatingBar.getRating()), reviewEditText.getText().toString()};
                    new SendReviewTask().execute(reviewData);
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    /**
     * Task to delete a bid
     */
    class DeleteBidTask extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int responseCode = 0;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String url = AppGlobals.DELETE_UPDATE_BID_URL +
                        Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME) + "/ads/"
                        + adPrimaryKey + "/bids/" + myBidPrimaryKey;
                System.out.println(url);
                try {
                    responseCode = Helpers.simpleDeleteRequest(url);
                    System.out.println(responseCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mProgressBar.setVisibility(View.GONE);
            placeBidEditText.setHint(R.string.place_bid);
            if (integer == HttpURLConnection.HTTP_NO_CONTENT) {
                if (itemInArray != 0) {
                    arrayList.remove(arrayList.indexOf(itemInArray));
                }
                if (sBidItemPrimaryKey != 0) {
                    mRecyclerView.removeViewAt(sBidItemPrimaryKey);
                }
            }
        }
    }

    /**
     * Task to update a bid.
     */
    class UpdateBidTask extends AsyncTask<String, String, Integer> {
        private String updatedAmount;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int responseCode = 0;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String url = AppGlobals.DELETE_UPDATE_BID_URL +
                        Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME) + "/ads/"
                        + adPrimaryKey + "/bids/" + myBidPrimaryKey;
                try {
                    updatedAmount = params[0];
                    responseCode = Helpers.simplePutRequest(url, "bid", params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mProgressBar.setVisibility(View.GONE);
            if (integer == HttpURLConnection.HTTP_OK) {
                bidPriceHashMap.remove(myBidPrimaryKey);
                bidPriceHashMap.put(myBidPrimaryKey, updatedAmount);
                mRecyclerView.removeAllViews();
                mBidsAdapter = new BidsAdapter(arrayList, SelectedAdDetail.this);
                mRecyclerView.setAdapter(mBidsAdapter);
                placeBidEditText.setText("");
                Toast.makeText(SelectedAdDetail.this, "Bid Updated!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class GetSellerRating extends AsyncTask<String, String, String> {

        private String next;

        @Override
        protected String doInBackground(String... params) {
            String[] data;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                try {
                    data = Helpers.simpleGetRequest(params[0], userName, password);
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(data[1]).getAsJsonObject();
                    if (!jsonObject.get("next").isJsonNull()) {
                        next = jsonObject.get("next").getAsString();
                    } else {
                        next = "";
                    }
                    JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jObject = jsonArray.get(i).getAsJsonObject();
                        if (!reviewIdList.contains(jObject.get("reviewer").getAsInt())) {
                            reviewIdList.add(jObject.get("reviewer").getAsInt());
                            starsSet.put(jObject.get("reviewer").getAsInt(),
                                    jObject.get("stars").getAsInt());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!next.equals("")) {
                new GetSellerRating().execute(next);
            } else {
                int value = 0;
                for (int id : reviewIdList) {
                    int currentStarValue = starsSet.get(id);
                    value = value + currentStarValue;
                }
                ratingValue = value;
                System.out.println(ratingValue);
            }
        }
    }

    /**
     * Method returns json formatted data to send to server
     *
     * @param starsValue
     * @param message
     * @return
     */
    private String getJsonObjectString(String starsValue, String message) {
        return String.format("{\"stars\": \"%s\" , \"review\": \"%s\"}",
                starsValue, message);
    }

    /*
    Task to send review to seller.
     */
    class SendReviewTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String link = AppGlobals.REVIEW_URL + productOwner + File.separator +
                        "reviews" + File.separator;
                try {
                    URL url;
                    url = new URL(link);
                    HttpURLConnection connection;
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod("POST");
                    String authString = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME)
                            + ":" + Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                    String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
                    connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
                    String jsonFormattedData = getJsonObjectString(params[0], params[1]);
                    Helpers.sendRequestData(connection, jsonFormattedData);
                    return connection.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return AppGlobals.NO_INTERNET;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            System.out.println(integer);
            if (integer == HttpURLConnection.HTTP_CREATED) {
                Helpers.saveBooleanForReview(adPrimaryKey, true);
                Toast.makeText(getApplicationContext(), "You have given your review",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

