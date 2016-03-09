package com.byteshaft.auction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Class to show single image
 */
public class ProductImageView extends AppCompatActivity {


    private ImageView imageView;
    private PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_imageview);
        imageView = (ImageView) findViewById(R.id.product_image_view);
        photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.update();
        Intent intent = getIntent();
        String imagePosition = intent.getStringExtra("url");
        Picasso.with(ProductImageView.this)
                .load(imagePosition)
                .resize(720, 1280)
                .into(imageView);
    }
}
