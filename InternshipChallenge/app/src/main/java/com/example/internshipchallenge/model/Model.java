package com.example.internshipchallenge.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Model {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    private String tags;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getTags() {
        return tags;
    }
}
