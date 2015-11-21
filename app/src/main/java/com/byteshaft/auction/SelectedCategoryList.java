package com.byteshaft.auction;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.auction.utils.AppGlobals;

import java.util.ArrayList;

public class SelectedCategoryList extends AppCompatActivity {

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private ArrayList<String> arrayList;
    private static final String TAG = "RecyclerViewFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String selectedItem = getIntent().getStringExtra(AppGlobals.selectedCategory);
        setContentView(R.layout.specific_category);
        setTitle(selectedItem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.specific_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.specific_category_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green,
                R.color.colorPrimary, R.color.gray);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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

    class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<String> item;
        private CustomView viewHolder;

        public CustomAdapter(ArrayList<String> categories) {
            this.item = categories;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.specific_category_detail, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }

        private Drawable getImageForCategory(String item) {
            switch (item) {
                case "Htc":
                    return getResources().getDrawable(R.drawable.mobile);
                case "Moto":
                    return getResources().getDrawable(R.drawable.electronics);
                case "Samsung":
                    return getResources().getDrawable(R.drawable.vehicle);
                case "LG":
                    return getResources().getDrawable(R.drawable.real_state);
                default:
                    return getResources().getDrawable(R.drawable.not_found);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            viewHolder.textView.setText(item.get(position));
            viewHolder.imageView.setImageDrawable(getImageForCategory(item.get(position)));
        }

        @Override
        public int getItemCount() {
            return item.size();
        }
    }

    public static class CustomView extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageView;
        public CustomView(View itemView) {
            super(itemView);
            textView =  (TextView) itemView.findViewById(R.id.specific_category_title);
            imageView = (ImageView) itemView.findViewById(R.id.specific_category_image);
        }
    }

    class GetSpecificDataTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            arrayList = new ArrayList<>();
            arrayList.add("Moto");
            arrayList.add("Htc");
            arrayList.add("Samsug");
            arrayList.add("LG");
            mAdapter = new CustomAdapter(arrayList);
            mRecyclerView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
