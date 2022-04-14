package com.group24.interactivediary.ui.listview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group24.interactivediary.ListviewAdapter;

public class ListviewViewModel extends ViewModel {
    private final MutableLiveData<Integer> entryType = new MutableLiveData<>();
    private ListviewAdapter listviewAdapter;

    public ListviewViewModel() {}

    public void setEntryType(Integer setTo) {
        entryType.setValue(setTo);
    }

    public LiveData<Integer> getEntryType() {
        return entryType;
    }

    public void setListviewAdapter(ListviewAdapter setTo) {
        listviewAdapter = setTo;
    }

    public ListviewAdapter getListviewAdapter() {
        return listviewAdapter;
    }
}