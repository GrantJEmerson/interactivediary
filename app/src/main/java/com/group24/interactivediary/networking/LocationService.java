package com.group24.interactivediary.networking;

import static com.group24.interactivediary.fragments.mapview.MapviewFragment.ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.group24.interactivediary.R;
import com.parse.ParseGeoPoint;

public class LocationService implements LocationListener {

    Activity activity;
    LocationManager locationManager;
    ParseGeoPoint geoPointLocation;

    public LocationService(Activity activity) {
        this.activity = activity;
        getPermissionToAccessFineLocation();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
    }

    public ParseGeoPoint getCurrentLocation() {
        System.out.println("The current location is: " + geoPointLocation);
        return geoPointLocation;
    }

    public void setUpLocationManager() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager == null) {
                locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show our own UI to explain to the user why we need to access location
                // before actually requesting the permission and showing the default UI
//                weNeedLocationPermissions.setVisibility(View.VISIBLE);
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST);
        } else {
            setUpLocationManager();
        }
    }
}
