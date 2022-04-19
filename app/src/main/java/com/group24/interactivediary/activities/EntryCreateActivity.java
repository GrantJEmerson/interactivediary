package com.group24.interactivediary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.group24.interactivediary.fragments.listview.ListviewViewModel;
import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntryCreateActivity extends AppCompatActivity {
    public static final String TAG = "EntryCreateActivity";

    public static final int ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST = 368643; // just an arbitrary number

    // Views in the layout
    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private EditText titleEditText;
    private CardView mediaCardView;
    private ImageButton addMediaImageButton;
    private EditText textEditText;
    private RadioButton privateRadioButton;
    private RadioButton sharedRadioButton;
    private RadioButton publicRadioButton;
    private Button postButton;

    // Other necessary member variables
    Entry entry;
    LocationManager locationManager;
    Location location;
    ParseGeoPoint geoPointLocation;
    private ViewModelProvider viewModelProvider;
    private ListviewViewModel listviewViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_create);

        // Initialize the views in the layout
        relativeLayout = findViewById(R.id.entryCreateRelativeLayout);
        toolbar = findViewById(R.id.toolbar);
        titleEditText = findViewById(R.id.entryCreateTitleEditText);
        mediaCardView = findViewById(R.id.entryCreateMediaCardView);
        addMediaImageButton = findViewById(R.id.entryCreateAddMediaButton);
        textEditText = findViewById(R.id.entryCreateTextEditText);
        privateRadioButton = findViewById(R.id.createEntryPrivateRadioButton);
        sharedRadioButton = findViewById(R.id.createEntrySharedRadioButton);
        publicRadioButton = findViewById(R.id.createEntryPublicRadioButton);
        postButton = findViewById(R.id.createEntryPostButton);

        // Initialize other member variables
        viewModelProvider = new ViewModelProvider(this);
        listviewViewModel = viewModelProvider.get(ListviewViewModel.class);

        // Set up toolbar
        toolbar.setTitleTextColor(getResources().getColor(R.color.white, getTheme()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Unwrap the entry that was passed in by the intent
        entry = (Entry) Parcels.unwrap(getIntent().getParcelableExtra(Entry.class.getSimpleName()));

        if (entry != null) { // an entry was passed in via Parcel, so we are editing a preexisting entry
            // put in all the existing data
            titleEditText.setText(entry.getTitle());
            textEditText.setText(entry.getText());
            // TODO: handle media
            switch (entry.getVisibility()) {
                case PRIVATE:
                    privateRadioButton.setChecked(true);
                    sharedRadioButton.setChecked(false);
                    publicRadioButton.setChecked(false);
                    break;
                case SHARED:
                    privateRadioButton.setChecked(false);
                    sharedRadioButton.setChecked(true);
                    publicRadioButton.setChecked(false);
                    break;
                case PUBLIC:
                default:
                    privateRadioButton.setChecked(false);
                    sharedRadioButton.setChecked(false);
                    publicRadioButton.setChecked(true);
                    break;
            }
        }
        else {
            entry = new Entry();
        }

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.title_is_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: handle media
                List<ParseFile> mediaItems = new ArrayList<>();
                List<String> mediaItemDescriptions = new ArrayList<>();

                String text = textEditText.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_is_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                Entry.Visibility visibility;
                List<ParseUser> contributors = new ArrayList<>();
                contributors.add(ParseUser.getCurrentUser());
                if (privateRadioButton.isChecked()) {
                    visibility = Entry.Visibility.PRIVATE;
                }
                else if (sharedRadioButton.isChecked()) {
                    visibility = Entry.Visibility.SHARED;
                    // TODO: for each user the user adds as a contributor, add them to the list here
                }
                else if (publicRadioButton.isChecked()) {
                    visibility = Entry.Visibility.PUBLIC;
                }
                else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.entry_type_is_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                Date currentDate = new Date();

                // Put all the information together
                entry.setTitle(title);
                entry.setAuthor(ParseUser.getCurrentUser());
                entry.setContributors(contributors);
                entry.setText(text);
                entry.setMediaItems(mediaItems);
                entry.setMediaItemDescriptions(mediaItemDescriptions);
                entry.setVisibility(visibility);
                entry.setUpdatedAtDay(currentDate.getDay());
                entry.setUpdatedAtMonth(currentDate.getMonth());
                if (location != null) entry.setLocation(geoPointLocation);
                Log.e(TAG, "Saving new entry...");
                entry.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Failed to save new entry: " + e.getLocalizedMessage());
                        }
                        else {
                            listviewViewModel.setVisibility(visibility);
                            Log.e(TAG, listviewViewModel.getVisibility().getValue().toString());
                            Log.e(TAG, "Saved new entry!");
                            // TODO: make this actually work lol
                            finish(); // Exits activity
                        }
                    }
                });
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

    // Starts an intent to go to the login/signup activity
    private void goLoginSignupActivity() {
        Intent intent = new Intent(this, LoginSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // Starts an intent to go to the profile activity
    private void goProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Logs out user and sends them back to login/signup page
    private void logout() {
        ProgressDialog logoutProgressDialog = new ProgressDialog(EntryCreateActivity.this);
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

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST);
        }
        else {
            location = getCurrentUserLocation();
            geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
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
                geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            }
            // Permission has been denied
            else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);

                if (!showRationale) {
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
                return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        return null;
    }
}