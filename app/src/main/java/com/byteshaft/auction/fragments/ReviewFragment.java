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
import com.byteshaft.auction.utils.SimpleDividerItemDecoration;
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
        String url = AppGlobals.REVIEW_URL + Helpers.getStringDataFromSharedPreference(
                AppGlobals.KEY_USERNAME) + File.separator + "reviews" + File.separator;
        new GetReviewsTask().execute(url);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.Review_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        reviewerName = new HashMap<>();
        reviewValues = new HashMap<>();
        reviewMessages = new HashMap<>();
        reviewIds = new ArrayList<>();
        return mBaseView;

    }

    class GetReviewsTask extends AsyncTask<String, String, Integer> {

        private boolean runningAgain = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("loading reviews...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            String[] data = new String[0];
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
                    System.out.println(jsonArray);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jObject = jsonArray.get(i).getAsJsonObject();
                        System.out.println();
                        if (!reviewIds.contains(jObject.get("reviewer").getAsInt())) {
                            reviewIds.add(jObject.get("reviewer").getAsInt());
                            reviewerName.put(jObject.get("reviewer").getAsInt(),
                                    jObject.get("reviewer_name").getAsString());
                            reviewMessages.put(jObject.get("reviewer").getAsInt(),
                                    jObject.get("review").getAsString());
                            reviewValues.put(jObject.get("reviewer").getAsInt(),
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
            System.out.println(next);
            if (next.contains("http://")) {
                new GetReviewsTask().execute(next);
                runningAgain = true;

            }
            if (!runningAgain && integer == HttpURLConnection.HTTP_OK) {
                mProgressDialog.dismiss();
                ReviewAdapter reviewAdapter = new ReviewAdapter(reviewIds);
                mRecyclerView.setAdapter(reviewAdapter);

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
            System.out.println(Float.parseFloat(reviewValues.get(reviewsId.get(position))));
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
