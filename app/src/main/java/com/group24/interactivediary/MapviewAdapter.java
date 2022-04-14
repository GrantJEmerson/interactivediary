package com.group24.interactivediary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group24.interactivediary.ui.mapview.MapviewFragment;

public class MapviewAdapter extends FragmentStateAdapter {
    public MapviewAdapter(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public Fragment createFragment(int position) {
        return new MapviewFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
