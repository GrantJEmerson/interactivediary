package com.group24.interactivediary.models;

public class Search {
    public enum SearchType {
        TITLE,
        DATE,
        LOCATION,
        POLYGON
    }

    public Object searchParameter;
    public SearchType type;

    /*
     * If you pass a type DATE, pass a java.util.Date object.
     * If you pass a type TITLE, pass a String.
     * If you pass a type LOCATION, pass a android.location.Location.
     * If you pass a type POLYGON pass a com.parse.ParsePolygon
     * */
    public Search(SearchType type, Object searchParameter) {
        this.searchParameter = searchParameter;
        this.type = type;
    }
}