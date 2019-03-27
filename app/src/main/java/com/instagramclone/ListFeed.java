package com.instagramclone;

import android.support.v7.widget.RecyclerView;

public class ListFeed {

    private String username;
    private String image;
    private String timeStamp;

    public ListFeed(String username, String image, String timeStamp) {
        this.username = username;
        this.image = image;
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
