package com.byteshaft.auction.fragments.seller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.byteshaft.auction.MainActivity;
import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class Seller extends Fragment implements View.OnClickListener{

    private EditText itemTitle;
    private EditText itemDescription;
    private Button submintButton;
    private Button addImageButton;
    private Spinner categorySpinner;
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
        submintButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
        categorySpinner = (Spinner) mBaseView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list);
        categorySpinner.setAdapter(adapter);

        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.isLastFragmentAvailable) {
            MainActivity.loginButton.setVisibility(View.INVISIBLE);
            MainActivity.registerButton.setVisibility(View.INVISIBLE);
            MainActivity.loginButton.setEnabled(false);
            MainActivity.registerButton.setEnabled(false);
        }
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                System.out.println("button submit");
                break;
            case R.id.btn_addimage:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = AppGlobals.getContext().getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                    System.out.println(yourSelectedImage);
                }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(AppGlobals.getContext().getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(AppGlobals.getContext().getContentResolver().openInputStream(selectedImage), null, o2);

    }
}
