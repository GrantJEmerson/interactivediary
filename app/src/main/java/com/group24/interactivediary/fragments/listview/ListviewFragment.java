package com.group24.interactivediary.fragments.listview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.group24.interactivediary.EndlessRecyclerViewScrollListener;
import com.group24.interactivediary.models.Entry;
import com.group24.interactivediary.adapters.EntryAdapter;
import com.group24.interactivediary.R;
import com.group24.interactivediary.models.Search;
import com.group24.interactivediary.networking.EntryManager;
import com.group24.interactivediary.networking.FetchCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListviewFragment extends Fragment {
    public static final String TAG = "ListviewFragment";

    // Views in the layout
    TextView nothingHereYet;
    SearchView searchView;
    Spinner searchTypeSpinner;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    // Other necessary member variables
    private ViewModelProvider viewModelProvider;
    private ListviewViewModel listviewViewModel;
    private Search.SearchType searchType;
    private List<Entry> entries;
    private EntryAdapter entryAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private EntryManager entryManager;
    private Search search;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the views in the layout
        nothingHereYet = view.findViewById(R.id.listviewNothingHereYet);
        searchView = view.findViewById(R.id.listviewSearchView);
        searchTypeSpinner = view.findViewById(R.id.listviewSearchTypeSpinner);
        swipeRefreshLayout = view.findViewById(R.id.listviewSwipeRefreshLayout);
        recyclerView = view.findViewById(R.id.listviewRecyclerView);

        // Initialize other member variables
        viewModelProvider = new ViewModelProvider(requireActivity());
        listviewViewModel = viewModelProvider.get(ListviewViewModel.class);
        entries = new ArrayList<>();
        entryAdapter = new EntryAdapter(requireActivity(), entries);
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        entryManager = new EntryManager(requireActivity());
        search = null;

        // Set up nothingHereYet TextView
        listviewViewModel.getNothingHereYetText().observe(getViewLifecycleOwner(), nothingHereYet::setText);

        // Set up recycler view
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(entryAdapter);

        // Set up search view
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) searchTypeSpinner.setVisibility(View.VISIBLE);
                else searchTypeSpinner.setVisibility(View.GONE);

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = new Search(searchType, query) {};
                populateHomeTimeline(listviewViewModel.getVisibility().getValue());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                search = null;
                populateHomeTimeline(listviewViewModel.getVisibility().getValue());

                return false;
            }
        });

        // Set up search type spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> searchTypeAdapter = ArrayAdapter.createFromResource(requireActivity(), R.array.search_type_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        searchTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        searchTypeSpinner.setAdapter(searchTypeAdapter);
        // Set it to the default value
        searchTypeSpinner.setSelection(0);

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSearchType = getResources().getStringArray(R.array.search_type_array)[position];

                if (selectedSearchType.equals("Title")) searchType = Search.SearchType.TITLE;
                else if (selectedSearchType.equals("Date")) searchType = Search.SearchType.DATE;
                else searchType = Search.SearchType.LOCATION;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Just search by title by default
                searchType = Search.SearchType.TITLE;
            }
        });

        listviewViewModel.getVisibility().observe(getViewLifecycleOwner(), new Observer<Entry.Visibility>() {
            @Override
            public void onChanged(Entry.Visibility visibility) {
                Log.e(TAG, "onChanged called");
                populateHomeTimeline(visibility);
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, new EndlessRecyclerViewScrollListener.LoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.e(TAG, "onLoadMore called");
                loadMorePosts(listviewViewModel.getVisibility().getValue());
            }
        });

        recyclerView.addOnScrollListener(scrollListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
        entryManager.fetchEntries(visibility, Entry.Ordering.DATE_ASCENDING, search, null, entriesFound -> {
            // Clear out old items before appending in the new ones
            entryAdapter.clear();
            // Save received posts to list and notify adapter of new data
            entryAdapter.addAll(entriesFound);
            // Show empty message if gallery is empty
            if (entryAdapter.getItemCount() == 0) nothingHereYet.setVisibility(View.VISIBLE);
            else nothingHereYet.setVisibility(View.GONE);
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadMorePosts(Entry.Visibility visibility) {
        // TODO: add other query options depending on user preferences
        Date latestEntry = entries.get(entries.size()-1).getUpdatedAt();
        entryManager.fetchEntries(visibility, Entry.Ordering.DATE_ASCENDING, search, latestEntry, entriesFound -> {
            // Save received posts to list and notify adapter of new data
            entryAdapter.addAll(entriesFound);
            // Show empty message if gallery is empty
            if (entryAdapter.getItemCount() == 0) nothingHereYet.setVisibility(View.VISIBLE);
            else nothingHereYet.setVisibility(View.GONE);
        });
        swipeRefreshLayout.setRefreshing(false);
    }
}