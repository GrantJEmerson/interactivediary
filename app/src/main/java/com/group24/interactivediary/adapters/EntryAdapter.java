package com.group24.interactivediary.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group24.interactivediary.R;
import com.group24.interactivediary.activities.EntryDetailsActivity;
import com.group24.interactivediary.models.Entry;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {
    Context context;
    List<Entry> entries;

    // Constructor
    public EntryAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        entries.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Entry> list) {
        entries.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mediaImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView timestampTextView;
        TextView textTextView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mediaImageView = itemView.findViewById(R.id.entryImageImageView);
            titleTextView = itemView.findViewById(R.id.entryTitle);
            authorTextView = itemView.findViewById(R.id.entryAuthorTextView);
            timestampTextView = itemView.findViewById(R.id.entryTimestampTextView);
           textTextView = itemView.findViewById(R.id.entryTextTextView);

            itemView.setOnClickListener(this);
        }

        public void bind(Entry entry) {
            List<List> mediaItemsLists = entry.getMediaItems();
            if (!mediaItemsLists.get(0).isEmpty()) {
                Bitmap first = ((Pair<Bitmap, String>) mediaItemsLists.get(0)).first;
                Glide.with(context)
                        .load(first)
                        .circleCrop()
                        .into(mediaImageView);
            }
            titleTextView.setText(entry.getTitle());
            timestampTextView.setText(entry.getTimestamp());
            textTextView.setText(entry.getText());

            String contributorsString = "";
            List<ParseUser> contributors = entry.getContributors();
            for (int i = 0; i < contributors.size(); i++) {
                try {
                    if (i != contributors.size() - 1) {
                        contributorsString += contributors.get(i).fetchIfNeeded().getUsername() + ", ";
                    }
                    else {
                        contributorsString += contributors.get(i).fetchIfNeeded().getUsername();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            authorTextView.setText(contributorsString);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) { // Check if position is valid
                Entry entry = entries.get(position);

                Intent intent = new Intent(context, EntryDetailsActivity.class);

                // Wrap the entry in a parcel and attach it to the intent so it can be sent along with it
                intent.putExtra(Entry.class.getSimpleName(), Parcels.wrap(entry));

                context.startActivity(intent);
            }
        }
    }
}
