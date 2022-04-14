package com.group24.interactivediary.ui.mapview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group24.interactivediary.HomeActivity;
import com.group24.interactivediary.R;
import com.group24.interactivediary.ui.listview.ListviewViewModel;

import org.jetbrains.annotations.NotNull;

public class MapviewFragment extends Fragment {
    public static final String TAG = "MapviewFragment";

    // Views in the layout
    private TextView textviewMapview;

    // Other necessary member variables
    private ViewModelProvider viewModelProvider;
    private MapviewViewModel mapviewViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the views in the layout
        textviewMapview = view.findViewById(R.id.textviewMapview);

        // Initialize other member variables
        viewModelProvider = new ViewModelProvider(requireActivity());
        mapviewViewModel = viewModelProvider.get(MapviewViewModel.class);

        mapviewViewModel.getEntryType().observe(getViewLifecycleOwner(), entryType -> {
            switch (entryType) {
                case HomeActivity.PRIVATE: // private
                    textviewMapview.setText("viewing map view for private entries");
                    break;
                case HomeActivity.SHARED: // shared
                    textviewMapview.setText("viewing map view for shared entries");
                    break;
                case HomeActivity.PUBLIC: // public
                    textviewMapview.setText("viewing map view for public entries");
                    break;
                default:
                    break;
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}