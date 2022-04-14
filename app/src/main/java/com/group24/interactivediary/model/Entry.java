package com.group24.interactivediary.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Entry {
    public enum Visibility {
        PRIVATE,
        SHARED,
        PUBLIC
    }

    public enum Ordering {
        TITLE_ASCENDING,
        TITLE_DESCENDING,
        DATE_ASCENDING,
        DATE_DESCENDING,
        NEAREST
    }

    User author;
    List<User> contributors;

    String title;
    String text;

    List<Pair<Bitmap, String>> images;
    List<Pair<File, String>> videos;

    Visibility visibility;

    Date creationDate;
    Date lastUpdateDate;

    ParseGeoPoint location;

    List<Comment> comments;

    public Entry() {
        author = User.currentUser;
        contributors = new ArrayList<>();

        title = "";
        text = "";

        images = new ArrayList<>();
        videos = new ArrayList<>();

        visibility = Visibility.PRIVATE;

        creationDate = new Date();
        lastUpdateDate = new Date();

        location = new ParseGeoPoint();

        comments = new ArrayList<>();
    }

    public Entry(ParseObject parseObject) {
        ParseUser authorObject = parseObject.getParseUser("author");
        author = new User(authorObject);

        List<ParseUser> contibutorObjects = parseObject.getList("contributors");
        contributors = new ArrayList<>();

        for (ParseUser contibutorObject : contibutorObjects) {
            contributors.add(new User(contibutorObject));
        }

        title = parseObject.getString("title");
        text = parseObject.getString("text");

        List<String> mediaItemDescriptions = parseObject.getList("mediaItemDescriptions");
        List<ParseFile> mediaItemPFiles = parseObject.getList("mediaItems");
        images = new ArrayList<>();
        videos = new ArrayList<>();

        for (int i = 0; i < mediaItemPFiles.size(); i++) {
            try {
                File mediaItemFile = mediaItemPFiles.get(i).getFile();
                String mediaItemFilePath = mediaItemFile.getPath();
                String mimeType = "";

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    try {
                        mimeType = Files.probeContentType(mediaItemFile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (mimeType.startsWith("image")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(mediaItemFilePath);
                    images.add(new Pair<>(bitmap, mediaItemDescriptions.get(i)));
                }

                if (mimeType.startsWith("video")) {
                    videos.add(new Pair<>(mediaItemFile, mediaItemDescriptions.get(i)));
                }
            } catch (ParseException exception) {
                System.out.println("Could not download media item.");
            }
        }

        visibility = Visibility.valueOf(parseObject.getString("visibility"));

        creationDate = parseObject.getDate("createdAt");
        lastUpdateDate = parseObject.getDate("updatedAt");

        location = parseObject.getParseGeoPoint("location");

        List<ParseObject> commentObjects = parseObject.getList("comments");
        comments = new ArrayList<>();

        for (ParseObject commentObject : commentObjects) {
            comments.add(new Comment(commentObject));
        }
    }
}
