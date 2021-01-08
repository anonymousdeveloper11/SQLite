package com.example.internshipchallenge.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.internshipchallenge.R;
import com.example.internshipchallenge.model.Model;
import com.example.internshipchallenge.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TagFragment extends Fragment {


    private TextView tagTv;

    public TagFragment() {
        // Required empty public constructor
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.technorio.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);


        Call<List<Model>> call = service.getTags();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    tagTv.setText("Code:" + response.code());
                    return;
                }
                List<Model> tags = (List<Model>) response.body();

                for (Model tag : tags) {
                    String content = "";

                    content += "Tag: " +tag.getTags();
                    tagTv.append(content);

                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {

                tagTv.setText(t.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tag, container, false);
        tagTv = view.findViewById(R.id.tagTv);
        return view;
    }
}