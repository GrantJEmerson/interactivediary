package com.group24.interactivediary.networking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.group24.interactivediary.adapters.EntryAdapter;
import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.models.Search;
import com.parse.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EntryManager {
    public static final String TAG = "EntryManager";

    String userID;
    Context context;
    LocationManager locationManager;
    EntryAdapter entryAdapter;
    TextView nothingHereYet;

    public EntryManager(Context context, EntryAdapter entryAdapter, TextView nothingHereYet) {
        this.context = context;
        userID = ParseUser.getCurrentUser().getObjectId();
        this.entryAdapter = entryAdapter;
        this.nothingHereYet = nothingHereYet;
    }

    public void fetchEntries(Entry.Visibility visibility, Entry.Ordering ordering, Search search, final FetchCallback<List<Entry>> callback) {
        ParseQuery<Entry> entryQuery;

        switch (visibility) {
            case PRIVATE:
                entryQuery = new ParseQuery<>(Entry.TAG);
                entryQuery.whereEqualTo(Entry.KEY_AUTHOR, userID);
                entryQuery.whereEqualTo(Entry.KEY_VISIBILITY, "PRIVATE");
                break;
            case SHARED:
                ParseQuery<Entry> contributorQuery = new ParseQuery<>(Entry.TAG);
                contributorQuery.whereContains(Entry.KEY_CONTRIBUTORS, userID);
                contributorQuery.whereEqualTo(Entry.KEY_VISIBILITY, "SHARED");

                ParseQuery<Entry> authorQuery = new ParseQuery<>(Entry.TAG);
                authorQuery.whereEqualTo(Entry.KEY_AUTHOR, userID);
                authorQuery.whereEqualTo(Entry.KEY_VISIBILITY, "SHARED");

                List<ParseQuery<Entry>> queries = Arrays.asList(contributorQuery, authorQuery);
                entryQuery = ParseQuery.or(queries);
                break;
            case PUBLIC:
            default:
                entryQuery = new ParseQuery<>(Entry.TAG);
                entryQuery.whereEqualTo(Entry.KEY_VISIBILITY, "PUBLIC");
                break;
        }

        switch (ordering) {
            case TITLE_ASCENDING:
                entryQuery.addAscendingOrder(Entry.KEY_TITLE);
                break;
            case TITLE_DESCENDING:
                entryQuery.addDescendingOrder(Entry.KEY_TITLE);
                break;
            case DATE_ASCENDING:
                entryQuery.addAscendingOrder(Entry.KEY_UPDATED_AT);
                break;
            case DATE_DESCENDING:
                entryQuery.addDescendingOrder(Entry.KEY_UPDATED_AT);
                break;
            case NEAREST:
                Location userCurrentLocation = getCurrentUserLocation();
                if (userCurrentLocation != null) {
                    ParseGeoPoint geoPoint = new ParseGeoPoint();
                    geoPoint.setLatitude(userCurrentLocation.getLatitude());
                    geoPoint.setLongitude(userCurrentLocation.getLongitude());

                    entryQuery.whereNear(Entry.KEY_LOCATION, geoPoint);

                    break;
                }
            default:
                entryQuery.addDescendingOrder(Entry.KEY_UPDATED_AT);
                break;
        }

        entryQuery.include(Entry.KEY_AUTHOR);
        entryQuery.include(Entry.KEY_CONTRIBUTORS);
        entryQuery.include(Entry.KEY_MEDIA_ITEMS);
        entryQuery.include(Entry.KEY_MEDIA_ITEM_DESCRIPTIONS);

        if (search != null) {
            switch (search.type) {
                case TITLE:
                    entryQuery.whereContains(Entry.KEY_TITLE, (String) search.searchParameter);
                    break;
                case DATE:
                    entryQuery.whereEqualTo(Entry.KEY_UPDATED_AT, (Date) search.searchParameter);
                    break;
                case LOCATION:
                    Location searchLocation = (Location) search.searchParameter;

                    ParseGeoPoint geoPoint = new ParseGeoPoint();
                    geoPoint.setLongitude(searchLocation.getLongitude());
                    geoPoint.setLatitude(searchLocation.getLatitude());

                    entryQuery.whereWithinMiles(Entry.KEY_LOCATION, geoPoint, 20);
                    break;
            }
        }

        entryQuery.findInBackground((entriesFound, e) -> {
            if (e == null) {
                // Clear out old items before appending in the new ones
                entryAdapter.clear();
                // Save received posts to list and notify adapter of new data
                entryAdapter.addAll(entriesFound);
                // Show empty message if gallery is empty
                if (entriesFound.size() == 0) nothingHereYet.setVisibility(View.VISIBLE);
                else nothingHereYet.setVisibility(View.GONE);

                callback.done(entriesFound);
            } else {
                Log.e(TAG, "Error fetching entries: " + e.getLocalizedMessage());
            }
        });
    }

    private Location getCurrentUserLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (locationManager == null) {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        return null;
    }
}
