package com.example.newsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsSourceDetailActivity extends AppCompatActivity {

    private TextView nameTv,descriptionTv,countryTv,categoryTv,languageTv;
    private RecyclerView newsRv;
    private ArrayList<ModelNewsSourceDetail> newsSourceDetails;
    private AdapterNewsSourceDetail adapterNewsSourceDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_source_detail);

        nameTv = findViewById(R.id.nameTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        countryTv = findViewById(R.id.countryTv);
        categoryTv = findViewById(R.id.categoryTv);
        languageTv = findViewById(R.id.languageTv);
        newsRv = findViewById(R.id.newsRv);

        //actionBar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Latest News");
        //add back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    //get data from intent
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String country = intent.getStringExtra("country");
        String language = intent.getStringExtra("language");
        String category = intent.getStringExtra("category");

        actionBar.setTitle(name);//set title/name of news source we selected
        nameTv.setText(name);
        descriptionTv.setText(description);
        categoryTv.setText("Category: "+category);
        countryTv.setText("Country: "+country);
        languageTv.setText("Language: "+language);

        loadNewsData(id);
    }

    private void loadNewsData(String id) {
        //init list
        newsSourceDetails = new ArrayList<>();
        String url ="https://newsapi.org/v2/top-headlines?sources="+ id +"&apiKey="+ Constants.API_KEY;
        //progressBar
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading News");
        progressDialog.show();

        //request data
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    //we need to get array out of that object
                    JSONArray jsonArray = jsonObject.getJSONArray("articles");
                    //get all data from that array using loop
                    for(int i=0; i<jsonArray.length(); i++){
                        //each array element is a json object get at specific position
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        //get actual data from that json object
                        String title = jsonObject1.getString("title");
                        String description = jsonObject1.getString("description");
                        String url = jsonObject1.getString("url");
                        String urlToImage = jsonObject1.getString("urlToImage");
                        String publishedAt = jsonObject1.getString("publishedAt");
                        String content = jsonObject1.getString("content");

                        //we need to convert date format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String formattedDate="";
                        try{
                            //try to format date time
                            Date date = dateFormat.parse(publishedAt);
                            formattedDate = dateFormat1.format(date);
                        }catch (Exception e){
                            formattedDate = publishedAt;

                        }
                        //add data  new instance of model
                        ModelNewsSourceDetail model = new ModelNewsSourceDetail(""+title,
                                ""+description,
                                ""+url,
                                ""+urlToImage,
                                ""+formattedDate,
                                ""+content);
                    }
                    progressDialog.dismiss();
                    //setup adapter
                    adapterNewsSourceDetail = new AdapterNewsSourceDetail(NewsSourceDetailActivity.this,newsSourceDetails);
                    newsRv.setAdapter(adapterNewsSourceDetail);

                }catch (Exception e){
                    progressDialog.dismiss();
                    Toast.makeText(NewsSourceDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(NewsSourceDetailActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //add request to volley Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}