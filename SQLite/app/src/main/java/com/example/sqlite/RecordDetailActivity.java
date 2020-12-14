package com.example.sqlite;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

import java.util.Calendar;
import java.util.Locale;

public class RecordDetailActivity extends AppCompatActivity {
    private CircularImageView profileIv;
    private TextView bioTv,nameTv,emailTv,phoneTv,dobTv,addedTimeTv,updatedTimeTv;

    private ActionBar actionBar;
    private String recordID;

    private MyDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        profileIv = findViewById(R.id.profileIv);
        bioTv = findViewById(R.id.bioTv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        dobTv = findViewById(R.id.dobTv);
        addedTimeTv = findViewById(R.id.addedTimeTv);
        updatedTimeTv = findViewById(R.id.updateTimeTv);

        actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        dbHelper = new MyDbHelper(this);

        //get record id from adapter through intent
        Intent intent = getIntent();
        recordID = intent.getStringExtra("RECORD_ID");
        showRecordDetails();
    }

    private void showRecordDetails() {
        //get record details
        //query to select record based on recordId
        String selectQuery = " SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.C_ID +" =\"" +recordID +"\"";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        //keep checking in whole db for that record
        if(cursor.moveToFirst()){
            do {
                //get data
                String id = ""+ cursor.getInt(cursor.getColumnIndex(Constants.C_ID));
                String name = ""+ cursor.getString(cursor.getColumnIndex(Constants.C_NAME));
                String image = ""+ cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE));
                String bio = ""+ cursor.getString(cursor.getColumnIndex(Constants.C_BIO));
                String email = ""+ cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL));
                String phone = ""+ cursor.getString(cursor.getColumnIndex(Constants.C_PHONE));
                String dob = ""+ cursor.getString(cursor.getColumnIndex(Constants.C_DOB));
                String timestampAdded = ""+ cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP));
                String timestampUpdated = ""+ cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP));

                //convert time
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTimeInMillis(Long.parseLong(timestampAdded));
                String timeAdded = ""+ DateFormat.format("dd/MM/yyy hh:mm:aa",calendar);

                Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
                calendar1.setTimeInMillis(Long.parseLong(timestampUpdated));
                String timeUpdated = ""+ DateFormat.format("dd/MM/yyy hh:mm:aa",calendar1);

                //set data
                nameTv.setText(name);
                bioTv.setText(bio);
                phoneTv.setText(phone);
                emailTv.setText(email);
                dobTv.setText(dob);
                addedTimeTv.setText(timeAdded);
                updatedTimeTv.setText(timeUpdated);
                if(image.equals("null")){
                    //no image in record , set default
                   profileIv.setImageResource(R.drawable.ic_person);
                }else {
                    //have image in record
                    profileIv.setImageURI(Uri.parse(image));

                }


            }while (cursor.moveToNext());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}