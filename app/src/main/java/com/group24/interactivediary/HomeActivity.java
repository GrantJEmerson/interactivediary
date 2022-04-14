package com.group24.interactivediary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.group24.interactivediary.model.Entry;
import com.group24.interactivediary.networking.EntryManager;
import com.group24.interactivediary.ui.listview.ListviewFragment;
import com.group24.interactivediary.ui.listview.ListviewViewModel;
import com.group24.interactivediary.ui.mapview.MapviewFragment;
import com.group24.interactivediary.ui.mapview.MapviewViewModel;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";

    public static final int PRIVATE = 0;
    public static final int SHARED = 1;
    public static final int PUBLIC = 2;

    private String[] tabTitles = {"Private", "Shared", "Public"};
    private int[] tabIcons = {R.drawable.ic_baseline_person_24, R.drawable.ic_baseline_people_24, R.drawable.ic_baseline_public_24};

    // Views in the layout
    private Toolbar toolbar;
    private Button privateButton;
    private Button sharedButton;
    private Button publicButton;
    private BottomNavigationView navView;
    private FloatingActionButton fab;

    // Other necessary member variables
    private ListviewFragment listviewFragment;
    private MapviewFragment mapviewFragment;
    private FragmentManager fragmentManager;
    private ViewModelProvider viewModelProvider;
    private ListviewViewModel listviewViewModel;
    private MapviewViewModel mapviewViewModel;
    private EntryManager entryManager;
    private List<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the views in the layout
        toolbar = findViewById(R.id.toolbar);
        privateButton = findViewById(R.id.homePrivateButton);
        sharedButton = findViewById(R.id.homeSharedButton);
        publicButton = findViewById(R.id.homePublicButton);
        navView = findViewById(R.id.homeBottomNavView);
        fab = findViewById(R.id.homeCreateEntryButton);

        // Initialize other member variables
        fragmentManager = getSupportFragmentManager();
        listviewFragment = new ListviewFragment();
        mapviewFragment = new MapviewFragment();
        viewModelProvider = new ViewModelProvider(this);
        listviewViewModel = viewModelProvider.get(ListviewViewModel.class);
        mapviewViewModel = viewModelProvider.get(MapviewViewModel.class);
        entryManager = new EntryManager(getApplicationContext());

        entryManager.fetchEntries(Entry.Visibility.PRIVATE, Entry.Ordering.DATE_DESCENDING, null, (entries) -> {
            this.entries = entries;
        });

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Set up the bottom nav bar
        // Set viewPager adapter to listviewAdapter by default
        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_listview) displayListviewFragment();
            if (item.getItemId() == R.id.navigation_mapview) displayMapviewFragment();
            return true;
        });

        // Set up the top nav bar
        privateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listviewViewModel.setEntryType(PRIVATE);
                mapviewViewModel.setEntryType(PRIVATE);
            }
        });

        sharedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listviewViewModel.setEntryType(SHARED);
                mapviewViewModel.setEntryType(SHARED);
            }
        });

        publicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listviewViewModel.setEntryType(PUBLIC);
                mapviewViewModel.setEntryType(PUBLIC);
            }
        });

        // Set up the entry add button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goEntryCreateActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Starts an intent to go to the EntryCreate activity
    private void goEntryCreateActivity() {
        Intent intent = new Intent(this, EntryCreateActivity.class);
        startActivity(intent);
    }

    private void displayListviewFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (listviewFragment.isAdded()) { // if the fragment is already in container
            ft.show(listviewFragment);
        }
        else { // fragment needs to be added to frame container
            ft.add(R.id.homeFragmentContainer, listviewFragment, "listViewFragment");
        }
        // Hide mapviewFragment
        if (mapviewFragment.isAdded()) {
            ft.hide(mapviewFragment);
        }
        // Commit changes
        ft.commit();
    }

    private void displayMapviewFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (mapviewFragment.isAdded()) { // if the fragment is already in container
            ft.show(mapviewFragment);
        }
        else { // fragment needs to be added to frame container
            ft.add(R.id.homeFragmentContainer, mapviewFragment, "mapViewFragment");
        }
        // Hide listviewFragment
        if (listviewFragment.isAdded()) {
            ft.hide(listviewFragment);
        }
        // Commit changes
        ft.commit();
    }
}