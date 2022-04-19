package com.group24.interactivediary.model;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

public class Comment {
    User author;
    String text;
    Date lastUpdateDate;

    public Comment() {
        author = User.currentUser;
        text = "";
        lastUpdateDate = new Date();
    }

    public Comment(ParseObject parseObject) {
        ParseUser authorObject = parseObject.getParseUser("author");
        author = new User(authorObject);
        text = parseObject.getString("text");
        lastUpdateDate = parseObject.getDate("updatedAt");
    }
}
