package com.group24.interactivediary.ui.mapview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapviewViewModel extends ViewModel {
    private final MutableLiveData<Integer> entryType = new MutableLiveData<>();

    public MapviewViewModel() {}

    public void setEntryType(Integer setTo) {
        entryType.setValue(setTo);
    }

    public LiveData<Integer> getEntryType() {
        return entryType;
    }
}