package com.byteshaft.auction.fragments.seller;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;
import com.byteshaft.auction.utils.ImageAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Seller extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_CAMERA = 122;
    private static final int SELECT_FILE = 123;
    private EditText itemTitle;
    private EditText itemDescription;
    private Button submintButton;
    private Button addImageButton;
    private Spinner categorySpinner;
    private RadioGroup currenyGroup;
    private Gallery gallery;
    private File destination;
    private String imageUrl;
    private Bitmap imageForAd;
    private Uri selectedImageUri;
    ArrayList<String> imagesArray;
    private static final int SELECT_PHOTO = 100;
    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.seller_fragment, container, false);
        Helpers.saveLastFragmentOpend(getClass().getSimpleName());

        String[] list =
                {"Mobiles","Electronics" ,"Vehicle","Real State",};

        itemTitle = (EditText) mBaseView.findViewById(R.id.item_title);
        itemDescription = (EditText) mBaseView.findViewById(R.id.item_description);
        submintButton = (Button) mBaseView.findViewById(R.id.btn_submit);
        addImageButton = (Button) mBaseView.findViewById(R.id.btn_addimage);
        currenyGroup = (RadioGroup) mBaseView.findViewById(R.id.currency_group);
        submintButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
        // gallery View for images
        gallery = (Gallery) mBaseView.findViewById(R.id.gallery);
        gallery.setAdapter(new ImageAdapter(AppGlobals.getContext()));
        categorySpinner = (Spinner) mBaseView.findViewById(R.id.spinner);
        currenyGroup.setOnCheckedChangeListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list);
        categorySpinner.setAdapter(adapter);

        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                System.out.println("button submit");
                break;
            case R.id.btn_addimage:
                selectImage();
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_doller) {
            System.out.println("Doller");
        } else if (checkedId == R.id.radio_riyal) {
            System.out.println("Riyal");
        }
    }
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
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
        System.out.println(requestCode);
//        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                System.out.println("Select file camera");
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                imageUrl = destination.getAbsolutePath();
                System.out.println(destination);
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageForAd = Helpers.getBitMapOfProfilePic(destination.getAbsolutePath());
                System.out.println(destination);
                System.out.println(thumbnail);
            } else if (requestCode == SELECT_FILE) {
                System.out.println("Select file working");
                selectedImageUri = data.getData();
                System.out.println(selectedImageUri);
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(AppGlobals.getContext(), selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                imageForAd = Helpers.getBitMapOfProfilePic(selectedImagePath);
                imageUrl = String.valueOf(selectedImagePath);
                System.out.println(selectedImagePath);
                System.out.println(imageUrl);
            }
    }
}
