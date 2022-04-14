package com.group24.interactivediary.ui.listview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListviewViewModel extends ViewModel {
    private final MutableLiveData<Integer> entryType = new MutableLiveData<>();

    public ListviewViewModel() {}

    public void setEntryType(Integer setTo) {
        entryType.setValue(setTo);
    }

    public LiveData<Integer> getEntryType() {
        return entryType;
    }
}