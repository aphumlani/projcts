package com.blog.macgyver.yb.Model;


import java.util.Date;

public class Comments {

    private String comments, user_id;
    private Date timestamp;

    public Comments() {
    }

    public Comments(String comments, String user_id, Date timestamp) {
        this.comments = comments;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
