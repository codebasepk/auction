package com.byteshaft.auction.fragments.seller;


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

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdsDetailFragment extends Fragment {

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private CustomAdapter customAdapter;
    private String catagories;
    public static String nextUrl;
    private static HashMap<Integer, String> descriptionHashMap;
    private static ArrayList<Integer> idsArray;
    private static HashMap<Integer, String> priceHashMap;
    private static HashMap<Integer, String> imagesUrlHashMap;
    private static HashMap<Integer, String> currencyHashMap;
    private static HashMap<Integer, String> titleHashMap;
    public ArrayList<Integer> arrayList;
    public static CustomView customView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.my_ad_details_fragment, container, false);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity().getApplicationContext());
        idsArray = new ArrayList<>();
        arrayList = new ArrayList<Integer>();
        descriptionHashMap = new HashMap<>();
        priceHashMap = new HashMap<>();
        imagesUrlHashMap = new HashMap<>();
        currencyHashMap = new HashMap<>();
        titleHashMap = new HashMap<>();
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.my_ads_details);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        new GetAllAdsDetail().execute();
        return mBaseView;
    }

    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Integer> items;

        public CustomAdapter(ArrayList<Integer> categories) {
            this.items = categories;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.all_catagries_detail, parent, false);
            customView = new CustomView(view);
            return customView;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            customView.idTextView.setText(String.valueOf(items.get(position)));
            customView.titleTextView.setText(titleHashMap.get(items.get(position)));
            customView.descriptionTextView.setText(descriptionHashMap.get(items.get(position)));
            customView.priceTextView.setText(priceHashMap.get(items.get(position)));

        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView priceTextView;
        public TextView descriptionTextView;
        public TextView titleTextView;
        public ImageView imageView;
        public ProgressBar progressBar;

        public CustomView(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.all_categories_invisible_textview);
            titleTextView = (TextView) itemView.findViewById(R.id.all_categories_title);
            imageView = (ImageView) itemView.findViewById(R.id.all_categories_image);
            descriptionTextView = (TextView) itemView.findViewById(R.id.all_categories_description);
            priceTextView = (TextView) itemView.findViewById(R.id.all_categories_price);
            progressBar = (ProgressBar) itemView.findViewById(R.id.all_categories_image_progressBar);
        }
    }

    class GetAllAdsDetail extends AsyncTask<String, String, ArrayList<Integer>> {

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String passWod = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                try {
                    String[] response = Helpers.simpleGetRequest(AppGlobals.USER_SPECIFIC_ADS +
                            userName + AppGlobals.USER_SPECIFIC_ADS_APPEND, userName, passWod);
                    System.out.println(response[0]);
                    System.out.println(response[1]);
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(response[1]).getAsJsonObject();
                    if (!jsonObject.get("next").isJsonNull()) {
                        nextUrl = jsonObject.get("next").getAsString();
                    }
                    JsonArray jsonArray = jsonObject.getAsJsonArray("results");
                    System.out.println(jsonArray);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject object = jsonArray.get(i).getAsJsonObject();
                        if (!idsArray.contains(object.get("id").getAsInt())) {
                            idsArray.add(object.get("id").getAsInt());
                            titleHashMap.put(object.get("id").getAsInt(),
                                    object.get("title").getAsString());
                            descriptionHashMap.put(object.get("id").getAsInt(),
                                    object.get("description").getAsString());
                            priceHashMap.put(object.get("id").getAsInt(),
                                    object.get("price").getAsString());
                            imagesUrlHashMap.put(object.get("id").getAsInt(),
                                    object.get("photo1").getAsString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return idsArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> integers) {
            super.onPostExecute(integers);
            customAdapter = new CustomAdapter(idsArray);
            mRecyclerView.setAdapter(customAdapter);
        }
    }
}