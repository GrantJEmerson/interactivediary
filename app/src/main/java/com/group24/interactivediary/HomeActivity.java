package com.group24.interactivediary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.group24.interactivediary.databinding.ActivityHomeBinding;
import com.group24.interactivediary.ui.listview.ListviewFragment;
import com.group24.interactivediary.ui.listview.ListviewViewModel;
import com.group24.interactivediary.ui.mapview.MapviewViewModel;

import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";

    private ActivityHomeBinding binding;
    private String[] tabTitles = {"Private", "Shared", "Public"};
    private int[] tabIcons = {R.drawable.ic_baseline_person_24, R.drawable.ic_baseline_people_24, R.drawable.ic_baseline_public_24};

    private Fragment curFragment;
    private ViewModelProvider viewModelProvider;
    private ListviewViewModel listviewViewModel;
    private MapviewViewModel mapviewViewModel;

    // Views in the layout
    private TabLayout entryTypeTabLayout;
    private ViewPager2 entryTypeViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Set up the bottom nav bar
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_listview, R.id.navigation_mapview)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setOnItemSelectedListener(item -> {
            Log.e(TAG, item.toString());
            updateCurFragment();
            return true;
        });

        // Set up the top nav bar
        entryTypeTabLayout = binding.entryTypeTabLayout;
        entryTypeViewPager = binding.entryTypeViewPager;

        // Get the viewModels of the current instances of listview and mapview fragments
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.bottom_nav);
        viewModelProvider = new ViewModelProvider(backStackEntry);
        listviewViewModel = viewModelProvider.get(ListviewViewModel.class);
        Log.e(TAG, listviewViewModel.toString());
        mapviewViewModel = viewModelProvider.get(MapviewViewModel.class);
        Log.e(TAG, mapviewViewModel.toString());

        updateCurFragment();

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(entryTypeTabLayout, entryTypeViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitles[position]);
                tab.setIcon(tabIcons[position]);
                Log.e(TAG, "listviewViewModel.select(" + position + ");");
                Log.e(TAG, "mapviewViewModel.select(" + position + ");");
                listviewViewModel.setEntryType(position);
                mapviewViewModel.setEntryType(position);
            }
        });
        tabLayoutMediator.attach();

        // Set up the entry add button
        binding.fab.setOnClickListener(new View.OnClickListener() {
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

    private void updateCurFragment() {
        // Find current fragment being displayed
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_home);
        curFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        Log.e(TAG, navHostFragment.getChildFragmentManager().getFragments().size() + " " + navHostFragment.getChildFragmentManager().getFragments().toString());
        Log.e(TAG, curFragment.getClass().getCanonicalName());
        // Set adapter to viewpager
        if (curFragment.getClass().equals(ListviewFragment.class)) {
            if (listviewViewModel.getListviewAdapter() == null) {
                listviewViewModel.setListviewAdapter(new ListviewAdapter(this));
            }
            entryTypeViewPager.setAdapter(listviewViewModel.getListviewAdapter());
        }
        else {
            if (mapviewViewModel.getMapviewAdapter() == null) {
                mapviewViewModel.setMapviewAdapter(new MapviewAdapter(this));
            }
            entryTypeViewPager.setAdapter(mapviewViewModel.getMapviewAdapter());
        }
    }
}