package com.group24.interactivediary.ui.mapview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapviewViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MapviewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mapview fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}