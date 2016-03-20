package com.byteshaft.auction.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.byteshaft.auction.utils.MultiPartUtility;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import nl.changer.polypicker.utils.ImageInternalFetcher;

/**
 * seller part where user can add title, description, images etc for ad and post the ad.
 */
public class Sell extends Fragment implements View.OnClickListener {

    private EditText itemTitle;
    private EditText itemDescription;
    private EditText mItemAmount;
    private Button submitButton;
    private ImageButton addImageButton;
    private Spinner categorySpinner;
    private ArrayList<Uri> imagesArray;
    private View mBaseView;
    private String currency = "SAR";
    private String category = "";
    // static categories will be removed when api is connected
    private ArrayList<String> list;
    private Set<String> categoryStringSet;
    private ProgressDialog mProgressDialog;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_GET_N_IMAGES = 14;
    private ViewGroup mSelectedImagesContainer;
    private HashSet<Uri> mMedia = new HashSet<>();
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CAMERA = 0;
    private EditText deliveryTimeEditText;
    private int adPrimaryKey;
    private ArrayList<String> imagesUrls;
    private String description;
    private String price;
    private String title;
    private String delivery_time;
    private String productOwner;
    private boolean updateProcess = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.seller_fragment, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int myInt = bundle.getInt("update", -1);
            if (myInt != -1) {
                adPrimaryKey = myInt;
                System.out.println(myInt);
                imagesUrls = new ArrayList<>();
                new GetItemDetailsTask().execute();
                updateProcess = true;
            }
        }
        Helpers.saveLastFragmentOpened(getClass().getSimpleName());
        list = new ArrayList<>();
        categoryStringSet = Helpers.getSavedStringSet(AppGlobals.ALL_CATEGORIES);
        for (String item : categoryStringSet) {
            if (!item.isEmpty()) {
                list.add(item);
            }
        }
        mSelectedImagesContainer = (ViewGroup) mBaseView.findViewById(R.id.selected_photos_container);
        imagesArray = new ArrayList<>(7);
        itemTitle = (EditText) mBaseView.findViewById(R.id.item_title);
        itemDescription = (EditText) mBaseView.findViewById(R.id.item_description);
        mItemAmount = (EditText) mBaseView.findViewById(R.id.item_price);
        submitButton = (Button) mBaseView.findViewById(R.id.btn_submit);
        addImageButton = (ImageButton) mBaseView.findViewById(R.id.btn_add_image);
        deliveryTimeEditText = (EditText) mBaseView.findViewById(R.id.deliverTime);
        submitButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
        categorySpinner = (Spinner) mBaseView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list);
        categorySpinner.setAdapter(adapter);
        submitButton.setText("submit");
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        categorySpinner.setSelection(0, true);
        category = (String) categorySpinner.getSelectedItem();
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (currency.equals("")) {
                    Toast.makeText(AppGlobals.getContext(), "please select currency", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imagesArray.size() < 1 && !updateProcess) {
                    Toast.makeText(AppGlobals.getContext(), "please select at least one image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (itemTitle.getText().toString().trim().isEmpty() ||
                        itemDescription.getText().toString().trim().isEmpty() ||
                        mItemAmount.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.valueOf(deliveryTimeEditText.getText().toString()) > 7) {
                    Toast.makeText(getActivity(), "delivery time must be less than a week", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!updateProcess && !itemTitle.getText().toString().trim().isEmpty() &&
                        !itemDescription.getText().toString().trim().isEmpty() &&
                        !mItemAmount.getText().toString().isEmpty() && imagesArray.size() > 0 &&
                        !currency.trim().isEmpty() && Integer.valueOf(deliveryTimeEditText.getText().toString()) <= 7) {
                    category = categorySpinner.getSelectedItem().toString();
                    String[] dataToUpload = {itemTitle.getText().toString(),
                            itemDescription.getText().toString(), mItemAmount.getText().toString(),
                            currency, category, deliveryTimeEditText.getText().toString()};
                    new PostDataTask().execute(dataToUpload);
                }
                if (updateProcess && mMedia.size() < 1) {
                    Toast.makeText(AppGlobals.getContext(), "please select at least one image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (updateProcess && !itemTitle.getText().toString().trim().isEmpty() &&
                        !itemDescription.getText().toString().trim().isEmpty() &&
                        !mItemAmount.getText().toString().isEmpty() && mMedia.size() > 0 &&
                        !currency.trim().isEmpty() &&
                        Integer.valueOf(deliveryTimeEditText.getText().toString()) <= 7) {
                    String[] dataToUpdate = {itemTitle.getText().toString(),
                            itemDescription.getText().toString(), mItemAmount.getText().toString(),
                            currency, category, deliveryTimeEditText.getText().toString()};
                    new UpdateAdTask().execute(dataToUpdate);
                }
                break;
            case R.id.btn_add_image:
                // here we check for camera permission
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // if not granted request it from user
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_ACCESS_CAMERA);
                } else {
                    getNImages();
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this code will be executed if the permission is either denied or given by the user
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // code in this statement will be executed if the requested permission is
                    // granted by the user
                    getNImages();
                } else {
                    // if not granted show something
                    Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    /*
    method to show captured images by camera or selected from gellery
     */
    private void showMedia() {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();
        final Iterator<Uri> iterator = mMedia.iterator();
        final ImageInternalFetcher imageFetcher = new ImageInternalFetcher(getActivity(), 500);
        while (iterator.hasNext()) {
            Uri uri = iterator.next();
            Log.i("TAG", " uri: " + uri);
            if (mMedia.size() >= 1 && mMedia.size() <= 8) {
                mSelectedImagesContainer.setVisibility(View.VISIBLE);
            }
            final View imageHolder = LayoutInflater.from(getActivity()).inflate(R.layout.media_layout, null);
            final ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);
            final Uri finalUri = uri;
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(v.getId());
                    mSelectedImagesContainer.removeView(imageHolder);
//                    imagesArray.clear();
                    mMedia.remove(finalUri);
                }
            });

            if (!uri.toString().contains("content://") && !uri.toString().contains("http://")) {
                // probably a relative uri
                uri = Uri.fromFile(new File(uri.toString()));
                imageFetcher.loadImage(uri, thumbnail);
                mSelectedImagesContainer.addView(imageHolder);
                // set the dimension to correctly
                // show the image thumbnail.
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getResources().getDisplayMetrics());
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getResources().getDisplayMetrics());
                thumbnail.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            } else if (uri.toString().contains("http://")) {
                Picasso.with(getActivity()).load(uri).into(thumbnail);
                mSelectedImagesContainer.addView(imageHolder);
                // set the dimension to correctly
                // show the image thumbnail.
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getResources().getDisplayMetrics());
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getResources().getDisplayMetrics());
                thumbnail.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            }
        }
    }

    /*
    Method that opens activity that allow user to capture and select images
     */
    private void getNImages() {
        Log.i("Auction", "Get Images Method called");
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.orange)
                .setSelectionLimit(8)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES || requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                if (parcelableUris == null) {
                    return;
                }
                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
                if (uris != null) {
                    for (Uri uri : uris) {
                        Log.i("TAG", " uri: " + uri);
                        if (!imagesArray.contains(uri)) {
                            imagesArray.add(uri);
                        }
                        mMedia.add(uri);
                    }
                    showMedia();
                }
            }
        }
    }

    /*
    Member class allow to send product data.
     */
    class PostDataTask extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                MultiPartUtility http;
                String username = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                try {
                    System.out.println(new URL(AppGlobals.POST_AD_URL + username + "/" + "ads/post"));
                    http = new MultiPartUtility(new URL(AppGlobals.POST_AD_URL + username + "/" + "ads/post"),
                            "POST", username, password);
                    System.out.println(params[4]);
                    http.addFormField("title", params[0]);
                    http.addFormField("description", params[1]);
                    http.addFormField("price", params[2]);
                    http.addFormField("currency", params[3]);
                    http.addFormField("category", params[4]);
                    http.addFormField("delivery_time", params[5]);
                    int photo = 1;
                    for (Uri item : imagesArray) {
                        http.addFilePart(("photo" + photo), new File(item.getPath()));
                        System.out.println(item.getPath());
                        System.out.println(photo);
                        photo++;
                    }
                    final byte[] bytes = http.finishFilesUpload();
                    for (Uri item : imagesArray) {
                        try {
                            OutputStream os = new FileOutputStream(item.getPath());
                            os.write(bytes);
                        } catch (IOException e) {

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return AppGlobals.getPostResponse();
            } else {
                return AppGlobals.NO_INTERNET;
            }
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            if (s.equals(201)) {
                imagesArray.clear();
                showMedia();
                Helpers.alertDialog(getActivity(), "Success!", "Your Product is posted");
                itemTitle.setText("");
                itemDescription.setText("");
                mItemAmount.setText("");
                deliveryTimeEditText.setText("");
            } else if (s.equals(AppGlobals.NO_INTERNET)) {
                Helpers.alertDialog(getActivity(), "No Internet", "please check your internet" +
                        " and try again");
            }
        }
    }

    /**
     * Task to get single add details using for update when user comes from adsDetailsFragment
     */
    class GetItemDetailsTask extends AsyncTask<String, String, ArrayList<Bitmap>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("loading ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                String userName = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                try {
                    String[] strings = Helpers.simpleGetRequest(AppGlobals.SINGLE_AD_DETAILS + userName
                                    + File.separator + AppGlobals.SINGLE_AD_DETAILS_APPEND_END
                                    + adPrimaryKey + "/",
                            userName, password);
                    System.out.println(AppGlobals.SINGLE_AD_DETAILS + userName
                            + File.separator + AppGlobals.SINGLE_AD_DETAILS_APPEND_END
                            + adPrimaryKey + "/");
                    if (HttpURLConnection.HTTP_OK == Integer.valueOf(strings[0])) {
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = jsonParser.parse(strings[1]).getAsJsonObject();
                        System.out.println(jsonObject);
                        description = jsonObject.get("description").getAsString();
                        price = jsonObject.get("price").getAsString();
                        category = jsonObject.get("category").getAsString();
                        title = jsonObject.get("title").getAsString();
                        currency = jsonObject.get("currency").getAsString();
                        productOwner = jsonObject.get("owner").getAsString();
                        delivery_time = jsonObject.get("delivery_time").getAsString();
                        for (int i = 1; i < 9; i++) {
                            String photoCounter = ("photo") + i;
                            if (!jsonObject.get(photoCounter).isJsonNull()) {
                                imagesUrls.add((AppGlobals.BASE_URL +
                                        jsonObject.get(photoCounter).getAsString()));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmap) {
            super.onPostExecute(bitmap);
            mProgressDialog.dismiss();
            itemTitle.setText(title);
            itemDescription.setText(description);
            mItemAmount.setText(price);
            deliveryTimeEditText.setText(delivery_time);
            getActivity().setTitle("Update");
            categorySpinner.setSelection(list.indexOf(category));
            submitButton.setText("update");
            for (String urls : imagesUrls) {
                mMedia.add(Uri.parse(urls));
            }
            showMedia();
        }
    }

    /**
     * Task to send data to update an AD.
     */
    class UpdateAdTask extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("updating ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            ArrayList<Uri> uriList = new ArrayList<>();
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                int counter = 0;
                System.out.println(mMedia);
                for (Uri uri : mMedia) {
                    if (uri.toString().contains("http://")) {
                        AppGlobals.addBitmapToInternalMemory(Helpers.downloadImage(uri.toString())
                                , counter + ".png",
                                AppGlobals.TEMP_FOLDER);
                    }
                    counter++;
                }
                MultiPartUtility http;
                String username = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME);
                String password = Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_PASSWORD);
                try {
                    System.out.println(AppGlobals.SINGLE_AD_DETAILS + username
                            + File.separator + AppGlobals.SINGLE_AD_DETAILS_APPEND_END
                            + adPrimaryKey + "/");
                    http = new MultiPartUtility(new URL(AppGlobals.SINGLE_AD_DETAILS + username
                            + File.separator + AppGlobals.SINGLE_AD_DETAILS_APPEND_END
                            + adPrimaryKey + "/"),
                            "PUT", username, password);
                    http.addFormField("title", params[0]);
                    http.addFormField("description", params[1]);
                    http.addFormField("price", params[2]);
                    http.addFormField("currency", params[3]);
                    http.addFormField("category", params[4].toLowerCase());
                    http.addFormField("delivery_time", params[5]);
                    int photo = 1;
                    File file = new File(AppGlobals.root + AppGlobals.TEMP_FOLDER);
                    File[] files = file.listFiles();
                    for (File images : files) {
                        http.addFilePart(("photo" + photo), images);
                        uriList.add(Uri.fromFile(images));
                        photo++;
                    }
                    if (imagesUrls.size() > 0) {
                        for (Uri item : imagesArray) {
                            http.addFilePart(("photo" + photo), new File(item.getPath()));
                            uriList.add(item);
                            photo++;
                        }

                    }
                    final byte[] bytes = http.finishFilesUpload();
                    for (Uri item : uriList) {
                        try {
                            OutputStream os = new FileOutputStream(item.getPath());
                            os.write(bytes);
                        } catch (IOException e) {

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return AppGlobals.getPostResponse();
            } else {
                return AppGlobals.NO_INTERNET;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mProgressDialog.dismiss();
            if (integer == HttpURLConnection.HTTP_OK) {
                imagesArray.clear();
                mMedia.clear();
                showMedia();
                Helpers.alertDialog(getActivity(), "Success!", "Your Product is Updated");
                itemTitle.setText("");
                itemDescription.setText("");
                mItemAmount.setText("");
                deliveryTimeEditText.setText("");
            }
            File file = new File(AppGlobals.root + AppGlobals.TEMP_FOLDER);
            if (file.exists()) {
                File[] folders = file.listFiles();
                for (File folder : folders) {
                    if (folder.exists()) {
                        Helpers.removeFiles(folder.getAbsolutePath());
                    }
                }
            }
        }
    }
}
