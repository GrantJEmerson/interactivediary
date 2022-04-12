package com.group24.interactivediary.ui.listview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group24.interactivediary.databinding.FragmentListviewBinding;

public class ListviewFragment extends Fragment {
    public static final String TAG = "ListviewFragment";

    private FragmentListviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListviewViewModel listviewViewModel =
                new ViewModelProvider(this).get(ListviewViewModel.class);

        binding = FragmentListviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textviewListview;
        listviewViewModel.getSelected().observe(getViewLifecycleOwner(), entryType -> {
            switch (entryType) {
                case 0: // private
                    textView.setText("viewing list view for private entries");
                    break;
                case 1: // shared
                    textView.setText("viewing list view for shared entries");
                    break;
                case 2: // public
                    textView.setText("viewing list view for public entries");
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