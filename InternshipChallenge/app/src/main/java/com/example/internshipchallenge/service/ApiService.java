package com.example.internshipchallenge.service;


import com.example.internshipchallenge.model.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("posts")
    Call<List<Model>> getPosts();

    @GET("tags")
    Call<List<Model>> getTags();


    @GET("posts/{id}/")
    Call<List<Model>> getTagByPostId(@Path("id") int id);
}
