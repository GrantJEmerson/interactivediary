package com.group24.interactivediary.ui.mapview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group24.interactivediary.databinding.FragmentMapviewBinding;

public class MapviewFragment extends Fragment {
    public static final String TAG = "MapViewFragment";

    private FragmentMapviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapviewViewModel mapviewViewModel =
                new ViewModelProvider(this).get(MapviewViewModel.class);

        binding = FragmentMapviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textviewMapview;
        mapviewViewModel.getSelected().observe(getViewLifecycleOwner(), entryType -> {
            switch (entryType) {
                case 0: // private
                    textView.setText("viewing map view for private entries");
                    break;
                case 1: // shared
                    textView.setText("viewing map view for shared entries");
                    break;
                case 2: // public
                    textView.setText("viewing map view for public entries");
                    break;
                default:
                    break;
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}