package com.byteshaft.auction.fragments.seller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
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

import com.byteshaft.auction.MainActivity;
import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.byteshaft.auction.utils.MultiPartUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import nl.changer.polypicker.utils.ImageInternalFetcher;


public class Sell extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_CAMERA = 122;
    private static final int SELECT_FILE = 123;
    private EditText itemTitle;
    private EditText itemDescription;
    private EditText mItemAmount;
    private Button submitButton;
    private ImageButton addImageButton;
    private Spinner categorySpinner;
    private RadioGroup currencyGroup;
    private ArrayList<Uri> imagesArray;
    private View mBaseView;
    private String currency = "";
    private String category = "";
    // static categories will be removed when api is connected
    private ArrayList<String> list;
    private Set<String> categoryStringSet;
    private ProgressDialog mProgressDialog;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_GET_N_IMAGES = 14;
    private ViewGroup mSelectedImagesContainer;
    private HashSet<Uri> mMedia = new HashSet<>();
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CAMERA = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.seller_fragment, container, false);
        Helpers.saveLastFragmentOpened(getClass().getSimpleName());
        list = new ArrayList<>();
        categoryStringSet = Helpers.getSavedStringSet(AppGlobals.ALL_CATEGORY);
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
        currencyGroup = (RadioGroup) mBaseView.findViewById(R.id.currency_group);
        submitButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
        categorySpinner = (Spinner) mBaseView.findViewById(R.id.spinner);
        currencyGroup.setOnCheckedChangeListener(this);
        currency = getSelectedCurrency(currencyGroup.getCheckedRadioButtonId());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list);
        categorySpinner.setAdapter(adapter);
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
        System.out.println(category);
        return mBaseView;
    }

    // get the relevant currency method takes radioButtonId as parameter
    private String getSelectedCurrency(int buttonId) {
        if (buttonId == R.id.radio_dollar) {
            return "USD";
        } else {
            return "SAR";
        }
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
                if (imagesArray.size() < 1) {
                    Toast.makeText(AppGlobals.getContext(), "please select at least one image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (itemTitle.getText().toString().trim().isEmpty() ||
                        itemDescription.getText().toString().trim().isEmpty() ||
                        mItemAmount.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!itemTitle.getText().toString().trim().isEmpty() &&
                        !itemDescription.getText().toString().trim().isEmpty() &&
                        !mItemAmount.getText().toString().isEmpty() && imagesArray.size() > 0 &&
                        !currency.trim().isEmpty()) {
                    category = categorySpinner.getSelectedItem().toString();
                    String[] dataToUpload = {itemTitle.getText().toString(),
                            itemDescription.getText().toString(), mItemAmount.getText().toString(),
                            currency, category};
                    new PostDataTask().execute(dataToUpload);
                }
                break;
            case R.id.btn_add_image:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_ACCESS_CAMERA);
                } else {
                    getNImages();
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getNImages();

                } else {
                    Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }

    }

    private void showMedia() {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();
        final Iterator<Uri> iterator = mMedia.iterator();
        ImageInternalFetcher imageFetcher = new ImageInternalFetcher(getActivity(), 500);
        while (iterator.hasNext()) {
            Uri uri = iterator.next();
            Log.i(TAG, " uri: " + uri);
            if (mMedia.size() >= 1 && mMedia.size()<=8) {
                mSelectedImagesContainer.setVisibility(View.VISIBLE);
            }
            View imageHolder = LayoutInflater.from(getActivity()).inflate(R.layout.media_layout, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(v.getId());
                }
            });

            if (!uri.toString().contains("content://")) {
                // probably a relative uri
                uri = Uri.fromFile(new File(uri.toString()));
            }
            imageFetcher.loadImage(uri, thumbnail);
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        currency = getSelectedCurrency(checkedId);
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
                        Log.i(TAG, " uri: " + uri);
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
                    http = new MultiPartUtility(new URL(AppGlobals.POST_AD_URL + username + "/" + "ads/post"),
                            "POST", username, password);
                    http.addFormField("title", params[0]);
                    http.addFormField("description", params[1]);
                    http.addFormField("price", params[2]);
                    http.addFormField("currency", params[3]);
                    http.addFormField("category", params[4].toLowerCase());
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
                return AppGlobals.getPostProductResponse();
            } else {
                return AppGlobals.NO_INTERNET;
            }
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            System.out.println(s);
            mProgressDialog.dismiss();
            if (s.equals(201)) {
                imagesArray.clear();
                showMedia();
                Helpers.alertDialog(getActivity(), "Success!", "Your Product is posted");
                itemTitle.setText("");
                itemDescription.setText("");
                mItemAmount.setText("");
            } else if (s.equals(AppGlobals.NO_INTERNET)) {
                Helpers.alertDialog(getActivity(), "No Internet", "please check your internet" +
                        " and try again");
            }
        }
    }
}
