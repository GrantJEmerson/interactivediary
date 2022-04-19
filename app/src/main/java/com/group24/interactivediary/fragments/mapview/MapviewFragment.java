package com.group24.interactivediary.fragments.mapview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group24.interactivediary.R;

import org.jetbrains.annotations.NotNull;

public class MapviewFragment extends Fragment {
    public static final String TAG = "MapviewFragment";

    public static final int ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST = 368643; // just an arbitrary number

    // Views in the layout
    TextView weNeedLocationPermissions;

    // Other necessary member variables
    private ViewModelProvider viewModelProvider;
    private MapviewViewModel mapviewViewModel;
    private LocationManager locationManager;
    private Location location;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the views in the layout
        weNeedLocationPermissions = view.findViewById(R.id.weNeedLocationPermissions);

        // Initialize other member variables
        viewModelProvider = new ViewModelProvider(requireActivity());
        mapviewViewModel = viewModelProvider.get(MapviewViewModel.class);

        // Get permission to access location
        getPermissionToAccessFineLocation();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Called when the user is performing an action which requires the app to access the user's location
    public void getPermissionToAccessFineLocation() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show our own UI to explain to the user why we need to access location
                // before actually requesting the permission and showing the default UI
                weNeedLocationPermissions.setVisibility(View.VISIBLE);
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST);
        }
        else {
            location = getCurrentUserLocation();
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original ACCESS_FINE_LOCATION request
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST) {
            // Permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireActivity(), getResources().getText(R.string.location_permissions_granted), Toast.LENGTH_SHORT).show();

                // Get location
                location = getCurrentUserLocation();
            }
            // Permission has been denied
            else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);

                if (showRationale) {
                    weNeedLocationPermissions.setVisibility(View.VISIBLE);
                }
                else {
                    weNeedLocationPermissions.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), getResources().getText(R.string.location_permissions_denied), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private Location getCurrentUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (locationManager == null) {
                locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        return null;
    }
}