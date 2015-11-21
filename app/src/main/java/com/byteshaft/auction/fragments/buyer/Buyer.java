package com.byteshaft.auction.fragments.buyer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.auction.MainActivity;
import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;

import java.util.ArrayList;

public class Buyer extends Fragment {

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private ArrayList<String> arrayList;
    private static final String TAG = "RecyclerViewFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.buyer_category_fragment, container, false);
        mBaseView.setTag(TAG);
        // BEGIN_INCLUDE(initializeRecyclerView)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mBaseView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green,
                R.color.colorPrimary, R.color.gray);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        System.out.println(mRecyclerView == null);
        arrayList = new ArrayList<>();
        arrayList.add("Mobile");
        arrayList.add("Electronics");
        arrayList.add("Vehicle");
        arrayList.add("Real State");
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println(MainActivity.isLastFragmentAvailable);
        if (MainActivity.isLastFragmentAvailable) {
            MainActivity.loginButton.setVisibility(View.INVISIBLE);
            MainActivity.registerButton.setVisibility(View.INVISIBLE);
            MainActivity.loginButton.setEnabled(false);
            MainActivity.registerButton.setEnabled(false);
        }
    }

    private void refreshContent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                arrayList.add("test");
                mAdapter = new CustomAdapter(arrayList);
                mRecyclerView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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
        mRecyclerView.addOnItemTouchListener(new CustomAdapter(arrayList , AppGlobals.getContext(), new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItem(View view, String item) {
                System.out.println(item);

            }
        }));
    }

    static class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<String> items;
        private CustomView viewHolder;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public interface OnItemClickListener {
            void onItem(View view, String item);
        }

        public CustomAdapter(ArrayList<String> categories, Context context, OnItemClickListener listener) {
            this.items = categories;
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_category_item, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }


        private Drawable getImageForCategory(String item) {
            switch (item) {
                case "Mobile":
                    return AppGlobals.getContext().getResources().getDrawable(R.drawable.mobile);
                case "Electronics":
                    return AppGlobals.getContext().getResources().getDrawable(R.drawable.electronics);
                case "Vehicle":
                    return AppGlobals.getContext().getResources().getDrawable(R.drawable.vehicle);
                case "Real State":
                    return AppGlobals.getContext().getResources().getDrawable(R.drawable.real_state);
                default:
                    return AppGlobals.getContext().getResources().getDrawable(R.drawable.not_found);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            viewHolder.textView.setText(items.get(position));
            viewHolder.imageView.setImageDrawable(getImageForCategory(items.get(position)));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                System.out.println(items == null);
                mListener.onItem(childView, items.get(rv.getChildPosition(childView)));
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


        public class CustomView extends RecyclerView.ViewHolder {
            public TextView textView;
            public ImageView imageView;

            public CustomView(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.category_title);
                imageView = (ImageView) itemView.findViewById(R.id.category_image);
            }
        }
    }
}
