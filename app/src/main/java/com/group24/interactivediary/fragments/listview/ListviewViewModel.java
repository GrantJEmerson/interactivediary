package com.group24.interactivediary.fragments.listview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group24.interactivediary.models.Entry;

public class ListviewViewModel extends ViewModel {
    private final MutableLiveData<Entry.Visibility> visibility = new MutableLiveData<>();
    private final MutableLiveData<String> nothingHereYetText = new MutableLiveData<>();

    public ListviewViewModel() {}

    public void setVisibility(Entry.Visibility setTo) {
        visibility.setValue(setTo);
    }

    public LiveData<Entry.Visibility> getVisibility() {
        return visibility;
    }

    public void setNothingHereYetText(String s) {
        nothingHereYetText.setValue(s);
    }

    public MutableLiveData<String> getNothingHereYetText() {
        return nothingHereYetText;
    }
}