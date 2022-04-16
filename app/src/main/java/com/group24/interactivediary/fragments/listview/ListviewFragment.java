package com.group24.interactivediary.fragments.listview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.group24.interactivediary.EndlessRecyclerViewScrollListener;
import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.adapters.EntryAdapter;
import com.group24.interactivediary.R;
import com.group24.interactivediary.networking.EntryManager;
import com.group24.interactivediary.networking.FetchCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListviewFragment extends Fragment {
    public static final String TAG = "ListviewFragment";

    // Views in the layout
    TextView nothingHereYet;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    // Other necessary member variables
    private ViewModelProvider viewModelProvider;
    private ListviewViewModel listviewViewModel;
    private List<Entry> entries;
    private EntryAdapter entryAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private EntryManager entryManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the views in the layout
        nothingHereYet = view.findViewById(R.id.listviewNothingHereYet);
        swipeRefreshLayout = view.findViewById(R.id.listviewSwipeRefreshLayout);
        recyclerView = view.findViewById(R.id.listviewRecyclerView);

        // Initialize other member variables
        viewModelProvider = new ViewModelProvider(requireActivity());
        listviewViewModel = viewModelProvider.get(ListviewViewModel.class);
        entries = new ArrayList<>();
        entryAdapter = new EntryAdapter(requireActivity(), entries);
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        entryManager = new EntryManager(requireActivity(), entryAdapter, nothingHereYet);

        // Set up nothingHereYet TextView
        listviewViewModel.getNothingHereYetText().observe(getViewLifecycleOwner(), nothingHereYet::setText);

        // Set up recycler view
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(entryAdapter);

        listviewViewModel.getVisibility().observe(getViewLifecycleOwner(), new Observer<Entry.Visibility>() {
            @Override
            public void onChanged(Entry.Visibility visibility) {
                Log.e(TAG, "we detected that visibility has changed to " + visibility);
                populateHomeTimeline(visibility);
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, new EndlessRecyclerViewScrollListener.LoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.e(TAG, "onLoadMore called");
                populateHomeTimeline(listviewViewModel.getVisibility().getValue());
            }
        });

        recyclerView.addOnScrollListener(scrollListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // if something goes wrong try adding swipeRefreshLayout.setRefreshing(false)
                Log.e(TAG, "onRefresh called");
                populateHomeTimeline(listviewViewModel.getVisibility().getValue());
            }
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateHomeTimeline(listviewViewModel.getVisibility().getValue());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void populateHomeTimeline(Entry.Visibility visibility) {
        // TODO: add other query options depending on user preferences
        Log.e(TAG, "populateHomeTimeline(" + visibility + ") called");
        entryManager.fetchEntries(visibility, Entry.Ordering.DATE_ASCENDING, null, entries -> {
            Log.e(TAG, "QUERIED SUCCESSFULLY");
        });
    }
}