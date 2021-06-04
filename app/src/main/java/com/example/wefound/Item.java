package com.example.wefound;

import android.graphics.Bitmap;
import android.net.Uri;

public class Item {

    public String name;
    public String location;
    public String description;
    public String time;
    public String phone;
    public String username;
    public String imageurl;
    public String userID;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", imageurl='" + imageurl + '\'' +
                '}';
    }

    public Item() {
    }

    public Item(String name, String location, String description, String time, String phone, String username, String imageurl, String userID) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.time = time;
        this.phone = phone;
        this.username = username;
        this.imageurl = imageurl;
        this.userID = userID;

    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
