package com.group24.interactivediary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group24.interactivediary.ui.listview.ListviewFragment;

public class ListviewAdapter extends FragmentStateAdapter {
    public ListviewAdapter(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public Fragment createFragment(int position) {
        return new ListviewFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
