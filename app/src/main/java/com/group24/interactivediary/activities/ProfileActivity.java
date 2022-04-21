package com.group24.interactivediary.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.group24.interactivediary.models.DiaryUser;
import com.group24.interactivediary.R;
import com.group24.interactivediary.networking.EntryManager;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "ProfileActivity";

    // Views in the layout
    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private TextView profileUsernameTextView;
    private SwitchMaterial notificationSwitch;
    private Button deleteAccountButton;

    // Other necessary member variables
    DiaryUser curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the views in the layout
        relativeLayout = findViewById(R.id.profileRelativeLayout);
        toolbar = findViewById(R.id.toolbar);
        profileUsernameTextView = findViewById(R.id.profileUsernameTextView);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        deleteAccountButton = findViewById(R.id.delete_account_button);

        // Initialize other member variables
        curUser = new DiaryUser(ParseUser.getCurrentUser());

        // Set up toolbar
        toolbar.setTitleTextColor(getResources().getColor(R.color.white, getTheme()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up username TextView
        profileUsernameTextView.setText(ParseUser.getCurrentUser().getUsername());

        // Set up delete account button
        deleteAccountButton.setOnClickListener(v -> {
            // Confirm delete account
            AlertDialog.Builder confirmAccountDeletionDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.confirm_account_deletion_title))
                    .setMessage(getResources().getString(R.string.confirm_account_deletion_message))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteAccount();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);

            confirmAccountDeletionDialog.show();
        });

        // Set up notification switch
        // Set initial configuration
        if (curUser.getGetsNotifications()) notificationSwitch.setChecked(true);
        else notificationSwitch.setChecked(false);
        // Set up toggle logic
        notificationSwitch.setOnClickListener(v -> {
            if (notificationSwitch.isChecked()) {
                curUser.setGetsNotifications(true);
                curUser.saveInBackground(e -> {
                    if (e != null) { // Save has failed
                        Snackbar.make(relativeLayout, getResources().getString(R.string.failed_to_save_user_settings), Snackbar.LENGTH_LONG).show();
                    }
                    else { // Save has succeeded
                        Snackbar.make(relativeLayout, getResources().getString(R.string.you_will_receive_notifications), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            else {
                curUser.setGetsNotifications(false);
                curUser.saveInBackground(e -> {
                    if (e != null) { // Save has failed
                        Snackbar.make(relativeLayout, getResources().getString(R.string.failed_to_save_user_settings), Snackbar.LENGTH_LONG).show();
                    }
                    else { // Save has succeeded
                        Snackbar.make(relativeLayout, getResources().getString(R.string.you_will_not_receive_notifications), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        // Add username next to profile icon
        menu.findItem(R.id.username).setTitle(ParseUser.getCurrentUser().getUsername());
        // Make the username text unclickable
        menu.findItem(R.id.username).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
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

    // Logs out user and sends them back to login/signup page
    private void logout() {
        ProgressDialog logoutProgressDialog = new ProgressDialog(ProfileActivity.this);
        logoutProgressDialog.setMessage(getResources().getString(R.string.logging_out));
        logoutProgressDialog.setCancelable(false);
        logoutProgressDialog.show();
        ParseUser.logOutInBackground(e -> {
            logoutProgressDialog.dismiss();
            if (e != null) { // Logout has failed
                Snackbar.make(relativeLayout, getResources().getString(R.string.logout_failed), Snackbar.LENGTH_LONG).show();
            }
            else { // Logout has succeeded
                goLoginSignupActivity();
                finish();
            }
        });
    }

    // Deletes user's account and associated entries.
    // Then sends them back to login/signup page.
    private void deleteAccount() {
        ProgressDialog deleteAccountProgressDialog = new ProgressDialog(ProfileActivity.this);
        deleteAccountProgressDialog.setMessage(getResources().getString(R.string.deleting_account));
        deleteAccountProgressDialog.setCancelable(false);
        deleteAccountProgressDialog.show();

        EntryManager entryManager = new EntryManager(this, null);
        entryManager.deleteUsersEntries(success -> {
            if (success) {
                ParseUser.getCurrentUser().deleteInBackground(exception -> {
                    deleteAccountProgressDialog.dismiss();
                    if (exception != null) { // Account deletion has failed
                        Snackbar.make(relativeLayout, getResources().getString(R.string.failed_to_delete_account), Snackbar.LENGTH_LONG).show();
                    } else { // Account has been deleted
                        logout();
                    }
                });
            } else {
                deleteAccountProgressDialog.dismiss();
                Snackbar.make(relativeLayout, getResources().getString(R.string.failed_to_delete_account), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}