package com.group24.interactivediary.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.group24.interactivediary.networking.FetchCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

// Extending the ParseUser class is not recommended, so I am doing this instead
public class DiaryUser {
    public static final String TAG = "DiaryUser";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_GETS_NOTIFICATIONS = "getNotifications";

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

    public void getProfilePicture(FetchCallback<Bitmap> callback) {
        ParseFile profilePictureFile = user.getParseFile(KEY_PROFILE_PICTURE);
        profilePictureFile.getFileInBackground((file, e) -> {
            Bitmap profilePicture;
            if (e == null) {
                profilePicture = BitmapFactory.decodeFile(file.getPath());
            } else {
                profilePicture = null;
                Log.e(TAG, "Could not fetch profile picture: " + e.getLocalizedMessage());
            }
            callback.done(profilePicture);
        });
    }

    public void setProfilePicture(Bitmap profilePicture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] profilePictureBytes = stream.toByteArray();

        ParseFile image = new ParseFile(profilePictureBytes);
        user.put(KEY_PROFILE_PICTURE, image);
    }

    public List<ParseUser> getFriends() {
        return user.getList(KEY_FRIENDS);
    }

    public void setFriends(List<ParseUser> friends) {
        user.put(KEY_FRIENDS, friends);
    }

    public void addFriend(ParseUser friend) {
        List<ParseUser> friends = getFriends();
        friends.add(friend);
        user.put(KEY_FRIENDS, friends);
    }

    public boolean getGetsNotifications() {
        return user.getBoolean(KEY_GETS_NOTIFICATIONS);
    }

    public void setGetsNotifications(boolean getsNotifications) {
        user.put(KEY_GETS_NOTIFICATIONS, getsNotifications);
    }

    // Asynchronously saves the user data to the database
    public void saveInBackground(SaveCallback saveCallback) {
        user.saveInBackground(saveCallback);
    }
}