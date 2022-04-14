package com.group24.interactivediary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;

public class EntryCreateActivity extends AppCompatActivity {
    public static final String TAG = "EntryCreateActivity";

    // Views in the layout
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
        setSupportActionBar(toolbar);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.title_is_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: handle media
                ArrayList<ParseFile> mediaItems = new ArrayList<>();
                ArrayList<String> mediaItemDescriptions = new ArrayList<>();

                String text = textEditText.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_is_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                int entryType;
                ArrayList<ParseUser> contributors = new ArrayList<>();
                contributors.add(ParseUser.getCurrentUser());
                if (privateRadioButton.isActivated()) {
                    entryType = HomeActivity.PRIVATE;
                }
                else if (sharedRadioButton.isActivated()) {
                    entryType = HomeActivity.SHARED;
                    // TODO: for each user the user adds as a contributor, add them to the arraylist here
                }
                else if (publicRadioButton.isActivated()) {
                    entryType = HomeActivity.PUBLIC;
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
                entry.setEntryType(entryType);
                // entry.setLocation(location); TODO: figure out location
                entry.setContributors(contributors);

                entry.saveInBackground();
            }
        });
    }
}