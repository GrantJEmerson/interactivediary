package com.group24.interactivediary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

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

    // Add a list of items -- change to type used
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
            ArrayList<ParseFile> mediaItems = entry.getMediaItems();
            if (!mediaItems.isEmpty()) {
                ParseFile first = mediaItems.get(0);
                Glide.with(context)
                        .load(first)
                        .circleCrop()
                        .into(mediaImageView);
            }
            titleTextView.setText(entry.getTitle());
            authorTextView.setText(entry.getAuthor().getUsername());
            timestampTextView.setText(entry.getTimestamp());
            textTextView.setText(entry.getText());
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
