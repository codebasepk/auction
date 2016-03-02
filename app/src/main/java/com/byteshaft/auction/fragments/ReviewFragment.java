package com.byteshaft.auction.fragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class ReviewFragment extends Fragment{

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private HashMap<Integer, String> reviewerName;
    private HashMap<Integer, String> reviewValues;
    private HashMap<Integer, String> reviewMessages;
    private ProgressDialog mProgressDialog;
    public ArrayList<Integer> reviewIds;
    public String next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.review_layout, container, false);
        new GetReviewsTask().execute();
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.Review_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        reviewerName = new HashMap<>();
        reviewValues = new HashMap<>();
        reviewMessages = new HashMap<>();
        return mBaseView;

    }

    class GetReviewsTask extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("loading reviews...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String[] data = new String[0];
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                String url = AppGlobals.REVIEW_URL + userName + File.separator + "reviews"
                        + File.separator;
                try {
                    data = Helpers.simpleGetRequest(url, userName, password);
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
                        if (!reviewIds.contains(jsonObject.get("reviewer").getAsInt())) {
                            reviewIds.add(jsonObject.get("reviewer").getAsInt());
                            reviewerName.put(jsonObject.get("reviewer").getAsInt(),
                                    jObject.get("reviewer_name").getAsString());
                            reviewMessages.put(jsonObject.get("reviewer").getAsInt(),
                                    jObject.get("review").getAsString());
                            reviewValues.put(jsonObject.get("reviewer").getAsInt(),
                                    jObject.get("stars").getAsString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Integer.valueOf(data[0]);
            } else {
                return AppGlobals.NO_INTERNET;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mProgressDialog.dismiss();
            if (integer == HttpURLConnection.HTTP_OK) {
                

            } else if (integer == AppGlobals.NO_INTERNET) {
                Helpers.alertDialog(getActivity(), "No internet", "please check internet connection");
            }
        }
    }

    class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private CustomView customView;
        private ArrayList<Integer> reviewsId;

        public ReviewAdapter(ArrayList<Integer> reviewList) {
            reviewsId = reviewList;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.single_review, parent, false);
            customView = new CustomView(view);
            return customView;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            customView.idTextView.setText(String.valueOf(reviewsId.get(position)));
            customView.reviewer.setText(reviewerName.get(reviewsId.get(position)));
            customView.ratingBar.setRating(Float.parseFloat(reviewValues.get(reviewsId.get(position))));
            customView.ratingBar.setStepSize(Float.parseFloat(reviewValues.get(reviewsId.get(position))));

        }

        @Override
        public int getItemCount() {
            return reviewsId.size();
        }
    }

    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView reviewer;
        public android.support.v7.widget.AppCompatRatingBar ratingBar;

        public CustomView(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.invisible_id_review);
            reviewer = (TextView) itemView.findViewById(R.id.reviewer_name);
            ratingBar = (android.support.v7.widget.AppCompatRatingBar) itemView.findViewById(R.id.rating_bar);
        }
    }


}
