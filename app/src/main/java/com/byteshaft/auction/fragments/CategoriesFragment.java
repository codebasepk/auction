package com.byteshaft.auction.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    private View mBaseView;
    private GridView category;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Helpers.saveLastFragmentOpend(getActivity().getClass().getSimpleName());

        mBaseView = inflater.inflate(R.layout.categories_fragment, container, false);
        Helpers.saveLastFragmentOpend(getClass().getName());
        category = (GridView) mBaseView.findViewById(R.id.category_list);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("item 1");
        arrayList.add("item 2");
        arrayList.add("item 3");
        arrayList.add("item 4");
        arrayList.add("item 5");
        arrayList.add("item 6");
        arrayList.add("item 7");
        arrayList.add("item 8");
        arrayList.add("item 9");
        arrayList.add("item 10");
        arrayList.add("item 11");
        arrayList.add("item 12");
        arrayAdapter = new CategoryArrayAdapter(AppGlobals.getContext(), R.layout.category_layout,arrayList);
        category.setAdapter(arrayAdapter);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AppGlobals.getContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });
        return mBaseView;
    }

    static class ViewHolder {
        public TextView title;
        public ImageView character;
        public CheckBox mCheckBox;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(mResources , parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.file_path);
                holder.character = (ImageView) convertView.findViewById(R.id.thumb_nail);
                holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);
                String title = categoryList.get(position);
                holder.title.setText(title);
                holder.character.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                holder.mCheckBox.setChecked(Helpers.getCategoryStatue(categoryList.get(position)));
                holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Helpers.saveCategoryStatus(categoryList.get(position), isChecked);
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }
    }

}
