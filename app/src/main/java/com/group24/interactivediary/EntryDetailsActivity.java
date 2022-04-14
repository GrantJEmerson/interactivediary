package com.group24.interactivediary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.ArrayList;

public class EntryDetailsActivity extends AppCompatActivity {
    public static final String TAG = "EntryDetailsActivity";

    // Views in the layout
    private Toolbar toolbar;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView timestampTextView;
    private CardView mediaCardView;
    private TextView textTextView;

    // Other necessary member variables
    private Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_details);

        // Initialize the views in the layout
        toolbar = findViewById(R.id.toolbar);
        titleTextView = findViewById(R.id.entryDetailsTitleTextView);
        authorTextView = findViewById(R.id.entryDetailsAuthorTextView);
        timestampTextView = findViewById(R.id.entryDetailsTimestampTextView);
        mediaCardView = findViewById(R.id.entryDetailsMediaCardView);
        textTextView = findViewById(R.id.entryDetailsTextTextView);

        // Initialize other member variables

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Unwrap the entry that was passed in by the intent
        entry = (Entry) Parcels.unwrap(getIntent().getParcelableExtra(Entry.class.getSimpleName()));

        // Bind the entry to the layout
        ArrayList<ParseFile> mediaItems = entry.getMediaItems();
        if (!mediaItems.isEmpty()) {
            for (ParseFile media: mediaItems) {
                ImageView imageView = new ImageView(this);
                mediaCardView.addView(imageView);
                Glide.with(this)
                        .load(media)
                        .circleCrop()
                        .into(imageView);
            }
        }
        titleTextView.setText(entry.getTitle());
        authorTextView.setText(entry.getAuthor().getUsername());
        timestampTextView.setText(entry.getTimestamp());
        textTextView.setText(entry.getText());
    }
}