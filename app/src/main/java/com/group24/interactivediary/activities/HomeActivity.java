package com.group24.interactivediary.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
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
import android.widget.Toast;

import org.parceler.Parcels;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";

    public static final int ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST = 368643; // just an arbitrary number

    private static final String[] tabTitles = {"Private", "Shared", "Public"};
    private static final int[] tabIcons = {R.drawable.ic_baseline_person_24, R.drawable.ic_baseline_people_24, R.drawable.ic_baseline_public_24};

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
    private LocationManager locationManager;
    private Location location;
    private NetworkRequest networkRequest;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean internetAccessLost = false;

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
        // Listen for visibility being changed
        listviewViewModel.getVisibility().observe(this, new Observer<Entry.Visibility>() {
            @Override
            public void onChanged(Entry.Visibility selectedVisibility) {
                Log.e(TAG, selectedVisibility+"");
                switch (selectedVisibility) {
                    case PRIVATE:
                        Log.e(TAG, "PRIVATE AAAAAAAAAAAAAA");
                        privateButton.setBackgroundColor(getResources().getColor(R.color.gold));
                        sharedButton.setBackgroundColor(getResources().getColor(R.color.mid_teal));
                        publicButton.setBackgroundColor(getResources().getColor(R.color.mid_teal));
                        break;
                    case SHARED:
                        Log.e(TAG, "SHARED AAAAAAAAAAAAAA");
                        privateButton.setBackgroundColor(getResources().getColor(R.color.mid_teal));
                        sharedButton.setBackgroundColor(getResources().getColor(R.color.gold));
                        publicButton.setBackgroundColor(getResources().getColor(R.color.mid_teal));
                        break;
                    case PUBLIC:
                        Log.e(TAG, "PUBLIC AAAAAAAAAAAAAA");
                        privateButton.setBackgroundColor(getResources().getColor(R.color.mid_teal));
                        sharedButton.setBackgroundColor(getResources().getColor(R.color.mid_teal));
                        publicButton.setBackgroundColor(getResources().getColor(R.color.gold));
                        break;
                    default:
                        Log.e(TAG, "DEFAULT AAAAAAAAAAAAAA");
                        privateButton.setBackgroundColor(getResources().getColor(R.color.gold));
                        sharedButton.setBackgroundColor(getResources().getColor(R.color.mid_teal));
                        publicButton.setBackgroundColor(getResources().getColor(R.color.mid_teal));
                        break;
                }
            }
        });

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

        networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        Context context = this;
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                if (internetAccessLost) {
                    Toast.makeText(context, getResources().getText(R.string.app_back_online), Toast.LENGTH_SHORT).show();
                    fab.setEnabled(true);
                    internetAccessLost = false;
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Toast.makeText(context, getResources().getText(R.string.internet_access_lost), Toast.LENGTH_SHORT).show();
                fab.setEnabled(false);
                internetAccessLost = true;
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
            }
        };

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        if (Settings.System.canWrite(this)) connectivityManager.requestNetwork(networkRequest, networkCallback);
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
        FragmentTransaction ft = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left);
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
        FragmentTransaction ft = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right);
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
        startActivityForResult(intent, EntryCreateActivity.CREATE_ACTIVITY);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode = " + requestCode);
        if (requestCode == EntryCreateActivity.CREATE_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                Entry returnedEntry = Parcels.unwrap(data.getParcelableExtra(EntryCreateActivity.ENTRY_RESULT_TAG));
                Entry.Visibility visibility = returnedEntry.getVisibility();
                listviewViewModel.setVisibility(visibility);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Do nothing
            }
        }
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

    // Called when the user is performing an action which requires the app to access the user's location
    public void getPermissionToAccessFineLocation() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show our own UI to explain to the user why we need to access location
                // before actually requesting the permission and showing the default UI

                ifNoLocationPermission();
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST);
        }
        else {
            location = getCurrentUserLocation();
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original ACCESS_FINE_LOCATION request
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST) {
            // Permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getText(R.string.location_permissions_granted), Toast.LENGTH_SHORT).show();

                // Get location
                location = getCurrentUserLocation();
            }
            // Permission has been denied
            else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);

                if (showRationale) {
                    ifNoLocationPermission();
                }
                else {
                    Toast.makeText(this, getResources().getText(R.string.location_permissions_denied), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private Location getCurrentUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (locationManager == null) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        return null;
    }

    private void ifNoLocationPermission() {
        // set menu to previous state (if ordering menu set to "nearest", set to whatever it was before
    }
}