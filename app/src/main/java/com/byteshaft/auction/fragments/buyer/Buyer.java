package com.byteshaft.auction.fragments.buyer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.Helpers;

import java.util.ArrayList;

public class Buyer extends Fragment {

    private View mBaseView;
    private GridView category;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.buyer_faragment, container, false);
        Helpers.saveLastFragmentOpend(getClass().getName());
        category = (GridView) mBaseView.findViewById(R.id.category_list);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("1 tatti");
        arrayList.add("2 tatti");
        arrayList.add("3 tatti");
        arrayAdapter = new CategoryArrayAdapter(getContext(), R.layout.category_layout,arrayList);
        category.setAdapter(arrayAdapter);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });
        return mBaseView;
    }

    static class ViewHolder {
        public TextView title;
        public ImageView character;
    }

    class CategoryArrayAdapter extends ArrayAdapter<String> {

        int mResources;
        private ArrayList<String> categoryList;

        public CategoryArrayAdapter(Context context, int resource, ArrayList<String> itemList) {
            super(context, resource, itemList);
            this.categoryList = itemList;
            this.mResources = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(mResources , parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.FilePath);
                holder.character = (ImageView) convertView.findViewById(R.id.Thumbnail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String title = categoryList.get(position);
            holder.title.setText(title);
            holder.character.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
            return convertView;
        }
    }
}
