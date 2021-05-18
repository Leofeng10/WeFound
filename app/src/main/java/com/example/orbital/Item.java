package com.example.orbital;

import android.graphics.Bitmap;

public class Item {

    public String name;
    public String location;
    public String description;
    public String time;
    public String phone;
    public String username;
    public Bitmap image;


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

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public Item(String name, String location, String description, String time, String phone, String username, Bitmap image) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.time = time;
        this.phone = phone;
        this.username = username;
        this.image = image;
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
