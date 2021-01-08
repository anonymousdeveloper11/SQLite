package com.example.internshipchallenge.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.internshipchallenge.model.Model;
import com.example.internshipchallenge.R;
import com.example.internshipchallenge.activity.TagDetailsActivity;
import com.example.internshipchallenge.listeners.PostListener;
import com.example.internshipchallenge.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PostFragment extends Fragment implements PostListener {

    private TextView textTv;


    public PostFragment() {
        // Required empty public constructor


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.technorio.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);


        Call <List<Model>> call = service.getPosts();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(!response.isSuccessful()){
                    textTv.setText("Code:" +response.code());
                    return;
                }

                List<Model> posts = (List<Model>) response.body();

                for(Model post : posts){
                    String content ="";
                    content += "ID: " +post.getId() +"\n";
                    content += "Title: " +post.getTitle() +"\n";
                    content += "Body: " +post.getBody() + "\n\n";
                    textTv.append(content);

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                textTv.setText(t.getMessage());
            }
        });
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        textTv = view.findViewById(R.id.textTv);
        return view;




    }

    @Override
    public void onModelClicked(Model model) {
        Intent intent = new Intent(getContext(), TagDetailsActivity.class);
        intent.putExtra("id", model.getId());
        //intent.putExtra("tag", model.getTag());
        startActivity(intent);
    }
}