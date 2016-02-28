package com.byteshaft.auction.fragments;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class AdsDetailFragment extends Fragment {

    private View mBaseView;
    public static RecyclerView mRecyclerView;
    private CustomAdapter customAdapter;
    public static String nextUrl;
    private static HashMap<Integer, String> descriptionHashMap;
    private static ArrayList<Integer> idsArray;
    private static HashMap<Integer, String> priceHashMap;
    private static HashMap<Integer, String> imagesUrlHashMap;
    private static HashMap<Integer, String> currencyHashMap;
    private static HashMap<Integer, String> titleHashMap;
    public static CustomView customView;
    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.my_ad_details_fragment, container, false);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity().getApplicationContext());
        idsArray = new ArrayList<>();
        descriptionHashMap = new HashMap<>();
        priceHashMap = new HashMap<>();
        imagesUrlHashMap = new HashMap<>();
        currencyHashMap = new HashMap<>();
        titleHashMap = new HashMap<>();
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.my_ads_details);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        new GetAllAdsDetailTask().execute();
        return mBaseView;
    }

    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements RecyclerView.OnItemTouchListener {

        private ArrayList<Integer> items;
        private Activity mActivity;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public CustomAdapter(ArrayList<Integer> categories, Context context,
                             OnItemClickListener listener) {
            items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public void onLongPress(MotionEvent e) {
                            super.onLongPress(e);
                            View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (childView != null && mListener != null) {
                                mListener.onItemLongClick(items.get(mRecyclerView
                                        .getChildPosition(childView)), mRecyclerView
                                        .getChildPosition(childView));
                            }
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                System.out.println((rv.getChildPosition(childView)));
                System.out.println(items);
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
            void onItem(Integer item);

            void onItemLongClick(Integer adPrimaryKey, int position);
        }

        public CustomAdapter(ArrayList<Integer> categories, Activity activity) {
            this.items = categories;
            this.mActivity = activity;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.all_catagries_detail, parent, false);
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
                                            itemView.findViewById(R.id.specific_image_progressBar)
                                            .setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            if (mRecyclerView.findViewHolderForAdapterPosition(position) != null) {
                                mRecyclerView.findViewHolderForAdapterPosition(position).
                                        itemView.findViewById(R.id.specific_image_progressBar)
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

    /**
     * task to get per user ads.
     */
    class GetAllAdsDetailTask extends AsyncTask<String, String, ArrayList<Integer>> {

        private boolean internetAvailable = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("fetching your ads...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String passWod = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                try {
                    String[] response = Helpers.simpleGetRequest(AppGlobals.USER_SPECIFIC_ADS +
                            userName + AppGlobals.USER_SPECIFIC_ADS_APPEND, userName, passWod);
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
                            if (!object.get("currency").isJsonNull()) {
                                currencyHashMap.put(object.get("id").getAsInt(),
                                        object.get("currency").getAsString());
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                internetAvailable = true;
            }
            return idsArray;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> integers) {
            super.onPostExecute(integers);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;

            }
            if (internetAvailable) {
                Helpers.alertDialog(getActivity(), "No internet", "Internet Not available");
                return;
            }
            customAdapter = new CustomAdapter(idsArray, getActivity());
            mRecyclerView.setAdapter(customAdapter);
            mRecyclerView.addOnItemTouchListener(new CustomAdapter(integers,
                    getContext(), new CustomAdapter.OnItemClickListener() {
                @Override
                public void onItem(Integer item) {
                }

                @Override
                public void onItemLongClick(final Integer adPrimaryKey, final int position) {
                    System.out.println(position);
                    System.out.println(adPrimaryKey);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Do you want to delete this add?");
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    String[] data = {String.valueOf(adPrimaryKey),
                                            String.valueOf(position)};
                                    new DeleteAdTask().execute(data);
                                }
                            });

                    alertDialogBuilder.setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }));
        }
    }

    public void removeItem(int position) {
        idsArray.remove(position);
        customAdapter.notifyDataSetChanged();
    }

    class DeleteAdTask extends AsyncTask<String, String, Integer> {

        private int position = 987456123;

        @Override
        protected Integer doInBackground(String... params) {
            int status = 0;
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String url = String.format("%s%s/ads/%s/", AppGlobals.DELETE_AD_URL,
                        Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME),
                        params[0]);
                System.out.println(url);
                try {
                    status = Helpers.simpleDeleteRequest(url);
                    if (status == HttpURLConnection.HTTP_NOT_FOUND) {
                        position = Integer.valueOf(params[1]);
                    }
                    System.out.println(status);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return status;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == HttpURLConnection.HTTP_NOT_FOUND && position != 987456123) {
                removeItem(position);
            }
        }
    }
}