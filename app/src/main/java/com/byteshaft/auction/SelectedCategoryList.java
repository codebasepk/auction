package com.byteshaft.auction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byteshaft.auction.receivers.MessageForDialog;
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
public class SelectedCategoryList extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

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
    private boolean refresh = false;
    private int countValue;
    private LinearLayout showMoreLinearLayout;
    private Button showMoreButton;
    private ProgressBar mShowMoreProgress;
    private String categorySpecificUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getIntent().getStringExtra(AppGlobals.SELECTED_CATEGORIES);
        setContentView(R.layout.specific_category);
        showMoreLinearLayout = (LinearLayout) findViewById(R.id.show_more_layout);
        showMoreLinearLayout.setVisibility(View.GONE);
        showMoreButton = (Button) findViewById(R.id.show_more);
        mShowMoreProgress = (ProgressBar) findViewById(R.id.show_progressBar);
        mShowMoreProgress.setVisibility(View.INVISIBLE);
        showMoreButton.setOnClickListener(this);
        categorySpecificUrl = AppGlobals.SELECTED_CATEGORY_DETAIL_URL + category.toLowerCase();
//        setTitle(category);
        initializeArrayAndHashMap();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
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
                refresh = true;
                new GetSpecificDataTask().execute(categorySpecificUrl);
            }
        });


        sRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) {
                    onScrolledToTop();
                } else if (!recyclerView.canScrollVertically(1)) {
                    onScrolledToBottom();
                } else if (dy < 0) {
                    onScrolledUp();
                } else if (dy > 0) {
                    onScrolledDown();
                }
            }
        });
        new GetSpecificDataTask().execute(categorySpecificUrl);
    }

    public void onScrolledUp() {
        System.out.println("onScrolledUp");
        showMoreLinearLayout.setVisibility(View.GONE);
    }

    public void onScrolledDown() {
        System.out.println("onScrolledDown");
    }

    public void onScrolledToTop() {
        System.out.println("onScrolledToTop");
        showMoreLinearLayout.setVisibility(View.GONE);
    }

    public void onScrolledToBottom() {
        System.out.println("onScrolledToBottom");
        System.out.println(countValue);
        System.out.println(idsArray.size());
        if (countValue > idsArray.size()) {
            showMoreLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_selected_item, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.widget.SearchView searchView =
                (android.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
//        ImageView mCloseButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
//        mCloseButton.setVisibility(View.GONE);
        int id = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView imageView = (ImageView) searchView.findViewById(id);
        imageView.setBackgroundResource(android.R.drawable.ic_menu_search);

        return true;
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

    // Method to initialize the array each time for new ads if available
    public static void initializeArrayAndHashMap() {
        idsArray = new ArrayList<>();
        descriptionHashMap = new HashMap<>();
        priceHashMap = new HashMap<>();
        imagesUrlHashMap = new HashMap<>();
        currencyHashMap = new HashMap<>();
        titleHashMap = new HashMap<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeArrayAndHashMap();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_more:
                if (countValue > 5 && !nextUrl.trim().isEmpty()) {
                    mShowMoreProgress.setVisibility(View.VISIBLE);
                    refresh = true;
                    System.out.println(nextUrl);
                    new GetSpecificDataTask().execute(nextUrl);
                }
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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
            viewHolder.price.setText(priceHashMap.get(items.get(position)) + " " +
                    currencyHashMap.get(items.get(position)));
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
                mListener.onItem(items.get(rv.getChildPosition(childView)), (TextView)
                        rv.findViewHolderForAdapterPosition(rv.getChildPosition(childView)).
                                itemView.findViewById(R.id.specific_category_title));
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
            void onItem(Integer item, TextView textView);
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

        private boolean noInternet = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!refresh) {
                mProgressDialog = new ProgressDialog(SelectedCategoryList.this);
                mProgressDialog.setMessage("loading ...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String parsedString;
                try {
                    HttpURLConnection httpURLConnection = Helpers.openConnectionForUrl(params[0], "GET");
                    System.out.println(httpURLConnection.getResponseCode());
                    InputStream inputStream = httpURLConnection.getInputStream();
                    parsedString = Helpers.convertInputStreamToString(inputStream);
                    System.out.println(parsedString);
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(parsedString).getAsJsonObject();
                    countValue = jsonObject.get("count").getAsInt();
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
                            if (!object.get("currency").isJsonNull()) {
                                currencyHashMap.put(object.get("id").getAsInt(),
                                        object.get("currency").getAsString());
                            }
                        }
                    }
                    System.out.println(currencyHashMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                noInternet = true;
            }
            return idsArray;
        }



        @Override
        protected void onPostExecute(ArrayList<Integer> idsList) {
            super.onPostExecute(idsList);
            refresh = false;
                mShowMoreProgress.setVisibility(View.INVISIBLE);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (noInternet) {
                Helpers.alertDialog(SelectedCategoryList.this, "No internet", "Internet not available",
                        AppGlobals.ACTION_FOR_SELECTED_CATEGORY);
                return;
            }
            System.out.println("countvalue:" +countValue);
            showMoreLinearLayout.setVisibility(View.VISIBLE);
            mAdapter = new CustomAdapter(idsList, SelectedCategoryList.this);
            sRecyclerView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
            sRecyclerView.addOnItemTouchListener(new CustomAdapter(idsList, getApplicationContext(),
                    new CustomAdapter.OnItemClickListener() {
                        @Override
                        public void onItem(Integer item, TextView textView) {
                            Intent intent = new Intent(getApplicationContext(), SelectedAdDetail.class);
                            intent.putExtra(AppGlobals.detail, item);
                            intent.putExtra(AppGlobals.SINGLE_PRODUCT_NAME, textView.getText());
                            startActivity(intent);
                        }
                    }));
        }
    }
}
