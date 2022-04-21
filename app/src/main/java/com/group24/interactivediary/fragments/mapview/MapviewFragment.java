package com.group24.interactivediary.fragments.mapview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.group24.interactivediary.R;
import com.group24.interactivediary.activities.EntryDetailsActivity;
import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.models.Search;
import com.group24.interactivediary.networking.EntryManager;
import com.group24.interactivediary.networking.FetchCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParsePolygon;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapviewFragment extends Fragment implements LocationListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {
    public static final String TAG = "MapviewFragment";

    public static final int ACCESS_FINE_LOCATION_PERMISSIONS_REQUEST = 368643; // just an arbitrary number

    // Views in the layout
    TextView weNeedLocationPermissions;

    // Other necessary member variables
    private ViewModelProvider viewModelProvider;
    private MapviewViewModel mapviewViewModel;
    private LocationManager locationManager;
    private Location location;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private EntryManager entryManager;
    private List<Entry> entries;
    private Map<String, Marker> markers;
    private Entry.Visibility visibility;

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

        // Listen for visibility being changed
        mapviewViewModel.getVisibility().observe(getViewLifecycleOwner(), new Observer<Entry.Visibility>() {
            @Override
            public void onChanged(Entry.Visibility selectedVisibility) {
                Log.e(TAG, "onChanged called");
                visibility = selectedVisibility;
                fetchEntries();
            }
        });

        markers = new HashMap<>();
        entries = new ArrayList<>();

        entryManager = new EntryManager(requireActivity());

        setUpMapIfNeeded();
        getPermissionToAccessFineLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayLocation();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
        displayLocation();
    }

    @Override
    public void onCameraIdle() {
        fetchEntries();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Entry entry : entries) {
            if (markers.containsKey(entry.getObjectId()) && markers.get(entry.getObjectId()).equals(marker)) {
                Context context = requireContext();

                Intent intent = new Intent(context, EntryDetailsActivity.class);

                // Wrap the entry in a parcel and attach it to the intent so it can be sent along with it
                intent.putExtra(Entry.class.getSimpleName(), Parcels.wrap(entry));

                context.startActivity(intent);
            }
        }
        return false;
    }

    protected void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
            // Check if we were successful in obtaining the map.
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        Log.e(TAG, "onMapReady called");
                        loadMap(map);
                    }
                });
            }
            else {
                Toast.makeText(requireActivity(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // The Map is verified. It is now safe to manipulate the map.
    protected void loadMap(GoogleMap googleMap) {
        Log.e(TAG, "loadMap called");
        if (googleMap != null) {
            map = googleMap;
            map.setOnCameraIdleListener(this);
            map.setOnMarkerClickListener(this);
            displayLocation();
        }
        else {
            Toast.makeText(requireActivity(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void displayLocation() {
        Log.e(TAG, "displayLocation called");
        if (location != null && map != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.setMyLocationEnabled(true);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
            fetchEntries();
        }
    }

    private void setUpLocationManager() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager == null) {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            }

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.getMainLooper());
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
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
        } else {
            setUpLocationManager();
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
                setUpLocationManager();
            } else {
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

    @SuppressLint("MissingPermission")
    private Location getCurrentUserLocation() {
        Log.e(TAG, "getCurrentUserLocation called");
        if (locationManager != null) {
            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        return null;
    }

    private void fetchEntries() {
        if (map == null) return;

        VisibleRegion region = map.getProjection().getVisibleRegion();

        List<ParseGeoPoint> points = new ArrayList<>();
        points.add(new ParseGeoPoint(region.farLeft.latitude, region.farLeft.longitude));
        points.add(new ParseGeoPoint(region.farRight.latitude, region.farRight.longitude));
        points.add(new ParseGeoPoint(region.nearRight.latitude, region.nearRight.longitude));
        points.add(new ParseGeoPoint(region.nearLeft.latitude, region.nearLeft.longitude));

        ParsePolygon polygon = new ParsePolygon(points);

        entryManager.fetchEntries(visibility, Entry.Ordering.DATE_DESCENDING, new Search(Search.SearchType.POLYGON, polygon), null, entriesFound -> {
            entries = entriesFound;
            updateEntriesShownOnMap();
        });
    }

    private void updateEntriesShownOnMap() {

        Set<String> unseenEntryIds = new HashSet<>(markers.keySet());

        for (Entry entry : entries) {
            if (!markers.keySet().contains(entry.getObjectId())) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(entry.getLocation().getLatitude(), entry.getLocation().getLongitude()))
                        .title(entry.getTitle())
                        .snippet(entry.getText());
                Marker marker = map.addMarker(markerOptions);
                marker.setDraggable(false);
                markers.put(entry.getObjectId(), marker);
            } else {
                unseenEntryIds.remove(entry.getObjectId());
            }
        }

        for (String entryId : unseenEntryIds) {
            Marker marker = markers.remove(entryId);
            marker.remove();
        }
    }
}