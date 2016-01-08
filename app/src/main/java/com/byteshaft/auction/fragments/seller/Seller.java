package com.byteshaft.auction.fragments.seller;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.byteshaft.auction.utils.ImageAdapter;
import com.byteshaft.auction.utils.MultiPartUtility;
import com.byteshaft.auction.utils.RealPathFromUri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;


public class Seller extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_CAMERA = 122;
    private static final int SELECT_FILE = 123;
    private EditText itemTitle;
    private EditText itemDescription;
    private EditText mItemAmount;
    private Button submitButton;
    private ImageButton addImageButton;
    private Spinner categorySpinner;
    private RadioGroup currencyGroup;
    private Gallery gallery;
    private File destination;
    private String imageUrl;
    private Bitmap imageForAd;
    //    private Uri selectedImageUri;
    private ArrayList<String> imagesArray;
    private View mBaseView;
    private String currency = "";
    private String category = "";
    // static categories will be removed when api is connected
    private ArrayList<String> list;
    private Set<String> categoryStringSet;
    private ProgressDialog mProgressDialog;
    private static Uri imageCapturedUri;
    private static File imageCapturePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.seller_fragment, container, false);
        Helpers.saveLastFragmentOpened(getClass().getSimpleName());
        list = new ArrayList<>();
        categoryStringSet = Helpers.getSavedStringSet(AppGlobals.ALL_CATEGORY);
        for (String item: categoryStringSet) {
            if (!item.isEmpty()) {
                list.add(item);
            }
        }
        imagesArray = new ArrayList<>(7);
        itemTitle = (EditText) mBaseView.findViewById(R.id.item_title);
        itemDescription = (EditText) mBaseView.findViewById(R.id.item_description);
        mItemAmount = (EditText) mBaseView.findViewById(R.id.item_price);
        submitButton = (Button) mBaseView.findViewById(R.id.btn_submit);
        addImageButton = (ImageButton) mBaseView.findViewById(R.id.btn_add_image);
        currencyGroup = (RadioGroup) mBaseView.findViewById(R.id.currency_group);
        submitButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
        // gallery View for images
        gallery = (Gallery) mBaseView.findViewById(R.id.gallery);
        categorySpinner = (Spinner) mBaseView.findViewById(R.id.spinner);
        currencyGroup.setOnCheckedChangeListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = String.valueOf(parent.getItemIdAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = String.valueOf(parent.getSelectedItem());
            }
        });
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        gallery.setAdapter(new ImageAdapter(imagesArray));
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
                    String[] dataToUpload = {itemTitle.getText().toString(),
                            itemDescription.getText().toString(), mItemAmount.getText().toString(),
                            currency, category};
                    new PostDataTask().execute(dataToUpload);
                }
                break;
            case R.id.btn_add_image:
                selectImage();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_doller) {
            currency = "dollar";
        } else if (checkedId == R.id.radio_riyal) {
            currency = "riyal";
        }
    }

    // method that shows a dialog with camera and gallery option to select or capture image
    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photos!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    File appFolder = new File(Environment.getExternalStorageDirectory().
                            getAbsolutePath() + File.separator + "Auction");
                    if (!appFolder.exists()) {
                        appFolder.mkdirs();
                    }
                    imageCapturePath = new File(appFolder, System.currentTimeMillis() + ".jpg");
                    imageCapturedUri = Uri.fromFile(imageCapturePath);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCapturedUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (!imagesArray.contains(imageCapturePath.getAbsolutePath()) && imagesArray.size() <= 7) {
                imagesArray.add(imageCapturePath.getAbsolutePath());
            }
        } else if (requestCode == SELECT_FILE) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri singleImageUri = data.getData();
                    String singlePath = RealPathFromUri.getRealPathFromURI_API19(getActivity().
                            getApplicationContext(), singleImageUri);
                    if (!imagesArray.contains(singlePath) && imagesArray.size() <= 7) {
                        imagesArray.add(singlePath);
                    }
                } else {
                    if (Build.VERSION.SDK_INT < 19) {
                        String[] imagesPath = data.getStringExtra("data").split("\\|");
                    } else if (Build.VERSION.SDK_INT > 19) {
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();
                                String path = RealPathFromUri.getRealPathFromURI_API19(getActivity().
                                        getApplicationContext(), uri);
                                if (!imagesArray.contains(path) && imagesArray.size() <= 7) {
                                    imagesArray.add(path);
                                    imageForAd = Helpers.getBitMapOfProfilePic(path);
                                    imageUrl = String.valueOf(path);
                                }
                            }

                        }
                    }
                }
            }
        }
        gallery.setAdapter(new ImageAdapter(imagesArray));
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
//                http.addFormField("currency", params[3]);
                    http.addFormField("category", params[4]);
                    int photo = 1;
                    for (String item : imagesArray) {
                        http.addFilePart(("photo" + photo), new File(item));
                        photo++;
                    }
                    final byte[] bytes = http.finishFilesUpload();
                    for (String item : imagesArray) {
                        try {
                            OutputStream os = new FileOutputStream(item);
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
            imagesArray.clear();
            mProgressDialog.dismiss();
            if (s.equals(201)) {
                Helpers.alertDialog(getActivity(), "Success!", "Your Product is posted");
                itemTitle.setText("");
                itemDescription.setText("");
                mItemAmount.setText("");
                gallery.setAdapter(new ImageAdapter(imagesArray));
            } else if (s.equals(AppGlobals.NO_INTERNET)) {

            }
        }
    }
}
