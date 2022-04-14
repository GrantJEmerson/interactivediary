package com.group24.interactivediary;

import com.parse.ParseUser;
import com.parse.SaveCallback;

// Extending the ParseUser class is not recommended, so I am doing this instead
public class DiaryUser {
    public static final String TAG = "DiaryUser";

    private ParseUser user;

    public DiaryUser(ParseUser user) {
        this.user = user;
    }

    public String getObjectId() {
        return user.getObjectId();
    }

    public String getUsername() {
        return user.getUsername();
    }

    // Asynchronously saves the user data to the database
    public void saveInBackground(SaveCallback saveCallback) {
        user.saveInBackground(saveCallback);
    }
}