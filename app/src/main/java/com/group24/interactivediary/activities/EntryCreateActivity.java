package com.group24.interactivediary.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class EntryCreateActivity extends AppCompatActivity {
    public static final String TAG = "EntryCreateActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_create);

        // Initialize the views in the layout
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
        entry = new Entry();

        // Set up toolbar
        toolbar.setTitleTextColor(getResources().getColor(R.color.white, getTheme()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

                entry.setTitle(title);
                entry.setAuthor(ParseUser.getCurrentUser());
                entry.setText(text);
                entry.setMediaItems(mediaItems);
                entry.setMediaItemDescriptions(mediaItemDescriptions);
                entry.setVisibility(visibility);
                // entry.setLocation(location); TODO: figure out location
                entry.setContributors(contributors);
                Log.e(TAG, "Saving new entry...");
                entry.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Failed to save new entry: " + e.getLocalizedMessage());
                        }
                        else {
                            Log.e(TAG, "Saved new entry!");
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
}