package com.group24.interactivediary.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Entry")
public class Entry extends ParseObject implements Parcelable {
    public static final String TAG = "Entry";

    // Keys for Back4App
    public static final String KEY_TITLE = "title";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTRIBUTORS = "contributors";
    public static final String KEY_TEXT = "text";
    public static final String KEY_MEDIA_ITEMS = "mediaItems";
    public static final String KEY_MEDIA_ITEM_DESCRIPTIONS = "mediaItemDescriptions";
    public static final String KEY_VISIBILITY = "visibility";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";
    public static final String KEY_UPDATED_AT_DAY = "updatedAtDay";
    public static final String KEY_UPDATED_AT_MONTH = "updatedAtMonth";
    public static final String KEY_LOCATION = "location";

    // Enums
    public enum Visibility {
        PRIVATE,
        SHARED,
        PUBLIC
    }

    public enum Ordering {
        TITLE_ASCENDING,
        TITLE_DESCENDING,
        DATE_ASCENDING,
        DATE_DESCENDING,
        NEAREST
    }

    public String getTimestamp() {
        Date createdAt = this.getUpdatedAt();
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + "d";
            }
        } catch (Exception e) {
            Log.i(TAG, "getTimestamp failed", e);
            e.printStackTrace();
        }

        return "";
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public List<ParseUser> getContributors() {
        return getList(KEY_CONTRIBUTORS);
    }

    public void setContributors(List<ParseUser> contributors) {
        put(KEY_CONTRIBUTORS, contributors);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public List<ParseFile> getMediaItemsAsParseFiles() {
        return getList(KEY_MEDIA_ITEMS);
    }

    public List<List> getMediaItems() {
        List<ParseFile> mediaItemPFiles = getList(KEY_MEDIA_ITEMS);
        List<Pair<Bitmap, String>> images = new ArrayList<>();
        List<Pair<File, String>> videos = new ArrayList<>();

        for (int i = 0; i < mediaItemPFiles.size(); i++) {
            try {
                File mediaItemFile = mediaItemPFiles.get(i).getFile();
                String mediaItemFilePath = mediaItemFile.getPath();
                /*String mimeType = "";

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    try {
                        mimeType = Files.probeContentType(mediaItemFile.toPath());
                        if (mimeType.startsWith("image")) {*/
                            Bitmap bitmap = BitmapFactory.decodeFile(mediaItemFilePath);
                            images.add(new Pair<Bitmap, String>(bitmap, getMediaItemDescriptions().get(i)));
                        /*}

                        if (mimeType.startsWith("video")) {
                            videos.add(new Pair<File, String>(mediaItemFile, getMediaItemDescriptions().get(i)));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
            } catch (ParseException e) {
                Log.e(TAG, "Could not download media item: " + e.getLocalizedMessage());
            }
        }
        List<List> res = new ArrayList<>();
        res.add(images);
        res.add(videos);
        return res;
    }

    public void setMediaItems(List<ParseFile> mediaItems) {
        put(KEY_MEDIA_ITEMS, mediaItems);
    }

    public List<String> getMediaItemDescriptions() {
        return getList(KEY_MEDIA_ITEM_DESCRIPTIONS);
    }

    public void setMediaItemDescriptions(List<String> mediaItemDescriptions) {
        put(KEY_MEDIA_ITEM_DESCRIPTIONS, mediaItemDescriptions);
    }

    public void addMediaItemDescription(String mediaItemDescription) {
        List<String> mediaItemDescriptions = getMediaItemDescriptions();
        mediaItemDescriptions.add(mediaItemDescription);
        put(KEY_MEDIA_ITEM_DESCRIPTIONS, mediaItemDescriptions);
    }

    public void setVisibility(Visibility visibility) {
        put(KEY_VISIBILITY, visibility.toString());
    }

    public Visibility getVisibility() {
        return Visibility.valueOf(getString(KEY_VISIBILITY));
    }

    public void setUpdatedAtDay(int day) {
        put(KEY_UPDATED_AT_DAY, day);
    }

    public void setUpdatedAtMonth(int month) {
        put(KEY_UPDATED_AT_MONTH, month);
    }

    public void setLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }
}
