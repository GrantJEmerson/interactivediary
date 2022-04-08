package com.group24.interactivediary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.group24.interactivediary.databinding.ActivityEntryCreateBinding;

public class EntryCreateActivity extends AppCompatActivity {
    private ActivityEntryCreateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_create);

        binding = ActivityEntryCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
    }
}