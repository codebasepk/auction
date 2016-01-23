package com.byteshaft.auction;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.byteshaft.auction.fragments.CategoriesFragment;
import com.byteshaft.auction.fragments.UserSettingFragment;
import com.byteshaft.auction.fragments.buyer.Buy;
import com.byteshaft.auction.fragments.AdsDetailFragment;
import com.byteshaft.auction.fragments.seller.Sell;
import com.byteshaft.auction.fragments.seller.UserSpeificBidsFragment;
import com.byteshaft.auction.gcm.QuickstartPreferences;
import com.byteshaft.auction.gcm.RegistrationIntentService;
import com.byteshaft.auction.register_login.LoginActivity;
import com.byteshaft.auction.utils.AppGlobals;
import com.byteshaft.auction.utils.BitmapWithCharacter;
import com.byteshaft.auction.utils.Helpers;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.FileNotFoundException;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity instance;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private View header;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Helpers.isUserLoggedIn()) {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                    if (sentToken) {
                        System.out.println(R.string.gcm_send_message);
                    } else {
                        System.out.println(R.string.token_error_message);
                    }
                }

            };

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }
        instance = this;
        if (Helpers.isUserLoggedIn()) {
            if (!Helpers.getBooleanValueFromSharedPreference(AppGlobals.KEY_CATEGORIES_SELECTED)) {
                loadFragment(new CategoriesFragment());
            }
            if (!Helpers.getLastFragment().equals("")) {
                if (Helpers.getLastFragment().contains("Buy")) {
                    loadFragment(new Buy());
                } else {
                    loadFragment(new Sell());
                }
            } else {
                loadFragment(new Buy());
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
        header = navigationView.getHeaderView(0);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        if (Helpers.isUserLoggedIn() && !Helpers.getBooleanValueFromSharedPreference(
                AppGlobals.KEY_CATEGORIES_SELECTED)) {
            loadFragment(new CategoriesFragment());
        } else if (Helpers.isUserLoggedIn() && Helpers.getLastFragment().equals("")) {
            loadFragment(new Buy());
        }

        CircularImageView circularImageView = (CircularImageView) header.findViewById(R.id.imageView);
        try {
            if (AppGlobals.getProfilePicBitMap() != null) {
                circularImageView.setImageBitmap(AppGlobals.getProfilePicBitMap());
            } else {
                if (Helpers.isUserLoggedIn()) {
                    final Resources res = getResources();
                    int[] array = getResources().getIntArray(R.array.letter_tile_colors);
                    final BitmapWithCharacter tileProvider = new BitmapWithCharacter();
                    final Bitmap letterTile = tileProvider.getLetterTile(Helpers.
                                    getStringDataFromSharedPreference(AppGlobals.KEY_USERNAME),
                            String.valueOf(array[new Random().nextInt(array.length)]), 100, 100);
                    circularImageView.setImageBitmap(letterTile);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (Helpers.isUserLoggedIn() && !Helpers.getBooleanValueFromSharedPreference(
                AppGlobals.PROFILE_PIC_STATUS) && !Helpers.getStringDataFromSharedPreference(
                AppGlobals.PROFILE_PIC_IMAGE_URL).equals("")) {
            System.out.println(Helpers.getStringDataFromSharedPreference(
                    AppGlobals.PROFILE_PIC_IMAGE_URL));
            new LoginActivity.DownloadProfilePic().execute(Helpers.
                    getStringDataFromSharedPreference(AppGlobals.PROFILE_PIC_IMAGE_URL));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    // Method to load the fragment required Fragment as parameter
    public void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }

    @Override
    public void onBackPressed() {
        if (Helpers.isUserLoggedIn()) {
            closeApplication();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // replace the fragment according to navigation item selected
    public void selectDrawerItem(MenuItem menuItem) {
        boolean logout = false;
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_buyer:
                fragmentClass = Buy.class;
                break;
            case R.id.nav_seller:
                fragmentClass = Sell.class;
                break;
            case R.id.nav_categories:
                fragmentClass = CategoriesFragment.class;
                break;
            case R.id.my_ads_detail:
                fragmentClass = AdsDetailFragment.class;
                break;
            case R.id.user_bids_detail:
                fragmentClass = UserSpeificBidsFragment.class;
                break;
            case R.id.nav_user:
                fragmentClass = UserSettingFragment.class;
                break;
            case R.id.logout:
                Helpers.removeUserData();
                logout = true;
                break;
            default:
                fragmentClass = Buy.class;
        }

        if (logout) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {

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