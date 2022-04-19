package com.group24.interactivediary.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.group24.interactivediary.networking.FetchCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.ArrayList;

public class User {

    public static User currentUser = new User(ParseUser.getCurrentUser());

    public String objectID;
    public String username;
    private Bitmap profilePicture;
    private ParseFile profilePictureFile;

    public User(ParseUser parseUser) {
        objectID = parseUser.getObjectId();
        username = parseUser.getUsername();
        profilePictureFile = parseUser.getParseFile("profilePicture");
        profilePicture = null;
    }

    public void getProfilePicture(FetchCallback<Bitmap> callback) {
        if (profilePicture != null) {
            callback.done(profilePicture);
            return;
        }

        profilePictureFile.getFileInBackground((file, exception) -> {
            if (exception == null) {
                profilePicture = BitmapFactory.decodeFile(file.getPath());
            } else {
                profilePicture = null;
                System.out.println("Could not fetch profile picture.");
            }
            callback.done(profilePicture);
        });
    }

    public void fetchFriends(FetchCallback<List<User>> callback) {
        ParseQuery<ParseObject> userQuery = new ParseQuery<>("User");

        userQuery.whereEqualTo("objectId", objectID);
        userQuery.include("friends");

        userQuery.getFirstInBackground((user, error) -> {
            List<User> friends = new ArrayList<User>();

            if (error == null) {
                ParseObject matchingUser = user;
                List<ParseUser> friendObjects = matchingUser.getList("friends");

                for (ParseUser friendObject : friendObjects) {
                    User friend = new User(friendObject);
                    friends.add(friend);
                }
            } else {
                System.out.println("Could not fetch user's friends.");
            }

            callback.done(friends);
        });
    }
}
