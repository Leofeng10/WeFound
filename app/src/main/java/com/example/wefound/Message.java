package com.example.wefound;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public String getUid() {
        return uid;
    }

    public Message() {

    }

    public Message(String text, String user, String uid) {
        this.text = text;
        this.user = user;
        SimpleDateFormat formatter= new SimpleDateFormat( "HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String currenttime = formatter.format(date);
        this.time = currenttime;
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String text;
    private String user;
    private String time;
    private String uid;
}
