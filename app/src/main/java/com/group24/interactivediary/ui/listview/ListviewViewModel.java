package com.group24.interactivediary.ui.listview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListviewViewModel extends ViewModel {

    private final MutableLiveData<Integer> entryType = new MutableLiveData<>();

    public void select(Integer setTo) {
        entryType.setValue(setTo);
    }

    public LiveData<Integer> getSelected() {
        return entryType;
    }
}