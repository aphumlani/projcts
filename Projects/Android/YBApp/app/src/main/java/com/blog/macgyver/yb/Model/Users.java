package com.blog.macgyver.yb.Model;

public class Users {

    private String image, name;

    public Users(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public Users() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
