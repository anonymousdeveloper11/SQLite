package com.example.sqlite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class AddUpdateRecordActivity extends AppCompatActivity {
    private CircularImageView imageIv;
    private EditText nameEt,phoneEt,emailEt,dobEt,bioEt;
    private FloatingActionButton addBtn;

    //actionBar
    private ActionBar actionBar;
    //permission Constants
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=101;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE=102;
    private static final int IMAGE_PICK_GALLERY_CODE=103;
    //array of permissions
    private String[] cameraPermissions;//camera and storage
    private String[] storagePermission;//only storage

    private Uri imageUri;

    private String id,name,phone,email,dob,bio,addedTime,updatedTime;
    private boolean isEditMode = false;
    //dbHelper
    private MyDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_record);

        imageIv = findViewById(R.id.imageIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        emailEt = findViewById(R.id.emailEt);
        dobEt = findViewById(R.id.dobEt);
        bioEt = findViewById(R.id.bioEt);
        addBtn = findViewById(R.id.addBtn);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //init  permissions of array
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //get data from intent
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);


        if(isEditMode){
            //updating data
            actionBar.setTitle("Update Data");
            id = intent.getStringExtra("ID");
            name =intent.getStringExtra("NAME");
            phone =intent.getStringExtra("PHONE");
            dob =intent.getStringExtra("DOB");
            email =intent.getStringExtra("EMAIL");
            bio =intent.getStringExtra("BIO");
            imageUri =Uri.parse(intent.getStringExtra("IMAGE"));
            addedTime =intent.getStringExtra("ADDED_TIME");
            updatedTime =intent.getStringExtra("UPDATED_TIME");

            //set data to views
            nameEt.setText(name);
            phoneEt.setText(phone);
            emailEt.setText(email);
            dobEt.setText(dob);
            bioEt.setText(bio);
            if(imageUri.toString().equals("null")){
                imageIv.setImageResource(R.drawable.ic_person);
            }else {
                imageIv.setImageURI(imageUri);
            }
        }else {
            //add new data
            actionBar.setTitle("Add Record");
        }
        //init db helper
        dbHelper = new MyDbHelper(this);
        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
                imagePickDialog();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }

    private void inputData() {
        //get data
        name = ""+nameEt.getText().toString().trim();
        phone = ""+phoneEt.getText().toString().trim();
        email = ""+emailEt.getText().toString().trim();
        dob = ""+dobEt.getText().toString().trim();
        bio = ""+bioEt.getText().toString().trim();

        String timestamp = ""+System.currentTimeMillis();
        if(isEditMode){
            //update
            dbHelper.updateRecord(""+id,
                    ""+name,
                    ""+imageUri,
                    ""+bio,
                    ""+dob,
                    ""+phone,
                    ""+email,
                    ""+addedTime,
                    ""+timestamp );

            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        }else{
            //save to db new data
            long id = dbHelper.insertRecord(
                    ""+name,
                    ""+imageUri,
                    ""+bio,
                    ""+dob,
                    ""+phone,
                    ""+email,
                    ""+timestamp,
                    ""+timestamp);
            Toast.makeText(this, "Record Added against ID: "+id, Toast.LENGTH_SHORT).show();
        }


    }

    private void imagePickDialog() {
        //options to display dialogs
        String[] options ={"Camera","Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //title
        builder.setTitle("Pick Image From");
        //set item options
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           //handle click
           if(which==0){
               //camera clicked
               if(!checkCameraPermission()){
                   requestCameraPermission();
               }else {
                   //permission already granted
                   pickFromCamera();
           }
           }
           else if(which ==1){
               if(!checkStoragePermission()){
                   requestStoragePermission();
               }else {
                   pickFromGallery();
               }
           }
            }

        });
        //show dialog
        builder.create().show();

    }

    private void pickFromGallery() {
        //intent to pick From Gallery,the image will be  returned in onActivityResult method
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");//we want only images
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Image_Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image_description");
        //put image
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to open camera for image
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private boolean checkStoragePermission(){
        //check storage permission is enable or not
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //request storage permission
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //results of permission
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //if allowed return true otherwise false
                if(grantResults.length>0){
                    boolean cameraAccepted =grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){
                        //both permission allowed
                        pickFromCamera();

                    } else {
                        Toast.makeText(this, "Camera and Storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        //permission allowed
                        pickFromGallery();
                    }else {
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //image pick from camera or gallery will be recived here
        if(requestCode==RESULT_OK){
            //image is picked
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //pick from gallery
                //crop image
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //pick from camera
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);


            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                //crop image recived
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(requestCode==RESULT_OK){
                    Uri resultUri = result.getUri();
                    imageUri = resultUri;
                    //set image
                    imageIv.setImageURI(resultUri);
                    copyFileOrDirectory(""+imageUri.getPath(), ""+getDir("SQLiteRecordImages",MODE_PRIVATE));
                }
                else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    //error
                    Exception error = result.getError();
                    Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void copyFileOrDirectory(String srcDir, String desDir){
        //create fikdir in specified directory
        try{
            File src = new File(srcDir);
            File des = new File(desDir, src.getName());
            if(src.isDirectory()){
                String[] files = src.list();
                int fileLength = files.length;
                for(String file : files){
                    String src1 = new File(src, file).getPath();
                    String dst1 = des.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            }
            else {
                copyFile(src, des);
            }
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFile(File srcDir, File desDir) throws IOException {

        if(!desDir.getParentFile().exists()){
            desDir.mkdirs();//create if not exists
        }
        if(!desDir.exists()){
            desDir.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try{

        source = new FileInputStream(srcDir).getChannel();
        destination = new FileOutputStream(desDir).getChannel();
        destination.transferFrom(source,0,source.size());
        imageUri = Uri.parse(desDir.getPath());//uri of saved image
            Log.d("ImagePath","copyFile: " +imageUri);
    }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            {
                //close resources
                if(source!=null){
                    source.close();
                }
                if (destination!=null){
                    destination.close();
                }
            }
        }
        }
}