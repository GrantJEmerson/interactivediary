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

    private FragmentMapviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapviewViewModel mapviewViewModel =
                new ViewModelProvider(this).get(MapviewViewModel.class);

        binding = FragmentMapviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textviewMapview;
        mapviewViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}