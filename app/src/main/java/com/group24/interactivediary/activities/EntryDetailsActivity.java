package com.group24.interactivediary.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.R;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.util.List;

public class EntryDetailsActivity extends AppCompatActivity {
    public static final String TAG = "EntryDetailsActivity";

    // Views in the layout
    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView timestampTextView;
    private LinearLayout mediaLinearLayout;
    private TextView textTextView;
    private Button settingsButton;

    // Other necessary member variables
    private Entry entry;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_details);

        // Initialize the views in the layout
        relativeLayout = findViewById(R.id.entryDetailsRelativeLayout);
        toolbar = findViewById(R.id.toolbar);
        titleTextView = findViewById(R.id.entryDetailsTitleTextView);
        authorTextView = findViewById(R.id.entryDetailsAuthorTextView);
        timestampTextView = findViewById(R.id.entryDetailsTimestampTextView);
        mediaLinearLayout = findViewById(R.id.entryDetailsMediaLinearLayout);
        textTextView = findViewById(R.id.entryDetailsTextTextView);
        settingsButton = findViewById(R.id.entryDetailsSettingsButton);

        // Initialize other member variables
        context = this;

        // Set up toolbar
        toolbar.setTitleTextColor(getResources().getColor(R.color.white, getTheme()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Unwrap the entry that was passed in by the intent
        entry = (Entry) Parcels.unwrap(getIntent().getParcelableExtra(Entry.class.getSimpleName()));

        // Show settings button if private or shared, hide if public (unless current user is the author)
        if (entry.getVisibility() == Entry.Visibility.PRIVATE || entry.getVisibility() == Entry.Visibility.SHARED) {
            settingsButton.setVisibility(View.VISIBLE);
        }
        else {
            if (entry.getAuthor().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) settingsButton.setVisibility(View.VISIBLE);
            else settingsButton.setVisibility(View.GONE);
        }

        bindEntryToLayout();

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForContextMenu(settingsButton);
                openContextMenu(view);
                unregisterForContextMenu(settingsButton);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.entry_settings_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.editMenuItem:
                goEntryCreateActivity();
                return true;
            case R.id.deleteMenuItem:
                AlertDialog.Builder confirmEntryDeletionDialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.confirm_entry_deletion_title))
                        .setMessage(getResources().getString(R.string.confirm_entry_deletion_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteEntry();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);

                confirmEntryDeletionDialog.show();
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

    // Starts an intent to go to the EntryCreate activity
    private void goEntryCreateActivity() {
        Intent intent = new Intent(this, EntryCreateActivity.class);
        intent.putExtra(Entry.class.getSimpleName(), Parcels.wrap(entry));
        startActivityForResult(intent, EntryCreateActivity.EDIT_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode = " + requestCode);
        if (requestCode == EntryCreateActivity.EDIT_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                Entry returnedEntry = Parcels.unwrap(data.getParcelableExtra(EntryCreateActivity.ENTRY_RESULT_TAG));
                entry = returnedEntry;
                bindEntryToLayout();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Do nothing
            }
        }
    }

    // Logs out user and sends them back to login/signup page
    private void logout() {
        ProgressDialog logoutProgressDialog = new ProgressDialog(EntryDetailsActivity.this);
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

    private void bindEntryToLayout() {
        // Bind the entry to the layout
        List<List> mediaItemsLists = entry.getMediaItems();
        // Images
        for (Object imagePairObject : mediaItemsLists.get(0)) {
            Pair<Bitmap, String> imagePair = (Pair<Bitmap, String>) imagePairObject;

            // Make new ImageView and set layout params
            ImageView imageView = new ImageView(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            imageView.setLayoutParams(layoutParams);
            imageView.getLayoutParams().height = 1000;
            imageView.getLayoutParams().width = 1000;
            imageView.requestLayout();
            int imageId = ViewCompat.generateViewId();
            imageView.setId(imageId);

            // Add ImageView to LinearLayout
            mediaLinearLayout.addView(imageView);
            Log.e(TAG, "added " + imageView.getId());
            Glide.with(this)
                    .load(imagePair.first)
                    .into(imageView);
        }
        // Videos
        for (Object videoPairObject : mediaItemsLists.get(1)) {
            Pair<File, String> videoPair = (Pair<File, String>) videoPairObject;

            // Make new VideoView and set layout params
            VideoView videoView = new VideoView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            videoView.setLayoutParams(layoutParams);
            int videoId = ViewCompat.generateViewId();
            videoView.setId(videoId);
            // Add VideoView to LinearLayout
            mediaLinearLayout.addView(videoView);
            videoView.setVideoURI(Uri.fromFile(videoPair.first)); // if doesn't work, try Uri.parse instead
        }
        titleTextView.setText(entry.getTitle());
        timestampTextView.setText(entry.getTimestamp());
        textTextView.setText(entry.getText());

        String contributorsString = "";
        List<ParseUser> contributors = entry.getContributors();
        if (contributors.size() == 0) {
            authorTextView.setText(entry.getAuthor().getUsername());
        }
        else {
            for (int i = 0; i < contributors.size(); i++) {
                try {
                    if (i != contributors.size() - 1) {
                        contributorsString += contributors.get(i).fetchIfNeeded().getUsername() + ", ";
                    } else {
                        contributorsString += contributors.get(i).fetchIfNeeded().getUsername();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            authorTextView.setText(entry.getAuthor().getUsername() + ", " + contributorsString);
        }
    }

    private void deleteEntry() {
        entry.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error fetching entries: " + e.getLocalizedMessage());
                }
            }
        });
    }
}