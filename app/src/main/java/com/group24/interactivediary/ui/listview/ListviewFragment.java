package com.group24.interactivediary.ui.listview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.group24.interactivediary.R;

import org.jetbrains.annotations.NotNull;

public class ListviewFragment extends Fragment {
    public static final String TAG = "ListviewFragment";

    // Views in the layout
    RecyclerView listviewRecyclerView;

    // Other necessary member variables
    private ViewModelProvider viewModelProvider;
    private ListviewViewModel listviewViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the views in the layout
        listviewRecyclerView = view.findViewById(R.id.listviewRecyclerView);

        // Initialize other member variables
        viewModelProvider = new ViewModelProvider(requireActivity());
        listviewViewModel = viewModelProvider.get(ListviewViewModel.class);

        // TODO: populate recyclerview
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}