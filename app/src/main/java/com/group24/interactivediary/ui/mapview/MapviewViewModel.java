package com.group24.interactivediary.ui.mapview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group24.interactivediary.MapviewAdapter;

public class MapviewViewModel extends ViewModel {
    private final MutableLiveData<Integer> entryType = new MutableLiveData<>();
    private MapviewAdapter mapviewAdapter;

    public MapviewViewModel() {}

    public void setEntryType(Integer setTo) {
        entryType.setValue(setTo);
    }

    public LiveData<Integer> getEntryType() {
        return entryType;
    }

    public void setMapviewAdapter(MapviewAdapter setTo) {
        mapviewAdapter = setTo;
    }

    public MapviewAdapter getMapviewAdapter() {
        return mapviewAdapter;
    }
}