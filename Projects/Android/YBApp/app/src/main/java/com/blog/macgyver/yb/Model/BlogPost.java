package com.blog.macgyver.yb.Model;

import com.blog.macgyver.yb.BlogPostId;
import com.google.firebase.Timestamp;

import java.util.Date;

public class BlogPost extends BlogPostId {

    public String title, image,user,thumbnail;
    public Date timestamp;

    public BlogPost() {
    }

    public BlogPost(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BlogPost(String user, String image, String title, String thumbnail) {
        this.user = user;
        this.image = image;
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
