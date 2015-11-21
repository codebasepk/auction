package com.byteshaft.auction.fragments;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.auction.R;

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
        setContentView(R.layout.specific_category);
        mBaseView.setTag(TAG);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mBaseView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green,
                R.color.colorPrimary, R.color.gray);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        new GetSpecificDataTask().execute();

    }

    class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<String> item;
        CustomView viewHolder;

        public CustomAdapter(ArrayList<String> categories) {
            this.item = categories;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_category_item, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }

        private Drawable getImageForCategory(String item) {
            switch (item) {
                case "Mobile":
                    return getResources().getDrawable(R.drawable.mobile);
                case "Electronics":
                    return getResources().getDrawable(R.drawable.electronics);
                case "Vehicle":
                    return getResources().getDrawable(R.drawable.vehicle);
                case "Real State":
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
            textView =  (TextView) itemView.findViewById(R.id.category_title);
            imageView = (ImageView) itemView.findViewById(R.id.category_image);


        }
    }

    class GetSpecificDataTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            arrayList = new ArrayList<>();
            arrayList.add("Mobile");
            arrayList.add("Electronics");
            arrayList.add("Vehicle");
            arrayList.add("Real State");
            mAdapter = new CustomAdapter(arrayList);
            mRecyclerView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }
}
