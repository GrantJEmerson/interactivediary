package com.group24.interactivediary.networking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import com.group24.interactivediary.model.Entry;
import com.group24.interactivediary.model.Search;
import com.parse.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EntryManager {

    String userID;
    Context context;
    LocationManager locationManager;

    public EntryManager(Context context) {
        this.context = context;
        userID = ParseUser.getCurrentUser().getObjectId();
    }

    public void fetchEntries(Entry.Visibility visibility, Entry.Ordering ordering, Search search, final FetchCallback<List<Entry>> callback) {
        ParseQuery<ParseObject> entryQuery;

        switch (visibility) {
            case PRIVATE:
                entryQuery = new ParseQuery<>("Entry");
                entryQuery.whereEqualTo("author", userID);
                entryQuery.whereEqualTo("visibility", "PRIVATE");
                break;
            case SHARED:
                ParseQuery<ParseObject> contributorQuery = new ParseQuery<>("Entry");
                contributorQuery.whereContains("contributors", userID);
                contributorQuery.whereEqualTo("visibility", "SHARED");

                ParseQuery<ParseObject> authorQuery = new ParseQuery<>("Entry");
                authorQuery.whereEqualTo("author", userID);
                authorQuery.whereEqualTo("visibility", "SHARED");

                List<ParseQuery<ParseObject>> queries = Arrays.asList(contributorQuery, authorQuery);
                entryQuery = ParseQuery.or(queries);
                break;
            case PUBLIC:
            default:
                entryQuery = new ParseQuery<>("Entry");
                entryQuery.whereEqualTo("visibility", "PUBLIC");
                break;
        }

        switch (ordering) {
            case TITLE_ASCENDING:
                entryQuery.addAscendingOrder("title");
                break;
            case TITLE_DESCENDING:
                entryQuery.addDescendingOrder("title");
                break;
            case DATE_ASCENDING:
                entryQuery.addAscendingOrder("updatedAt");
                break;
            case DATE_DESCENDING:
                entryQuery.addDescendingOrder("updatedAt");
                break;
            case NEAREST:
                Location userCurrentLocation = getCurrentUserLocation();
                if (userCurrentLocation != null) {
                    ParseGeoPoint geoPoint = new ParseGeoPoint();
                    geoPoint.setLatitude(userCurrentLocation.getLatitude());
                    geoPoint.setLongitude(userCurrentLocation.getLongitude());

                    entryQuery.whereNear("location", geoPoint);

                    break;
                }
            default:
                entryQuery.addDescendingOrder("updatedAt");
                break;
        }

        entryQuery.include("author");
        entryQuery.include("comments");
        entryQuery.include("contributors");
        entryQuery.include("mediaItems");

        if (search != null) {
            switch (search.type) {
                case TITLE:
                    entryQuery.whereContains("title", (String) search.searchParameter);
                    break;
                case DATE:
                    entryQuery.whereEqualTo("updatedAt", (Date) search.searchParameter);
                    break;
                case LOCATION:
                    Location searchLocation = (Location) search.searchParameter;

                    ParseGeoPoint geoPoint = new ParseGeoPoint();
                    geoPoint.setLongitude(searchLocation.getLongitude());
                    geoPoint.setLatitude(searchLocation.getLatitude());

                    entryQuery.whereWithinMiles("location", geoPoint, 20);
                    break;
            }
        }

        entryQuery.findInBackground((objects, error) -> {
            if (error == null) {
                List<Entry> entries = new ArrayList<>();

                for (ParseObject object : objects) {
                    Entry entry = new Entry(object);
                    entries.add(entry);
                }

                callback.done(entries);
            } else {
                System.out.println("Error fetching entries: " + error.getLocalizedMessage());
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
