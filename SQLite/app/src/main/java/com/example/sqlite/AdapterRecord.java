package com.example.sqlite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

import java.util.ArrayList;

public class AdapterRecord extends RecyclerView.Adapter<AdapterRecord.HolderRecord>{

    private Context context;
    private ArrayList<ModelRecord> recordsList;
    MyDbHelper dbHelper;

    public AdapterRecord(Context context, ArrayList<ModelRecord> recordsList) {
        this.context = context;
        this.recordsList = recordsList;
    dbHelper = new MyDbHelper(context);
    }

    @NonNull
    @Override
    public HolderRecord onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_record,parent,false);
        return new HolderRecord(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecord holder, final int position) {
//get data
        ModelRecord model = recordsList.get(position);
        final String id = model.getId();
        final String name = model.getName();
        final String image = model.getImage();
        final String phone = model.getPhone();
        final String email = model.getEmail();
        final String dob = model.getDob();
        final String bio = model.getBio();
        final String addedTime = model.getAddedTime();
        final String updatedTime = model.getUpdatedTime();

        //set data
        holder.nameTv.setText(name);
        holder.phoneTv.setText(phone);
        holder.emailTv.setText(email);
        holder.dobTv.setText(dob);
        //if user does'not attach image then imageUri will be null.so set default image in that case
        if(image.equals("null")){
            //no image in record , set default
            holder.profileIv.setImageResource(R.drawable.ic_person);
        }else {
            //have image in record
            holder.profileIv.setImageURI(Uri.parse(image));

        }

        //handle item clickListners
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,RecordDetailActivity.class);
                intent.putExtra("RECORD_ID", id);
                context.startActivity(intent);

            }
        });

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show options menu
                showMoreDialog(""+position,
                        ""+id,
                        ""+name,
                        ""+phone,
                        ""+dob,
                        ""+email,
                        ""+image,
                        ""+bio,
                        ""+addedTime,
                        ""+updatedTime
                );
            }
        });

        Log.d("ImagePath", "onBindViewHolder: " +image);

    }

    private void showMoreDialog(String position, final String id, final String name, final String phone, final String dob, final String email, final String image, final String bio, final String addedTime, final String updateTime) {
        //options to display in dialog
        String[] options = {"Edit", "Delete"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //add item to dialog
       builder.setItems(options, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
          //handle item click
          if(which==0){
              //Edit is clicked
              Intent intent = new Intent(context,AddUpdateRecordActivity.class);
              intent.putExtra("ID",id);
              intent.putExtra("NAME",name);
              intent.putExtra("PHONE",phone);
              intent.putExtra("DOB",dob);
              intent.putExtra("EMAIL",email);
              intent.putExtra("IMAGE",image);
              intent.putExtra("BIO",bio);
              intent.putExtra("ADDED_TIME",addedTime);
              intent.putExtra("UPDATED_TIME",updateTime);
              intent.putExtra("isEditMode",true);
              context.startActivity(intent);
          } else if(which==1){
              //delete
              dbHelper.deleteData(id);
              //refresh record by calling activities OnResume Method
              ((MainActivity)context).onResume();
          }
           }
       });
       builder.create().show();
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    class HolderRecord extends RecyclerView.ViewHolder{

        CircularImageView profileIv;
        TextView nameTv,phoneTv,emailTv,dobTv;
        ImageButton moreBtn;

        public HolderRecord(@NonNull View itemView) {
            super(itemView);
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            dobTv = itemView.findViewById(R.id.dobTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }
}
