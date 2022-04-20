package com.group24.interactivediary.models;

public class Search {
    public enum SearchType {
        TITLE,
        DATE,
        LOCATION
    }

    public Object searchParameter;
    public SearchType type;

    /*
     * If you pass a type DATE, pass a java.util.Date object.
     * If you pass a type TITLE, pass a String.
     * If you pass a type LOCATION, pass a android.location.Location.
     * */
    public Search(SearchType type, Object searchParameter) {
        this.searchParameter = searchParameter;
        this.type = type;
    }
}