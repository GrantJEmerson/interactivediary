package com.group24.interactivediary;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Entry")
public class Entry extends ParseObject implements Parcelable {
    public static final String TAG = "Entry";
    public static final String KEY_TITLE = "title";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TEXT = "text";
    public static final String KEY_MEDIA_ITEMS = "mediaItems";
    public static final String KEY_MEDIA_ITEM_DESCRIPTIONS = "mediaItemDescriptions";
    public static final String KEY_ENTRY_TYPE = "entryType";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_CONTRIBUTORS = "contributors";

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

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public ArrayList<ParseFile> getMediaItems() {
        JSONArray mediaItemsJSON = getJSONArray(KEY_MEDIA_ITEMS);
        ArrayList<ParseFile> mediaItems = new ArrayList<>();
        // TODO: figure out how to parse json array into arraylist
        return mediaItems;
    }

    public void setMediaItems(ArrayList<ParseFile> mediaItems) {
        JSONArray mediaItemsJSON = new JSONArray();

        // The "get" method returns an ArrayList, so manually add all the elements in the ArrayList to the JSON array
        for (ParseFile s: mediaItems) {
            mediaItemsJSON.put(s);
        }

        // Assign this JSON array as the user's list of media items
        put(KEY_MEDIA_ITEMS, mediaItemsJSON);
    }

    public ArrayList<String> getMediaItemDescriptions() {
        JSONArray mediaItemDescriptionsJSON = getJSONArray(KEY_MEDIA_ITEM_DESCRIPTIONS);
        ArrayList<String> mediaItemDescriptions = new ArrayList<>();
        // TODO: figure out how to parse json array into arraylist
        return mediaItemDescriptions;
    }

    public void setMediaItemDescriptions(ArrayList<String> mediaItemDescriptions) {
        JSONArray mediaItemDescriptionsJSON = new JSONArray();

        // The "get" method returns an ArrayList, so manually add all the elements in the ArrayList to the JSON array
        for (String s: mediaItemDescriptions) {
            mediaItemDescriptionsJSON.put(s);
        }

        // Assign this JSON array as the list of media item descriptions
        put(KEY_MEDIA_ITEMS, mediaItemDescriptionsJSON);
    }

    public void setEntryType(int entryType) {
        put(KEY_ENTRY_TYPE, entryType);
    }

    public int getEntryType() {
        return getInt(KEY_ENTRY_TYPE);
    }

    public void setLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public ArrayList<ParseUser> getContributors() {
        JSONArray contributorsJSON = getJSONArray(KEY_CONTRIBUTORS);
        ArrayList<ParseUser> contributors = new ArrayList<>();
        // TODO: figure out how to parse json array into arraylist
        return contributors;
    }

    public void setContributors(ArrayList<ParseUser> contributors) {
        JSONArray contributorsJSON = new JSONArray();

        // The "get" method returns an ArrayList, so manually add all the elements in the ArrayList to the JSON array
        for (ParseUser s: contributors) {
            contributorsJSON.put(s);
        }

        // Assign this JSON array as the list of contributors
        put(KEY_CONTRIBUTORS, contributorsJSON);
    }
}
