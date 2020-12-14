package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterSourceList extends RecyclerView.Adapter<AdapterSourceList.HolderSourceList> implements Filterable {

    private Context context;
    public ArrayList<ModelSourceList> sourceLists, filterList;
    private FilterSourceList filter;


    public AdapterSourceList(Context context, ArrayList<ModelSourceList> sourceLists) {
        this.context = context;
        this.sourceLists = sourceLists;
        this.filterList = sourceLists;
    }


    @NonNull
    @Override
    public HolderSourceList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_source_list,parent,false);
        return new HolderSourceList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSourceList holder, int position) {
        //get data
        ModelSourceList model = sourceLists.get(position);

        final String id = model.getId();
        final String name = model.getName();
        final String description = model.getDescription();
        final String country = model.getCountry();
        final String category = model.getCategory();
        final String language = model.getLanguage();

        //set data to ui views

        holder.nameTv.setText(name);
        holder.descriptionTv.setText(description);
        holder.countryTv.setText("Country :" +country);
        holder.categoryTv.setText("Category :" +category);
        holder.languageTv.setText("Language :" +language);

        //handle click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open details of news source in new activity
                //pass data of clicked news source while starting activity
                Intent intent = new Intent(context, NewsSourceDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                intent.putExtra("country", country);
                intent.putExtra("category", category);
                intent.putExtra("language", language);
                context.startActivity(intent);



            }
        });

    }

    @Override
    public int getItemCount() {
        return sourceLists.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterSourceList(this,filterList);
        }
        return filter;
    }

    class HolderSourceList extends RecyclerView.ViewHolder{

        TextView nameTv, descriptionTv,countryTv,categoryTv,languageTv;
        public HolderSourceList(@NonNull View itemView) {
            super(itemView);
            nameTv =itemView.findViewById(R.id.nameTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            countryTv = itemView.findViewById(R.id.countryTv);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            languageTv = itemView.findViewById(R.id.languageTv);

        }
    }
}
