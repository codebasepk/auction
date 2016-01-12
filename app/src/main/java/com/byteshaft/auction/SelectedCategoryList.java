package com.byteshaft.auction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity that represent the selected categories of user
 */
public class SelectedCategoryList extends AppCompatActivity {

    public static RecyclerView sRecyclerView;
    private CustomAdapter mAdapter;
    private ArrayList<String> arrayList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String category;
    private String nextUrl;
    private static HashMap<Integer, String> descriptionHashMap;
    private static ArrayList<Integer> idsArray;
    private static HashMap<Integer, String> priceHashMap;
    private static HashMap<Integer, String> imagesUrlHashMap;
    private static HashMap<Integer, String> currencyHashMap;
    private static HashMap<Integer, String> titleHashMap;
    private static CustomView viewHolder;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getIntent().getStringExtra(AppGlobals.SELECTED_CATEGORIES);
        setContentView(R.layout.specific_category);
        setTitle(category);
        initializeArrayAndHashMap();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(getApplicationContext(), category, Toast.LENGTH_SHORT).show();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        sRecyclerView = (RecyclerView) findViewById(R.id.specific_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.specific_category_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green,
                R.color.colorPrimary, R.color.gray);
        sRecyclerView.setLayoutManager(linearLayoutManager);
        sRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        sRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeArrayAndHashMap();
                new GetSpecificDataTask().execute();
            }
        });
        new GetSpecificDataTask().execute();
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

    public static void initializeArrayAndHashMap() {
        idsArray = new ArrayList<>();
        descriptionHashMap = new HashMap<>();
        priceHashMap = new HashMap<>();
        imagesUrlHashMap = new HashMap<>();
        currencyHashMap = new HashMap<>();
        titleHashMap = new HashMap<>();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initializeArrayAndHashMap();
        finish();
    }

    // custom Member class to represent the categories selected by user and its images
    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<Integer> items;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;
        private Activity mActivity;

        public CustomAdapter(ArrayList<Integer> categories, Context context, OnItemClickListener listener) {
            this.items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        public CustomAdapter(ArrayList<Integer> categories, Activity activity) {
            this.items = categories;
            this.mActivity = activity;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.specific_category_detail, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            viewHolder.idTextView.setText(String.valueOf(items.get(position)));
            viewHolder.titleTextView.setText(titleHashMap.get(items.get(position)).toUpperCase());
            viewHolder.description.setText(descriptionHashMap.get(items.get(position)));
            viewHolder.price.setText(priceHashMap.get(items.get(position)));
            Picasso.with(mActivity)
                    .load(imagesUrlHashMap.get(items.get(position)))
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (sRecyclerView.findViewHolderForAdapterPosition(position) != null) {
                                sRecyclerView.findViewHolderForAdapterPosition(position).
                                        itemView.findViewById(R.id.specific_image_progressBar)
                                        .setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {
                            if (sRecyclerView.findViewHolderForAdapterPosition(position) != null) {
                                System.out.println(sRecyclerView.findViewHolderForAdapterPosition(position) == null);
                                sRecyclerView.findViewHolderForAdapterPosition(position).
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

        public interface OnItemClickListener {
            void onItem(Integer item);
        }
    }

    // custom viewHolder to access xml elements requires a view in constructor
    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView titleTextView;
        public ImageView imageView;
        public TextView description;
        public TextView price;
        public ProgressBar progressBar;

        public CustomView(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.id_invisible_textView);
            titleTextView = (TextView) itemView.findViewById(R.id.specific_category_title);
            imageView = (ImageView) itemView.findViewById(R.id.specific_category_image);
            description = (TextView) itemView.findViewById(R.id.specific_category_description);
            price = (TextView) itemView.findViewById(R.id.specific_category_price);
            progressBar = (ProgressBar) itemView.findViewById(R.id.specific_image_progressBar);
        }
    }

    class GetSpecificDataTask extends AsyncTask<String, String, ArrayList<Integer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(SelectedCategoryList.this);
            mProgressDialog.setMessage("fetching ads...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String parsedString;
                try {
                    HttpURLConnection httpURLConnection = Helpers.openConnectionForUrl(
                            AppGlobals.SELECTED_CATEGORY_DETAIL_URL + category.toLowerCase(), "GET");
                    InputStream inputStream = httpURLConnection.getInputStream();
                    parsedString = Helpers.convertInputStreamToString(inputStream);
                    System.out.println(parsedString);
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(parsedString).getAsJsonObject();
                    if (!jsonObject.get("next").isJsonNull()) {
                        nextUrl = jsonObject.get("next").getAsString();
                    }
                    JsonArray jsonArray = jsonObject.getAsJsonArray("results");
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
        protected void onPostExecute(ArrayList<Integer> idsList) {
            super.onPostExecute(idsList);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            mAdapter = new CustomAdapter(idsList, SelectedCategoryList.this);
            sRecyclerView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
            sRecyclerView.addOnItemTouchListener(new CustomAdapter(idsList, getApplicationContext(),
                    new CustomAdapter.OnItemClickListener() {
                        @Override
                        public void onItem(Integer item) {
                            Intent intent = new Intent(getApplicationContext(), ItemDetail.class);
                            intent.putExtra(AppGlobals.detail, item);
                            startActivity(intent);
                        }
                    }));
        }
    }
}
