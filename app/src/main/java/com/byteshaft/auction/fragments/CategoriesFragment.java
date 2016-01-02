package com.byteshaft.auction.fragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CategoriesFragment extends Fragment {

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private ArrayList<String> arrayList;
    private static final String TAG = "RecyclerViewFragment";
    private Set<String> selectedCategories;
    private ProgressDialog mProgressDialog;
    private HashMap<String, String> linksHaspMap;
    private ArrayList<String> categoriesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.categories_fragment, container, false);
        linksHaspMap = new HashMap<>();
        categoriesList = new ArrayList<>();
        new GetCategoriesTask().execute();
        mBaseView.setTag(TAG);
        setHasOptionsMenu(true);
        if (!Helpers.getBooleanValueFromSharedPreference(AppGlobals.KEY_CATEGORIES_SELECTED)) {
            Helpers.alertDialog(getActivity(), "Category selection",
                    "select categories to view products of your interest");
            Toast.makeText(getActivity(), "please select your categories", Toast.LENGTH_LONG).show();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.category_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
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
                if (!selectedCategories.isEmpty()) {
                    if (selectedCategories.contains("nothing")) {
                        selectedCategories.remove("nothing");
                    }
                    Helpers.saveBooleanToSharedPreference(AppGlobals.KEY_CATEGORIES_SELECTED, true);
                    Helpers.saveCategories(selectedCategories);
                    String[] strings = {Helpers.getStringDataFromSharedPreference(
                            AppGlobals.KEY_USERNAME), Helpers.getStringDataFromSharedPreference(
                            AppGlobals.KEY_PASSWORD
                    )};
                    new UpdateCategories().execute(strings);
                }
                return true;
        }
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    // custom RecyclerView class for inflating customView
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
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            selectedCategories = Helpers.getCategories();
            holder.setIsRecyclable(false);
            viewHolder.textView.setText(item.get(position));
//            viewHolder.imageView.setImageDrawable(getImageForCategory(item.get(position)));
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedCategories.add(item.get(position));
                    } else {
                        selectedCategories.remove(item.get(position));
                    }
                }
            });
            if (selectedCategories.contains(item.get(position))) {
                viewHolder.checkBox.setChecked(true);
            }
        }

        @Override
        public int getItemCount() {
            return item.size();
        }
    }

        class GetCategoriesTask extends AsyncTask<String, String, ArrayList<String>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("Updating ...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                if (Helpers.isNetworkAvailable(getActivity().getApplicationContext()) &&
                        Helpers.isInternetWorking()) {
                    URL url;
                    String parsedString;
                    try {
                        url = new URL(AppGlobals.ALL_CATEGORIES);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Content-Type", "application/json");
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStream is = connection.getInputStream();
                            parsedString = Helpers.convertInputStreamToString(is);
                            JsonParser jsonParser = new JsonParser();
                            JsonObject mainJsonObject = jsonParser.parse(parsedString).getAsJsonObject();
                            JsonArray jsonArray = mainJsonObject.getAsJsonArray("results");
                            System.out.println(jsonArray);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                                categoriesList.add(String.valueOf(jsonObject.get("name")).replace("\"", ""));
                                linksHaspMap.put(String.valueOf(jsonObject.get("name")).replace("\"", ""),
                                        String.valueOf(jsonObject.get("photo")));

                            }
                            System.out.println(categoriesList);
                            System.out.println(linksHaspMap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<String> arrayList) {
                super.onPostExecute(arrayList);
                mProgressDialog.dismiss();
                mAdapter = new CustomAdapter(categoriesList);
                mRecyclerView.setAdapter(mAdapter);
            }
        }

            class GetImagesTask extends AsyncTask<String, String, String> {

                @Override
                protected String doInBackground(String... params) {
                    Bitmap bitmap = Helpers.downloadImage(params[0]);
                    if (bitmap != null) {
                        AppGlobals.addBitmapToInternalMemory(bitmap, params[1]);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                }
            }

        // custom class getting view item by giving view in constructor.
        public class CustomView extends RecyclerView.ViewHolder {
            public TextView textView;
            public ImageView imageView;
            public CheckBox checkBox;
            public ProgressBar progressBar;

            public CustomView(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.all_category_title);
                imageView = (ImageView) itemView.findViewById(R.id.all_category_image);
                checkBox = (CheckBox) itemView.findViewById(R.id.all_categories_checkbox);
                progressBar = (ProgressBar) itemView.findViewById(R.id.imageProgressBar);
            }
        }

        // member class to update categories on server
        class UpdateCategories extends AsyncTask<String, String, Integer> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("Updating ...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected Integer doInBackground(String... params) {
                HttpURLConnection connection;
                int status = 0;
                try {
                    URL url = new URL(AppGlobals.CATEGORY_URL + params[0] + "/interests/");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod("POST");
                    String authString = params[0] + ":" + params[1];
                    String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.DEFAULT);
                    connection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
                    Set<String> categories = Helpers.getCategories();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String item : categories) {
                        stringBuilder.append(item);
                        stringBuilder.append(",");
                    }
                    String jsonFormattedData = getJsonObjectString(String.valueOf(stringBuilder));
                    sendRequestData(connection, jsonFormattedData);
                    status = connection.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return status;
            }

            private String getJsonObjectString(String latitude) {

                return String.format("{\"interests\": \"%s\"}", latitude);
            }

            private void sendRequestData(HttpURLConnection connection, String body) throws IOException {
                byte[] outputInBytes = body.getBytes("UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(outputInBytes);
                os.close();
            }

            @Override
            protected void onPostExecute(Integer s) {
                super.onPostExecute(s);
                mProgressDialog.dismiss();
                Log.i(AppGlobals.getLogTag(getClass()), String.valueOf(s));
            }
        }
}
