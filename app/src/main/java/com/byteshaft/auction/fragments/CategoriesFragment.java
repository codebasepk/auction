package com.byteshaft.auction.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.byteshaft.auction.register_login.LoginActivity;
import com.byteshaft.auction.register_login.RegisterActivity;
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
import java.util.HashSet;
import java.util.Set;

/**
 * class to show all category and you check you favourite ones and these categories will appear
 * in buyer
 */

public class CategoriesFragment extends Fragment {

    private static CategoriesAdapter sAdapter;
    private static ArrayList<String> sCategoriesList;
    private static boolean sInternetTaskInProgress = false;
    private static HashMap<String, String> sLinksHaspMap;
    private static ProgressDialog sProgressDialog;
    private static RecyclerView sRecyclerView;
    private static CustomView sViewHolder;
    private static Set<String> selectedCategories;
    private Set<String> allCategories;
    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.categories_fragment, container, false);
        AppGlobals.sCategoriesFragmentForeGround = true;
        selectedCategories = new HashSet<>();
        if (!Helpers.getBooleanValueFromSharedPreference(AppGlobals.KEY_CATEGORY_BOOLEAN_STATUS)
                && !AppGlobals.alertDialogShownOneTimeForCategory) {
            Helpers.alertDialog(getActivity(), "Category selection", "select categories to view products of your interest");
            AppGlobals.alertDialogShownOneTimeForCategory = true;
        }
        mBaseView.setTag("RecyclerViewFragment");
        setHasOptionsMenu(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        sRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.category_list);
        sRecyclerView.setLayoutManager(linearLayoutManager);
        sRecyclerView.canScrollVertically(1);
        sRecyclerView.setHasFixedSize(true);
        Log.i("ALL_CATEGORIES_STATUS", String.valueOf(Helpers.getBooleanValueFromSharedPreference(
                AppGlobals.KEY_CATEGORY_BOOLEAN_STATUS)));
        Log.i("CATEGORIES_IMAGES_SAVED", String.valueOf(Helpers.getBooleanValueFromSharedPreference(
                AppGlobals.CATEGORIES_IMAGES_SAVED)));
        if (!Helpers.getBooleanValueFromSharedPreference(AppGlobals.KEY_CATEGORY_BOOLEAN_STATUS)
                || !Helpers.getBooleanValueFromSharedPreference(AppGlobals.CATEGORIES_IMAGES_SAVED)) {
            sInternetTaskInProgress = true;
            (new GetCategoriesTask(getActivity())).execute();
        } else {
            sLinksHaspMap = new HashMap<>();
            sCategoriesList = new ArrayList<>();
            allCategories = Helpers.getSavedStringSet(AppGlobals.ALL_CATEGORIES);
            for (String item : allCategories) {
                if (!item.isEmpty()) {
                    sCategoriesList.add(item);
                }
            }
            selectedCategories = Helpers.getCategories();
            sAdapter = new CategoriesAdapter(sCategoriesList);
            sRecyclerView.setAdapter(sAdapter);
        }
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
    static class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<String> item;

        public CategoriesAdapter(ArrayList<String> categories)   {
            this.item = categories;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
            sViewHolder = new CustomView(view);
            return sViewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            sViewHolder.textView.setText(item.get(position));
            sViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedCategories.add(item.get(position));
                        System.out.println(item.get(position));
                    } else {
                        selectedCategories.remove(item.get(position));
                        System.out.println(item.get(position));
                    }
                }
            });
            if (selectedCategories.contains(item.get(position))) {
                sViewHolder.checkBox.setChecked(true);
            }

            if (!CategoriesFragment.sInternetTaskInProgress && (item.get(position))
                    .equals(CategoriesFragment.sViewHolder.textView.getText())) {
                CategoriesFragment.sViewHolder.imageView.setImageBitmap(Helpers
                        .getBitMapOfProfilePic((new StringBuilder()).append(AppGlobals.root)
                                .append("/categories_folder").append("/").append(item.get(position))
                                .append(".png").toString()));
                CategoriesFragment.sViewHolder.progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return item.size();
        }
    }

    /*
    Task to get all present categories on server
     */
    public static class GetCategoriesTask extends AsyncTask<String, String, ArrayList<String>> {

        private Activity mActivity;

        public GetCategoriesTask(Activity activity) {
            mActivity = activity;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (AppGlobals.sCategoriesFragmentForeGround) {
                sProgressDialog = new ProgressDialog(mActivity);
                sProgressDialog.setMessage("Updating ...");
                sProgressDialog.setIndeterminate(false);
                sProgressDialog.setCancelable(false);
                sProgressDialog.show();
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                sLinksHaspMap = new HashMap<>();
                sCategoriesList = new ArrayList<>();
                URL url;
                String parsedString;
                try {
                    url = new URL(AppGlobals.ALL_CATEGORIES_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Content-Type", "application/json");
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream is = connection.getInputStream();
                        parsedString = Helpers.convertInputStreamToString(is);
                        JsonParser jsonParser = new JsonParser();
                        JsonObject mainJsonObject = jsonParser.parse(parsedString).getAsJsonObject();
                        JsonArray jsonArray = mainJsonObject.getAsJsonArray("results");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                            sCategoriesList.add(jsonObject.get("name").getAsString());
                            sLinksHaspMap.put(jsonObject.get("name").getAsString(),
                                    jsonObject.get("photo").getAsString());
                        }
                        Set<String> stringSet = new HashSet<>();
                        for (String category : sCategoriesList) {
                            if (!category.isEmpty()) {
                                stringSet.add(category);
                            }
                        }
                        Helpers.saveStringSet(AppGlobals.ALL_CATEGORIES, stringSet);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sCategoriesList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            Log.i("POST_EXECUTE", "that");
            if (CategoriesFragment.sProgressDialog != null &&
                    AppGlobals.sCategoriesFragmentForeGround) {
                CategoriesFragment.sProgressDialog.dismiss();
            }
            System.out.println(arrayList.size());
            if (arrayList.size() > 0) {
                if (AppGlobals.sCategoriesFragmentForeGround) {
                    sAdapter = new CategoriesAdapter(arrayList);
                    CategoriesFragment.sRecyclerView.setAdapter(CategoriesFragment.sAdapter);
                }
                for (String item : arrayList) {
                    String[] data = {sLinksHaspMap.get(item), item};
                    Log.i("IMAGE TASK", "running");
                    new GetImagesTask().execute(data);
                }
                Helpers.saveBooleanToSharedPreference(AppGlobals.KEY_CATEGORY_BOOLEAN_STATUS, true);
                Log.i(AppGlobals.getLogTag(getClass()), "categories saved");
            }
        }
    }

    /*
    Task to get images for each categories
     */
    public static class GetImagesTask extends AsyncTask<String, String, String> {

        private boolean imagesSaved = false;

        @Override
        protected String doInBackground(String... params) {
            System.out.println(params[0]);
            Bitmap bitmap = Helpers.downloadImage(params[0]);
            if (bitmap != null) {
                AppGlobals.addBitmapToInternalMemory(bitmap, (params[1] + ".png"),
                        AppGlobals.CATEGORIES_FOLDER);
            }
            if (Helpers.getCategoriesImagesCount() == sCategoriesList.size()) {
                Helpers.saveBooleanToSharedPreference(AppGlobals.CATEGORIES_IMAGES_SAVED, true);
                Log.i(AppGlobals.getLogTag(getClass()), "images Saved");
                imagesSaved = true;
            }
            return params[1];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("Category Fragment", String.valueOf(AppGlobals.sCategoriesFragmentForeGround));
            if (AppGlobals.sCategoriesFragmentForeGround &&
                    s.equals(CategoriesFragment.sViewHolder.textView.getText().toString())) {
                CategoriesFragment.sViewHolder.imageView.setImageBitmap(
                        Helpers.getBitMapOfProfilePic((new StringBuilder()).append(AppGlobals.root)
                                .append("/categories_folder").append("/").append(s)
                                .append(".png").toString()));
                CategoriesFragment.sViewHolder.progressBar.setVisibility(View.GONE);
            }
            if (imagesSaved && Helpers.getCategoriesImagesCount() == sCategoriesList.size()) {
                if (LoginActivity.sProgressDialog != null) {
                    LoginActivity.sProgressDialog.dismiss();
                }
                if (RegisterActivity.sRegisterProgressDialog != null && AppGlobals.sRegisterProcess) {
                    RegisterActivity.sRegisterProgressDialog.dismiss();
                    RegisterActivity.getInstance().finish();
                }
                LoginActivity.getLoginActivityInstance().finish();

                Log.i("LoginActivity", "closed");
            }
            if (LoginActivity.sProgressDialog != null) {
                LoginActivity.sProgressDialog.dismiss();
            }
        }
    }

    // custom class getting view item by giving view in constructor.
    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public CheckBox checkBox;
        public ProgressBar progressBar;

        public CustomView(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.all_category_title);
            imageView = (ImageView) itemView.findViewById(R.id.category_image);
            checkBox = (CheckBox) itemView.findViewById(R.id.all_categories_checkbox);
            progressBar = (ProgressBar) itemView.findViewById(R.id.imageProgressBar);
        }
    }

    // member class to update categories on server
    class UpdateCategories extends AsyncTask<String, String, Integer> {

        private ProgressDialog mProgressDialog;

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
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
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
            } else {
                return AppGlobals.NO_INTERNET;
            }
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
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Log.i(AppGlobals.getLogTag(getClass()), String.valueOf(s));
            if (s == AppGlobals.NO_INTERNET) {
                Helpers.alertDialog(getActivity(), "No internet", "Internet not available");
                return;
            }
            if (s == HttpURLConnection.HTTP_OK) {
                Helpers.saveBooleanToSharedPreference(AppGlobals.KEY_SELECTED_CATEGORY_BOOLEAN_STATUS, true);
                Log.i("OnPostExecute", String.valueOf(selectedCategories));
                Helpers.saveCategories(selectedCategories);
                Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.container, new Buy());
                tx.commit();
            }
        }
    }
}
