package com.byteshaft.auction.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.byteshaft.auction.utils.SimpleDividerItemDecoration;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Callback;
import com.byteshaft.auction.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class UserSpecificBidsFragment extends Fragment {

    private View mBaseView;
    public static RecyclerView mRecyclerView;
    private static CustomAdapter customAdapter;
    private static HashMap<Integer, String> descriptionHashMap;
    private static ArrayList<Integer> idsArray;
    private static HashMap<Integer, String> priceHashMap;
    private static HashMap<Integer, String> imagesUrlHashMap;
    private static HashMap<Integer, String> currencyHashMap;
    private static HashMap<Integer, String> titleHashMap;
    public static HashMap<Integer, String> bidPriceHashMap;
    public ArrayList<Integer> arrayList;
    public static CustomView customView;
    public static String nextUrl;
    public static ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.user_bids_fragmet, container, false);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity().getApplicationContext());
        idsArray = new ArrayList<>();
        arrayList = new ArrayList<>();
        descriptionHashMap = new HashMap<>();
        priceHashMap = new HashMap<>();
        imagesUrlHashMap = new HashMap<>();
        currencyHashMap = new HashMap<>();
        titleHashMap = new HashMap<>();
        bidPriceHashMap = new HashMap<>();
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.user_bids);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        new GetUserSpecificBids(getActivity()).execute();
        return mBaseView;
    }

    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Integer> items;
        public Activity mActivity;

        public CustomAdapter(ArrayList<Integer> categories, Activity activity) {
            this.items = categories;
            mActivity = activity;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.user_soecific_bids_detail, parent, false);
            customView = new CustomView(view);
            return customView;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            customView.idTextView.setText(String.valueOf(items.get(position)));
            customView.titleTextView.setText(titleHashMap.get(items.get(position)).toUpperCase());
            customView.descriptionTextView.setText(descriptionHashMap.get(items.get(position)));
            customView.priceTextView.setText(priceHashMap.get(items.get(position)) + " " +
                    currencyHashMap.get(items.get(position)));
            customView.userBidsPrice.setText(bidPriceHashMap.get(items.get(position)) + " " +
                    currencyHashMap.get(items.get(position)));
            Picasso.with(mActivity)
                    .load(imagesUrlHashMap.get(items.get(position)))
                    .resize(200, 200)
                    .centerCrop()
                    .into(customView.imageView, new Callback() {

                        @Override
                        public void onSuccess() {
                            if (mRecyclerView.findViewHolderForAdapterPosition(position) != null) {
                                if (mRecyclerView.findViewHolderForAdapterPosition(position).
                                        itemView.findViewById(R.id.specific_image_progressBar) != null) {
                                    mRecyclerView.findViewHolderForAdapterPosition(position).
                                            itemView.findViewById(R.id.bids_image_progressBar)
                                            .setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            if (mRecyclerView.findViewHolderForAdapterPosition(position) != null) {
                                mRecyclerView.findViewHolderForAdapterPosition(position).
                                        itemView.findViewById(R.id.bids_image_progressBar)
                                        .setVisibility(View.GONE);
                            }
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView priceTextView;
        public TextView userBidsPrice;
        public TextView descriptionTextView;
        public TextView titleTextView;
        public ImageView imageView;
        public ProgressBar progressBar;

        public CustomView(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.bids_invisible_textview);
            titleTextView = (TextView) itemView.findViewById(R.id.bids_title);
            imageView = (ImageView) itemView.findViewById(R.id.bids_image);
            descriptionTextView = (TextView) itemView.findViewById(R.id.bids_description);
            priceTextView = (TextView) itemView.findViewById(R.id.total_price);
            userBidsPrice = (TextView) itemView.findViewById(R.id.user_bid_price);
            progressBar = (ProgressBar) itemView.findViewById(R.id.bids_image_progressBar);
        }
    }

    static class GetUserSpecificBids extends AsyncTask<String, String, ArrayList<Integer>> {

        private Activity mActivity;

        public GetUserSpecificBids(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            String[] userdata = new String[0];
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String username = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                String userBidsUrl = AppGlobals.GET_USER_SPECIFIC_BIDS + username + "/bids";
                try {
                    userdata = Helpers.simpleGetRequest(userBidsUrl, username, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Integer.valueOf(userdata[0]) == HttpURLConnection.HTTP_OK) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(userdata[1]).getAsJsonObject();
                    if (!jsonObject.get("next").isJsonNull()) {
                        nextUrl = jsonObject.get("next").getAsString();
                    }
                    JsonArray jsonArray = jsonObject.getAsJsonArray("results");
                    System.out.println(jsonArray);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        System.out.println(jsonArray + "okay");
                        JsonObject object = jsonArray.get(i).getAsJsonObject();
                        if (!idsArray.contains(object.get("id").getAsInt())) {
                            idsArray.add(object.get("id").getAsInt());
                            titleHashMap.put(object.get("id").getAsInt(),
                                    object.get("title").getAsString());
                            descriptionHashMap.put(object.get("id").getAsInt(),
                                    object.get("description").getAsString());
                            priceHashMap.put(object.get("id").getAsInt(),
                                    object.get("price").getAsString());
//                            bidPriceHashMap.put(object.get("id").getAsInt(),
//                            object.get("bidsPrice").getAsString());
                            imagesUrlHashMap.put(object.get("id").getAsInt(),
                                    object.get("photo1").getAsString());
                            if (!object.get("currency").isJsonNull()) {
                                currencyHashMap.put(object.get("id").getAsInt(),
                                        object.get("currency").getAsString());
                            }
                        }
                    }


                }
            }

            return idsArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> s) {
            super.onPostExecute(s);
            customAdapter = new CustomAdapter(idsArray, mActivity);
            mRecyclerView.setAdapter(customAdapter);
        }
    }
}
