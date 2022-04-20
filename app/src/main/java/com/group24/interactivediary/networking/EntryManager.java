package com.group24.interactivediary.networking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.models.GeneralDate;
import com.group24.interactivediary.models.Search;
import com.parse.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class EntryManager {
    public static final String TAG = "EntryManager";

    Context context;
    LocationManager locationManager;

    public EntryManager(Context context) {
        this.context = context;
    }

    public void deleteUsersEntries(final FetchCallback<Boolean> callback) {
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        ParseQuery<Entry> entryQuery;
        entryQuery = new ParseQuery<>(Entry.TAG);
        entryQuery.whereEqualTo(Entry.KEY_AUTHOR, ParseUser.getCurrentUser());

        entryQuery.findInBackground((entriesFound, e) -> {
            if (e == null) {
                final Iterator<Entry> entryIterator = entriesFound.iterator();

                DeleteCallback handler = new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            callback.done(false);
                        } else {
                            if (entryIterator.hasNext()) {
                                entryIterator.next().deleteInBackground(this);
                            } else {
                                callback.done(true);
                            }
                        }
                    }
                };

                if (entryIterator.hasNext()) {
                    entryIterator.next().deleteInBackground(handler);
                } else {
                    callback.done(true);
                }
            } else {
                Log.e(TAG, "Error fetching entries: " + e.getLocalizedMessage());
                callback.done(false);
            }
        });
    }

    public void fetchEntries(Entry.Visibility visibility, Entry.Ordering ordering, Search search, Date latestEntry, final FetchCallback<List<Entry>> callback) {
        ParseQuery<Entry> entryQuery;

        ParseUser user = ParseUser.getCurrentUser();

        switch (visibility) {
            case PRIVATE:
                Log.e(TAG, "querying for private entries");
                entryQuery = new ParseQuery<>(Entry.TAG);
                entryQuery.whereEqualTo(Entry.KEY_AUTHOR, user);
                entryQuery.whereEqualTo(Entry.KEY_VISIBILITY, Entry.Visibility.PRIVATE.toString());
                break;
            case SHARED:
                Log.e(TAG, "querying for shared entries");
                ParseQuery<Entry> contributorQuery = new ParseQuery<>(Entry.TAG);

                List<ParseUser> authorList = new ArrayList<ParseUser>();
                authorList.add(user);

                contributorQuery.whereContainsAll(Entry.KEY_CONTRIBUTORS, authorList);
                contributorQuery.whereEqualTo(Entry.KEY_VISIBILITY, Entry.Visibility.SHARED.toString());

                ParseQuery<Entry> authorQuery = new ParseQuery<>(Entry.TAG);
                authorQuery.whereEqualTo(Entry.KEY_AUTHOR, user);
                authorQuery.whereEqualTo(Entry.KEY_VISIBILITY, Entry.Visibility.SHARED.toString());

                List<ParseQuery<Entry>> queries = Arrays.asList(contributorQuery, authorQuery);
                entryQuery = ParseQuery.or(queries);
                break;
            case PUBLIC:
            default:
                Log.e(TAG, "querying for public entries");
                entryQuery = new ParseQuery<>(Entry.TAG);
                entryQuery.whereEqualTo(Entry.KEY_VISIBILITY, Entry.Visibility.PUBLIC.toString());
                break;
        }

        switch (ordering) {
            case TITLE_ASCENDING:
                Log.e(TAG, "query sorted by title ascending");
                entryQuery.addAscendingOrder(Entry.KEY_TITLE);
                break;
            case TITLE_DESCENDING:
                Log.e(TAG, "query sorted by title descending");
                entryQuery.addDescendingOrder(Entry.KEY_TITLE);
                break;
            case DATE_ASCENDING:
                Log.e(TAG, "query sorted by date ascending");
                entryQuery.addAscendingOrder(Entry.KEY_UPDATED_AT);
                break;
            case DATE_DESCENDING:
                Log.e(TAG, "query sorted by date descending");
                entryQuery.addDescendingOrder(Entry.KEY_UPDATED_AT);
                break;
            case NEAREST:
                Log.e(TAG, "query sorted by nearest");
                Location userCurrentLocation = getCurrentUserLocation();
                if (userCurrentLocation != null) {
                    ParseGeoPoint geoPoint = new ParseGeoPoint();
                    geoPoint.setLatitude(userCurrentLocation.getLatitude());
                    geoPoint.setLongitude(userCurrentLocation.getLongitude());

                    entryQuery.whereNear(Entry.KEY_LOCATION, geoPoint);

                    break;
                }
            default:
                Log.e(TAG, "query sorted by date descending");
                entryQuery.addDescendingOrder(Entry.KEY_UPDATED_AT);
                break;
        }

        // Limit query to latest 20 items
        entryQuery.setLimit(20);

        if (latestEntry != null) {
            if (ordering == Entry.Ordering.DATE_ASCENDING) {
                // Query only posts that are younger than the given date
                entryQuery.whereGreaterThan(Entry.KEY_UPDATED_AT, latestEntry);
            }
            else {
                // Query only posts that are older than the given date
                entryQuery.whereLessThan(Entry.KEY_UPDATED_AT, latestEntry);
            }
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
                    GeneralDate date = (GeneralDate) search.searchParameter;
                    entryQuery.whereEqualTo(Entry.KEY_UPDATED_AT_DAY, date.getDay());
                    entryQuery.whereEqualTo(Entry.KEY_UPDATED_AT_MONTH, date.getMonth());
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
