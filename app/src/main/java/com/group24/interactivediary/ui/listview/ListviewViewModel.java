package com.group24.interactivediary.ui.listview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListviewViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ListviewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is listview fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}