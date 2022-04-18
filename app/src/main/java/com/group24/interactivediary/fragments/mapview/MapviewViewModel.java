package com.group24.interactivediary.fragments.mapview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group24.interactivediary.models.Entry;

public class MapviewViewModel extends ViewModel {
    private final MutableLiveData<Entry.Visibility> visibility = new MutableLiveData<>();

    public MapviewViewModel() {}

    public void setVisibility(Entry.Visibility setTo) {
        visibility.setValue(setTo);
    }

    public LiveData<Entry.Visibility> getVisibility() {
        return visibility;
    }
}