package com.example.along_the_road;

import android.graphics.drawable.Drawable;

public class ListReview {

    private int _id;
    private String title;
    private String name;
    private int like;
    private Drawable image;

    public ListReview(int _id, String title, String name) {
        this._id = _id;
        this.title = title;
        this.name = name;
    }

    public ListReview(int _id, String title, String name, int like) {
        this._id = _id;
        this.title = title;
        this.name = name;
        this.like = like;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int get_id() {
        return _id;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

}

