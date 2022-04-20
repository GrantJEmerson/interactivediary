package com.group24.interactivediary.models;

public class GeneralDate {
    private int month;
    private int day;

    public GeneralDate(int month, int day) {
        this.month = month;
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }
}
