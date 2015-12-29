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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.auction.fragments.CategoriesFragment;
import com.byteshaft.auction.fragments.UserSettingFragment;
import com.byteshaft.auction.fragments.buyer.Buyer;
import com.byteshaft.auction.fragments.seller.Seller;
import com.byteshaft.auction.register_login.LoginActivity;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.Helpers;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if (Helpers.isUserLoggedIn()) {
            if (!Helpers.getLastFragment().equals("")) {
                if (Helpers.getLastFragment().contains("Buyer")) {
                    loadFragment(new Buyer());
                } else {
                    loadFragment(new Seller());
                }
            }
//            else if (!Helpers.getBooleanValueFromSharedPreference(AppGlobals.KEY_CATEGORIES_SELECTED)) {
//                loadFragment(new CategoriesFragment());
//            }
            else {
                loadFragment(new Buyer());
            }
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
        View header = navigationView.getHeaderView(0);
        TextView userName = (TextView) header.findViewById(R.id.nav_user_name);
        TextView userEmail = (TextView) header.findViewById(R.id.nav_user_email);
        if (!Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME).equals("")) {
            userName.setText(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME));
        } else {
            userName.setText("username");
        }
        if (!Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_EMAIL).equals("")) {
            userEmail.setText(Helpers.getStringDataFromSharedPreference(AppGlobals.KEY_EMAIL));
        } else {
            userEmail.setText("abc@xyz.com");
        }
        ImageView circularImageView = (ImageView) header.findViewById(R.id.imageView);
        try {
            if (AppGlobals.getProfilePicBitMap() != null) {
                circularImageView.setImageBitmap(AppGlobals.getProfilePicBitMap());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppGlobals.loginSuccessFull) {
            loadFragment(new Buyer());
            AppGlobals.loginSuccessFull = false;
        }
    }

    // Method to load the fragment required Fragment as parameter
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

    // replace the fragment according to navigation item selected
    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_buyer:
                fragmentClass = Buyer.class;
                break;
            case R.id.nav_seller:
                fragmentClass = Seller.class;
                break;
            case R.id.nav_categories:
                fragmentClass = CategoriesFragment.class;
                break;
            case R.id.nav_user:
                fragmentClass = UserSettingFragment.class;
                break;
            default:
                fragmentClass = Buyer.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        menuItem.setCheckable(true);
        setTitle(menuItem.getTitle());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    // Method to close the application when needed
    public void closeApplication() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        startActivity(startMain);
        MainActivity.this.finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();
        selectDrawerItem(item);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}