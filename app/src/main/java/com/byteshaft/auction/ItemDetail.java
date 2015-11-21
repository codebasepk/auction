package com.byteshaft.auction;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.byteshaft.auction.utils.AppGlobals;

public class ItemDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detials);
        String detail = getIntent().getStringExtra(AppGlobals.detial);
        setTitle(detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(getApplicationContext(), detail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
