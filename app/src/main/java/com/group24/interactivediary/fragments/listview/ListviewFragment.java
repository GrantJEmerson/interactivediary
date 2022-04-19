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
    Spinner sortTypeSpinner;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    // Other necessary member variables
    private ViewModelProvider viewModelProvider;
    private ListviewViewModel listviewViewModel;
    private Entry.Visibility visibility;
    private Search.SearchType searchType;
    private Entry.Ordering sortType;
    private Search search;
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
        searchView = view.findViewById(R.id.listviewSearchView);
        searchTypeSpinner = view.findViewById(R.id.listviewSearchTypeSpinner);
        sortTypeSpinner = view.findViewById(R.id.listviewSortTypeSpinner);
        swipeRefreshLayout = view.findViewById(R.id.listviewSwipeRefreshLayout);
        recyclerView = view.findViewById(R.id.listviewRecyclerView);

        // Initialize other member variables
        viewModelProvider = new ViewModelProvider(requireActivity());
        listviewViewModel = viewModelProvider.get(ListviewViewModel.class);
        visibility = Entry.Visibility.PRIVATE;
        searchType = Search.SearchType.TITLE;
        sortType = Entry.Ordering.TITLE_ASCENDING;
        search = null;
        entries = new ArrayList<>();
        entryAdapter = new EntryAdapter(requireActivity(), entries);
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        entryManager = new EntryManager(requireActivity());

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
                // TODO: figure out how to adapt string query into Date (in case of Date search) or Location (in case of Location search)
                search = new Search(searchType, query) {};
                populateHomeTimeline();

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
                populateHomeTimeline();

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
        // Set it to the default value, title (which is at index 0)
        searchTypeSpinner.setSelection(0);

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        searchType = Search.SearchType.DATE;
                        break;
                    case 2:
                        searchType = Search.SearchType.LOCATION;
                        break;
                    default:
                        searchType = Search.SearchType.TITLE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Just search by title by default
                searchType = Search.SearchType.TITLE;
            }
        });

        // Set up sort type spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sortTypeAdapter = ArrayAdapter.createFromResource(requireActivity(), R.array.sort_type_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        searchTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortTypeSpinner.setAdapter(sortTypeAdapter);
        // Set it to the default value, date ascending (which is at index 3)
        sortTypeSpinner.setSelection(3);

        sortTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sortType = Entry.Ordering.TITLE_ASCENDING;
                        break;
                    case 1:
                        sortType = Entry.Ordering.TITLE_DESCENDING;
                        break;
                    case 2:
                        sortType = Entry.Ordering.DATE_ASCENDING;
                        break;
                    case 4:
                        sortType = Entry.Ordering.NEAREST;
                        // TODO: request location?
                        break;
                    default:
                        sortType = Entry.Ordering.DATE_DESCENDING;
                        break;
                }
                populateHomeTimeline();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Just sort by date descending by default
                sortType = Entry.Ordering.DATE_DESCENDING;
            }
        });

        // Listen for visibility being changed
        listviewViewModel.getVisibility().observe(getViewLifecycleOwner(), new Observer<Entry.Visibility>() {
            @Override
            public void onChanged(Entry.Visibility selectedVisibility) {
                Log.e(TAG, "onChanged called");
                visibility = selectedVisibility;
                populateHomeTimeline();
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, new EndlessRecyclerViewScrollListener.LoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.e(TAG, "onLoadMore called");
                loadMorePosts();
            }
        });

        recyclerView.addOnScrollListener(scrollListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "onRefresh called");
                populateHomeTimeline();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Initial populating of the recycler view
        populateHomeTimeline();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        populateHomeTimeline();
        super.onResume();
    }

    private void populateHomeTimeline() {
        entryManager.fetchEntries(visibility, sortType, search, null, entriesFound -> {
            Log.e(TAG, entriesFound.size() + " entries found");
            // Clear out old items before appending in the new ones
            entryAdapter.clear();
            // Save received posts to list and notify adapter of new data
            entryAdapter.addAll(entriesFound);
            // Show empty message if gallery is empty
            if (entryAdapter.getItemCount() == 0) nothingHereYet.setVisibility(View.VISIBLE);
            else nothingHereYet.setVisibility(View.GONE);
        });
    }

    private void loadMorePosts() {
        if (entries.size() == 0) return;
        Date latestEntry = entries.get(entries.size()-1).getUpdatedAt();
        entryManager.fetchEntries(visibility, sortType, search, latestEntry, entriesFound -> {
            Log.e(TAG, entriesFound.size() + " extra entries found");
            // Save received posts to list and notify adapter of new data
            entryAdapter.addAll(entriesFound);
            // Show empty message if gallery is empty
            if (entryAdapter.getItemCount() == 0) nothingHereYet.setVisibility(View.VISIBLE);
            else nothingHereYet.setVisibility(View.GONE);
        });
    }
}