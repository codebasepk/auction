package com.byteshaft.auction;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.byteshaft.auction.fragments.buyer.Buyer;
import com.byteshaft.auction.fragments.seller.Seller;
import com.byteshaft.auction.login.SellerLogin;
import com.byteshaft.auction.utils.Helpers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private boolean isUserRoleAvailble = false;
    private Button buyerButton;
    private Button sellerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getApplicationContext(), SellerLogin.class));
        if (!Helpers.getUserRole().equals("")) {
            if (Helpers.getUserRole().equals("Buyer")) {
                loadFragment(new Buyer());
                isUserRoleAvailble = true;
            } else if (Helpers.getUserRole().equals("Seller")) {
                loadFragment(new Seller());
                isUserRoleAvailble = true;
            }
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        buyerButton = (Button) findViewById(R.id.buyer_button);
        sellerButton = (Button) findViewById(R.id.seller_button);
        if (isUserRoleAvailble) {
            buyerButton.setVisibility(View.INVISIBLE);
            sellerButton.setVisibility(View.INVISIBLE);
            buyerButton.setEnabled(false);
            sellerButton.setEnabled(false);
        }
        buyerButton.setOnClickListener(this);
        sellerButton.setOnClickListener(this);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.buyer:
                fragmentClass = Buyer.class;
                break;
            case R.id.seller:
                fragmentClass = Seller.class;
                break;
            default:
                fragmentClass = Seller.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        selectDrawerItem(item);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buyer_button:
                Helpers.saveUserRole(buyerButton.getText().toString());
                buyerButton.setEnabled(false);
                buyerButton.setVisibility(View.INVISIBLE);
                sellerButton.setEnabled(false);
                sellerButton.setVisibility(View.INVISIBLE);
                loadFragment(new Buyer());
                break;
            case R.id.seller_button:
                Helpers.saveUserRole(sellerButton.getText().toString());
                sellerButton.setEnabled(false);
                sellerButton.setVisibility(View.INVISIBLE);
                buyerButton.setEnabled(false);
                buyerButton.setVisibility(View.INVISIBLE);
                loadFragment(new Seller());
                break;
        }
    }
}