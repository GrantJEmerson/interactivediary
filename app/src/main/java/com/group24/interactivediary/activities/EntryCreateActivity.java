package com.group24.interactivediary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.group24.interactivediary.BitmapScaler;
import com.group24.interactivediary.BuildConfig;
import com.group24.interactivediary.models.DiaryUser;
import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EntryCreateActivity extends AppCompatActivity implements LocationListener {
    public static final String TAG = "EntryCreateActivity";

    public static final int CREATE_ACTIVITY = 5732787; // just an arbitrary number
    public static final int EDIT_ACTIVITY = 7643278; // just an arbitrary number
    public static final String ENTRY_RESULT_TAG = "entryFromEntryCreateActivity";

    public final static int CAMERA_CODE = 3434536;  // just an arbitrary number
    public final static int GALLERY_CODE = 9878754;  // just an arbitrary number
    public String mediaFileName = "interactiveDiary" + System.currentTimeMillis() + ".png";

    public final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 5354657;  // just an arbitrary number

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
    private ExpandableLayout contributorsExpandableLayout;
    private EditText contributorsEditText;
    private Switch locationSwitch;
    private Button postButton;

    // Other necessary member variables
    private Entry entry;
    private LocationManager locationManager;
    private Location location;
    private ParseGeoPoint geoPointLocation;
    private Context context;
    private File mediaFile;
    private List<ParseFile> mediaItems;
    private List<String> mediaItemDescriptions;

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
        contributorsExpandableLayout = findViewById(R.id.entryCreateAddContributorsExpandableLayout);
        contributorsEditText = findViewById(R.id.entryCreateAddContributorEditText);
        locationSwitch = findViewById(R.id.createEntryLocationSwitch);
        postButton = findViewById(R.id.createEntryPostButton);

        // Initialize other member variables
        context = this;
        mediaItems = new ArrayList<>();
        mediaItemDescriptions = new ArrayList<>();

        // Set up toolbar
        toolbar.setTitleTextColor(getResources().getColor(R.color.white, getTheme()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Unwrap the entry that was passed in by the intent
        entry = (Entry) Parcels.unwrap(getIntent().getParcelableExtra(Entry.class.getSimpleName()));

        if (entry != null) { // an entry was passed in via Parcel, so we are editing a preexisting entry
            getSupportActionBar().setTitle(R.string.edit_an_entry);
            // Bind all the entry's existing data to the layout
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
                // Add ImageView to CardView
                mediaCardView.addView(imageView);

                Glide.with(this)
                        .load(imagePair.first)
                        .into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Add media item description on click
                        TextView mediaItemDescription = new TextView(context);
                        mediaItemDescription.setText(imagePair.second);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.BELOW, imageId);
                        mediaItemDescription.setLayoutParams(layoutParams);
                    }
                });
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
                // Add VideoView to CardView
                mediaCardView.addView(videoView);
                videoView.setVideoURI(Uri.fromFile(videoPair.first)); // if doesn't work, try Uri.parse instead
                videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Add media item description on click
                        TextView mediaItemDescription = new TextView(context);
                        mediaItemDescription.setText(videoPair.second);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.BELOW, videoId);
                        mediaItemDescription.setLayoutParams(layoutParams);
                    }
                });
            }
            titleEditText.setText(entry.getTitle());
            textEditText.setText(entry.getText());

            switch (entry.getVisibility()) {
                case PRIVATE:
                    privateRadioButton.setChecked(true);
                    sharedRadioButton.setChecked(false);
                    publicRadioButton.setChecked(false);
                    contributorsExpandableLayout.collapse();
                    break;
                case SHARED:
                    privateRadioButton.setChecked(false);
                    sharedRadioButton.setChecked(true);
                    publicRadioButton.setChecked(false);
                    contributorsExpandableLayout.expand();
                    break;
                case PUBLIC:
                default:
                    privateRadioButton.setChecked(false);
                    sharedRadioButton.setChecked(false);
                    publicRadioButton.setChecked(true);
                    contributorsExpandableLayout.collapse();
                    break;
            }
            if (entry.getLocation() != null) locationSwitch.setChecked(true);
            else locationSwitch.setChecked(false);
        }
        else {
            entry = new Entry();
            entry.setAuthor(ParseUser.getCurrentUser());
        }

        // Set up listener to expand ExpandableLayout when entry is made Shared
        sharedRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contributorsExpandableLayout.expand();
            }
        });

        // Set up listeners to collapse ExpandableLayout when entry is made Private or Public
        privateRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contributorsExpandableLayout.collapse();
            }
        });
        publicRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contributorsExpandableLayout.collapse();
            }
        });

        // Set up location switch
        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationSwitch.isChecked()) getPermissionToAccessFineLocation();
                else location = null;
            }
        });

        // Set up add media button
        addMediaImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder cameraOrGalleryDialog = new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.capture_media_with_camera))
                        .setMessage(getResources().getString(R.string.upload_media_from_gallery))
                        .setIcon(R.drawable.ic_baseline_image_24)
                        .setPositiveButton(R.string.capture_media_with_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hideSoftKeyboard(relativeLayout);
                                launchCamera();
                            }
                        })
                        .setNegativeButton(R.string.upload_media_from_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hideSoftKeyboard(relativeLayout);
                                getPermissionToReadExternalStorage(context);
                                pickMedia();
                            }
                        });

                cameraOrGalleryDialog.show();
            }
        });

        // Set up post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.title_is_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                String text = textEditText.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_is_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                Entry.Visibility visibility;
                List<ParseUser> contributors = new ArrayList<>();
                if (privateRadioButton.isChecked()) {
                    visibility = Entry.Visibility.PRIVATE;
                }
                else if (sharedRadioButton.isChecked()) {
                    visibility = Entry.Visibility.SHARED;
                    String[] contributorsUsernames = contributorsEditText.getText().toString().split(", "); // Take in as comma-separated list of names
                    for (String username : contributorsUsernames) {
                        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                        userQuery.whereEqualTo(DiaryUser.KEY_USERNAME, username);
                        try {
                            contributors.add(userQuery.getFirst());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
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
                entry.setContributors(contributors);
                entry.setText(text);
                entry.setMediaItems(mediaItems);
                Log.e(TAG, Arrays.toString(mediaItems.toArray()));
                entry.setMediaItemDescriptions(mediaItemDescriptions);
                entry.setVisibility(visibility);
                entry.setUpdatedAtDay(currentDate.getDate());
                entry.setUpdatedAtMonth(currentDate.getMonth());
                if (geoPointLocation != null) entry.setLocation(geoPointLocation);
                Log.e(TAG, "Saving new entry...");
                entry.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Failed to save new entry: " + e.getLocalizedMessage());
                        }
                        else {
                            Log.e(TAG, "Saved new entry!");

                            // Tell the Home activity what tab to switch to when we return
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(ENTRY_RESULT_TAG, Parcels.wrap(entry));
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
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
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
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

    private void setUpLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager == null) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.getMainLooper());
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        }
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
//                weNeedLocationPermissions.setVisibility(View.VISIBLE);
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST);
        } else {
            setUpLocationManager();
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
                setUpLocationManager();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);

                if (showRationale) {
//                    weNeedLocationPermissions.setVisibility(View.VISIBLE);
                }
                else {
//                    weNeedLocationPermissions.setVisibility(View.GONE);
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
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        return null;
    }

    // Trigger camera starting to take photo
    private void launchCamera() {
        Log.i(TAG, "launchCamera() called");

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mediaFile = getPhotoFileUri(mediaFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                BuildConfig.APPLICATION_ID + ".provider", mediaFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAMERA_CODE);
        } else {
            Log.e(TAG, "No app can handle this intent");
            Toast.makeText(this, getResources().getString(R.string.no_camera), Toast.LENGTH_SHORT).show();
        }
    }

    // Trigger gallery selection for a photo
    public void pickMedia() {
        Log.i(TAG, "pickPhoto() called");

        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, GALLERY_CODE);
        } else {
            Log.e(TAG, "No app can handle this intent");
            Toast.makeText(this, getResources().getString(R.string.no_gallery), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CODE) { // If a photo was taken from the camera
            if (resultCode == Activity.RESULT_OK) { // Result succeeded
                Dialog mediaCheckDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
                mediaCheckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                mediaCheckDialog.setContentView(R.layout.media_check_dialog);
                mediaCheckDialog.setCancelable(true);
                mediaCheckDialog.show();

                ImageView imageView = mediaCheckDialog.findViewById(R.id.checkMediaDialogImageView);
                EditText editText = mediaCheckDialog.findViewById(R.id.checkMediaDialogEditText);
                Button sendButton = mediaCheckDialog.findViewById(R.id.checkMediaDialogSendButton);
                Button nevermindButton = mediaCheckDialog.findViewById(R.id.checkMediaDialogNevermindButton);

                Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(mediaFileName));
                //Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                Bitmap rawTakenImage = rotateBitmapOrientation(takenPhotoUri.getPath());
                imageView.setImageBitmap(rawTakenImage);

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mediaDescription = editText.getText().toString();
                        try { // Try to resize the image and save it to the disk
                            // Resize bitmap
                            Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 1000);
                            // Configure byte output stream
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            // Compress the image further
                            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                            // Create a new file for the resized bitmap
                            File resizedFile = getPhotoFileUri(mediaFileName);
                            resizedFile.createNewFile();
                            FileOutputStream fos = new FileOutputStream(resizedFile);
                            // Write the bytes of the bitmap to file
                            fos.write(bytes.toByteArray());
                            fos.close();
                            // Load the resized image into the ImageView
                            ParseFile parseFile = new ParseFile(resizedFile);
                            mediaItems.add(parseFile);
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to save resized bitmap to disk", e);
                            // Load the original taken image into the ImageView
                            ParseFile parseFile = new ParseFile(getPhotoFileUri(mediaFileName));
                            mediaItems.add(parseFile);
                        }
                        mediaItemDescriptions.add(mediaDescription);
                        mediaFileName = "interactiveDiary" + System.currentTimeMillis() + ".png";
                        mediaCheckDialog.dismiss();
                    }
                });

                nevermindButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaCheckDialog.dismiss();
                    }
                });
            } else { // Result failed
                Toast.makeText(this, getResources().getString(R.string.error_camera_media), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_CODE) { // If a photo was chosen from the gallery
            if (resultCode == Activity.RESULT_OK) { // Result succeeded
                Dialog mediaCheckDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
                mediaCheckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                mediaCheckDialog.setContentView(R.layout.media_check_dialog);
                mediaCheckDialog.setCancelable(true);
                mediaCheckDialog.show();

                ImageView imageView = mediaCheckDialog.findViewById(R.id.checkMediaDialogImageView);
                EditText editText = mediaCheckDialog.findViewById(R.id.checkMediaDialogEditText);
                Button sendButton = mediaCheckDialog.findViewById(R.id.checkMediaDialogSendButton);
                Button nevermindButton = mediaCheckDialog.findViewById(R.id.checkMediaDialogNevermindButton);

                Uri chosenPhotoUri = data.getData();
                Bitmap rawChosenImage = loadFromUri(chosenPhotoUri);
                imageView.setImageBitmap(rawChosenImage);

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mediaDescription = editText.getText().toString();
                        try { // Try to resize the image and save it to the disk
                            // Resize bitmap
                            Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawChosenImage, 1000);
                            // Configure byte output stream
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            // Compress the image further
                            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                            // Create a new file for the resized bitmap
                            File resizedFile = getPhotoFileUri(mediaFileName);
                            resizedFile.createNewFile();
                            FileOutputStream fos = new FileOutputStream(resizedFile);
                            // Write the bytes of the bitmap to file
                            fos.write(bytes.toByteArray());
                            fos.close();
                            // Load the resized image into the ImageView
                            ParseFile parseFile = new ParseFile(resizedFile);
                            mediaItems.add(parseFile);
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to save resized bitmap to disk", e);
                            // Load the original taken image into the ImageView
                            ParseFile parseFile = new ParseFile(getPhotoFileUri(mediaFileName));
                            mediaItems.add(parseFile);
                        }
                        mediaItemDescriptions.add(mediaDescription);
                        mediaFileName = "interactiveDiary" + System.currentTimeMillis() + ".png";
                        mediaCheckDialog.dismiss();
                    }
                });

                nevermindButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaCheckDialog.dismiss();
                    }
                });
            } else { // Result failed
                Toast.makeText(this, getResources().getString(R.string.error_gallery_media), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 270;
        //if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        //if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        //if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    // Minimizes the soft keyboard
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean getPermissionToReadExternalStorage(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                return false;
            }
            else return true;
        }
        else return true;
    }
}
