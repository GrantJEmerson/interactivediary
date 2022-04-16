package com.group24.interactivediary.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.group24.interactivediary.R;
import com.group24.interactivediary.fragments.listview.ListviewFragment;
import com.group24.interactivediary.fragments.listview.ListviewViewModel;
import com.group24.interactivediary.fragments.mapview.MapviewFragment;
import com.group24.interactivediary.fragments.mapview.MapviewViewModel;
import com.group24.interactivediary.models.Entry;
import com.parse.ParseUser;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";

    private String[] tabTitles = {"Private", "Shared", "Public"};
    private int[] tabIcons = {R.drawable.ic_baseline_person_24, R.drawable.ic_baseline_people_24, R.drawable.ic_baseline_public_24};

    // Views in the layout
    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private Button privateButton;
    private Button sharedButton;
    private Button publicButton;
    private BottomNavigationView navView;
    private FloatingActionButton fab;

    // Other necessary member variables
    private ListviewFragment listviewFragment;
    private MapviewFragment mapviewFragment;
    private FragmentManager fragmentManager;
    private ViewModelProvider viewModelProvider;
    private ListviewViewModel listviewViewModel;
    private MapviewViewModel mapviewViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the views in the layout
        relativeLayout = findViewById(R.id.homeRelativeLayout);
        toolbar = findViewById(R.id.toolbar);
        privateButton = findViewById(R.id.homePrivateButton);
        sharedButton = findViewById(R.id.homeSharedButton);
        publicButton = findViewById(R.id.homePublicButton);
        navView = findViewById(R.id.homeBottomNavView);
        fab = findViewById(R.id.homeCreateEntryButton);

        // Initialize other member variables
        fragmentManager = getSupportFragmentManager();
        listviewFragment = new ListviewFragment();
        mapviewFragment = new MapviewFragment();
        viewModelProvider = new ViewModelProvider(this);
        listviewViewModel = viewModelProvider.get(ListviewViewModel.class);
        mapviewViewModel = viewModelProvider.get(MapviewViewModel.class);

        // Set up toolbar
        toolbar.setTitleTextColor(getResources().getColor(R.color.white, getTheme()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Display listviewFragment by default
        displayListviewFragment();

        // Display private visibility by default
        listviewViewModel.setVisibility(Entry.Visibility.PRIVATE);
        listviewViewModel.setNothingHereYetText(getResources().getString(R.string.private_nothing_here_yet));
        mapviewViewModel.setVisibility(Entry.Visibility.PRIVATE);

        // Set up the bottom nav bar
        // Set viewPager adapter to listviewAdapter by default
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_listview) displayListviewFragment();
            if (item.getItemId() == R.id.navigation_mapview) displayMapviewFragment();
            return true;
        });

        // Set up the top nav bar
        privateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "changed visibility to private");
                listviewViewModel.setVisibility(Entry.Visibility.PRIVATE);
                listviewViewModel.setNothingHereYetText(getResources().getString(R.string.private_nothing_here_yet));
                mapviewViewModel.setVisibility(Entry.Visibility.PRIVATE);
            }
        });

        sharedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "changed visibility to shared");
                listviewViewModel.setVisibility(Entry.Visibility.SHARED);
                listviewViewModel.setNothingHereYetText(getResources().getString(R.string.shared_nothing_here_yet));
                mapviewViewModel.setVisibility(Entry.Visibility.SHARED);
            }
        });

        publicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "changed visibility to public");
                listviewViewModel.setVisibility(Entry.Visibility.PUBLIC);
                listviewViewModel.setNothingHereYetText(getResources().getString(R.string.public_nothing_here_yet));
                mapviewViewModel.setVisibility(Entry.Visibility.PUBLIC);
            }
        });

        // Set up the entry add button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goEntryCreateActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Add username next to profile icon
        menu.findItem(R.id.username).setTitle(ParseUser.getCurrentUser().getUsername());
        // Make the username text unclickable
        menu.findItem(R.id.username).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.profileMenuItem:
                goProfileActivity();
                return true;
            case R.id.logoutMenuItem:
                logout();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void displayListviewFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (listviewFragment.isAdded()) { // if the fragment is already in container
            ft.show(listviewFragment);
        }
        else { // fragment needs to be added to frame container
            ft.add(R.id.homeFragmentContainer, listviewFragment, "listViewFragment");
        }
        // Hide mapviewFragment
        if (mapviewFragment.isAdded()) {
            ft.hide(mapviewFragment);
        }
        // Commit changes
        ft.commit();
    }

    private void displayMapviewFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (mapviewFragment.isAdded()) { // if the fragment is already in container
            ft.show(mapviewFragment);
        }
        else { // fragment needs to be added to frame container
            ft.add(R.id.homeFragmentContainer, mapviewFragment, "mapViewFragment");
        }
        // Hide listviewFragment
        if (listviewFragment.isAdded()) {
            ft.hide(listviewFragment);
        }
        // Commit changes
        ft.commit();
    }

    // Starts an intent to go to the EntryCreate activity
    private void goEntryCreateActivity() {
        Intent intent = new Intent(this, EntryCreateActivity.class);
        startActivity(intent);
    }

    // Starts an intent to go to the profile activity
    private void goProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Starts an intent to go to the login/signup activity
    private void goLoginSignupActivity() {
        Intent intent = new Intent(this, LoginSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // Logs out user and sends them back to login/signup page
    private void logout() {
        ProgressDialog logoutProgressDialog = new ProgressDialog(HomeActivity.this);
        logoutProgressDialog.setMessage(getResources().getString(R.string.logging_out));
        logoutProgressDialog.setCancelable(false);
        logoutProgressDialog.show();
        ParseUser.logOutInBackground(e -> {
            logoutProgressDialog.dismiss();
            if (e != null) {  // Logout has failed
                Snackbar.make(relativeLayout, getResources().getString(R.string.logout_failed), Snackbar.LENGTH_LONG).show();
            }
            else { // Logout has succeeded
                goLoginSignupActivity();
                finish();
            }
        });
    }
}