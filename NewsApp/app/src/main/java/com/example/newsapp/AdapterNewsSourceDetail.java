package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterNewsSourceDetail extends RecyclerView.Adapter<AdapterNewsSourceDetail.HolderNewsSourceDetail>{

    private Context context;
    private ArrayList<ModelNewsSourceDetail> newsSourceDetails;

    public AdapterNewsSourceDetail(Context context, ArrayList<ModelNewsSourceDetail> newsSourceDetails) {
        this.context = context;
        this.newsSourceDetails = newsSourceDetails;
    }

    @NonNull
    @Override
    public HolderNewsSourceDetail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_news_source_details,parent,false);
        return new HolderNewsSourceDetail(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNewsSourceDetail holder, int position) {
        //get data
        ModelNewsSourceDetail model = newsSourceDetails.get(position);

        final String content = model.getContent();
        String description = model.getDescription();
        String publishedAt = model.getPublishedAt();
        String title = model.getTitle();
        final String url = model.getUrl();
        String urlToImage = model.getUrlToImage();

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(publishedAt);
        Picasso.get().load(urlToImage).into(holder.imageIv);

        //handle click view complete details of news
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,NewsDetailActivity.class);
                intent.putExtra("url",url);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsSourceDetails.size();
    }

    class  HolderNewsSourceDetail extends RecyclerView.ViewHolder{
        TextView titleTv,descriptionTv,dateTv;
        ImageView imageIv;
        public HolderNewsSourceDetail(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            imageIv = itemView.findViewById(R.id.imageIv);

        }
    }
}
