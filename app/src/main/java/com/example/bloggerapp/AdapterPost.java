package com.example.bloggerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.HolderPost>{

    private Context context;
    private ArrayList<ModelPost> postArrayList;

    public AdapterPost(Context context, ArrayList<ModelPost> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public HolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_post,parent,false);
        return new HolderPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPost holder, int position) {

        //get data ,set data,handle click etc
        ModelPost model = postArrayList.get(position);//get data from specific position
        String authorName = model.getAuthorName();
        String content = model.getContent();
        final String id = model.getId();
        String published = model.getPublished();
        String selfLink = model.getSelfLink();
        String title = model.getTitle();
        String updated = model.getUpdated();
        String url = model.getUrl();

        //content/description is in HTML/web form, we need to convert t to simple text using jsoup libirary
        Document document = Jsoup.parse(content);
        //there may be multiple image, get first image from the post
        try {
            Elements elements = document.select("image");
            String image = elements.get(0).attr("src");
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv);

        }catch (Exception e){
            //exception occured while retriving image maybe due to no image
            holder.imageIv.setImageResource(R.drawable.ic_image_black);

        }
        //format date
        String gmtDate = published;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy K:mm a");
         String formattedDate="";
         try{
             Date date = dateFormat.parse(gmtDate);
             formattedDate = dateFormat1.format(date);
         }catch (Exception e){
             formattedDate = published;
             e.printStackTrace();
         }
         holder.titleTv.setText(title);
         holder.descriptionTv.setText(document.text());
         holder.publishInfoTv.setText("By " + authorName+ ""+formattedDate);

         //handle click and start activity with post id
         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(context,PostDetailsActivity.class);
                 intent.putExtra("postId",id);
                 context.startActivity(intent);

             }
         });
        }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class HolderPost extends RecyclerView.ViewHolder{

        //ui views
        ImageButton moreBtn;
        TextView titleTv,publishInfoTv,descriptionTv;
        ImageView imageIv;
        public HolderPost(@NonNull View itemView) {
            super(itemView);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            titleTv = itemView.findViewById(R.id.titleTv);
            publishInfoTv = itemView.findViewById(R.id.publishInfoTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            imageIv = itemView.findViewById(R.id.imageIv);
        }
    }
}
