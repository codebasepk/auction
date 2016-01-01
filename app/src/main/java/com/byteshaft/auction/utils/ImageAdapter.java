package com.byteshaft.auction.utils;

import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.byteshaft.auction.R;

import java.util.ArrayList;

/**
 * class for the Gallery to show images captured or selected by user for posting a product
 */
public class ImageAdapter extends BaseAdapter{
    private int mGalleryItemBackground;

    private ArrayList<String> mImages;

    public ImageAdapter(ArrayList<String> images) {
        TypedArray attr = AppGlobals.getContext().obtainStyledAttributes(R.styleable.HelloGallery);
        mGalleryItemBackground = attr.getResourceId(
                R.styleable.HelloGallery_android_galleryItemBackground, 0);
        attr.recycle();
        mImages = images;
    }

    public int getCount() {
        return mImages.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(AppGlobals.getContext());
        imageView.setImageBitmap(Helpers.getBitMapOfProfilePic(mImages.get(position)));
        imageView.setLayoutParams(new Gallery.LayoutParams(250, 200));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundResource(mGalleryItemBackground);
        return imageView;
    }

}
