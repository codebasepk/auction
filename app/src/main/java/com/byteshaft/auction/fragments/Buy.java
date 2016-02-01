package com.byteshaft.auction.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.auction.R;
import com.byteshaft.auction.SelectedCategoryList;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;

import java.util.ArrayList;
import java.util.Set;

/**
 * this class belongs to buyer.
 */
public class Buy extends Fragment {

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private ArrayList<String> arrayList;
    private static final String TAG = "RecyclerViewFragment";
    private Set<String> categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.buyer_category_fragment, container, false);
        categories = Helpers.getCategories();
        if (categories.contains("nothing")) {
            categories.remove("nothing");
        }
        if (categories.isEmpty()) {
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.replace(R.id.container, new CategoriesFragment());
            tx.commit();
        }
        mBaseView.setTag(TAG);
        Helpers.saveLastFragmentOpened(getClass().getSimpleName());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        for (String category: categories) {
            if (!category.isEmpty()) {
                arrayList.add(category);
            }
        }
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new CustomAdapter(arrayList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new CustomAdapter(arrayList , AppGlobals.getContext()
                , new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItem(String item) {
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        SelectedCategoryList.class);
                intent.putExtra(AppGlobals.CATEGORY_INTENT_KEY, item);
                startActivity(intent);
            }
        }));
    }

    /**
     * custom adapter to display selected categories of user
     */
    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<String> items;
        private CustomView viewHolder;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public interface OnItemClickListener {
            void onItem(String item);
        }

        public CustomAdapter(ArrayList<String> categories, Context context,
                             OnItemClickListener listener) {
            this.items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        public CustomAdapter(ArrayList<String> categories) {
            this.items = categories;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.single_category_item, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            viewHolder.textView.setText(items.get(position));
            viewHolder.imageView.setImageBitmap(Helpers.getBitMapOfProfilePic(
                    (AppGlobals.root + AppGlobals.CATEGORIES_FOLDER) + "/" +
                            (items.get(position) + ".png")));
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


        /* Member class that extends RecyclerView.ViewHolder allows us to access the elements inside
           xml it takes view in constructor
         */
        public class CustomView extends RecyclerView.ViewHolder {
            public TextView textView;
            public ImageView imageView;

            public CustomView(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.category_title);
                imageView = (ImageView) itemView.findViewById(R.id.selected_category_image);
            }
        }
    }
}
