package com.example.internshipchallenge.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.internshipchallenge.service.ApiService;
import com.example.internshipchallenge.model.Model;
import com.example.internshipchallenge.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TagDetailsActivity extends AppCompatActivity {

    private TextView textTagTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_details);
        textTagTv = findViewById(R.id.textTagTv);
        getTags();
    }

    private void getTags() {

        String postId = String.valueOf(getIntent().getIntExtra("id",-1));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.technorio.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);


        Call<List<Model>> call = service.getTagByPostId();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    textTagTv.setText("Code:" + response.code());
                    return;
                }
                List<Model> tags = (List<Model>) response.body();

                for (Model tag : tags) {
                    String content = "";

                    content += "Id: " +postId;
                    content += "Tag: " +tag.getTags();
                    textTagTv.append(content);

                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {

                textTagTv.setText(t.getMessage());
            }
        });
    }

    }
}