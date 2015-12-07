package com.byteshaft.auction.fragments;


import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.auction.R;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private ArrayList<String> arrayList;
    private static final String TAG = "RecyclerViewFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.categories_fragment, container, false);
        mBaseView.setTag(TAG);
        setHasOptionsMenu(true);
        // BEGIN_INCLUDE(initializeRecyclerView)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.category_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        System.out.println(mRecyclerView == null);
        arrayList = new ArrayList<>();
        arrayList.add("Mobile");
        arrayList.add("Electronics");
        arrayList.add("Vehicle");
        arrayList.add("Real State");
        arrayList.add("test");
        return mBaseView;
    }
    @Override
    public void onResume() {
        super.onResume();
//        if (MainActivity.isLastFragmentAvailable) {
//            MainActivity.loginButton.setVisibility(View.INVISIBLE);
//            MainActivity.registerButton.setVisibility(View.INVISIBLE);
//            MainActivity.loginButton.setEnabled(false);
//            MainActivity.registerButton.setEnabled(false);
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.my_category_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                new UpdateCategories().execute();
                return true;
        }
        return false;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new CustomAdapter(arrayList);
        mRecyclerView.setAdapter(mAdapter);
        super.onViewCreated(view, savedInstanceState);

    }

    class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<String> item;
        CustomView viewHolder;

        public CustomAdapter(ArrayList<String> categories) {
            this.item = categories;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
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
//            viewHolder.checkBox.setChecked();
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return item.size();
        }
    }

    public static class CustomView extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageView;
        public CheckBox checkBox;
        public CustomView(View itemView) {
            super(itemView);
            textView =  (TextView) itemView.findViewById(R.id.all_category_title);
            imageView = (ImageView) itemView.findViewById(R.id.all_category_image);
            checkBox = (CheckBox) itemView.findViewById(R.id.all_categories_checkbox);
        }
    }

    class UpdateCategories extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

    }

}
