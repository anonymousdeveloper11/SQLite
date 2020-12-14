package com.example.sqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private RecyclerView recordRv;
    private MyDbHelper dbHelper;
    private ActionBar actionBar;

    //sort options
    String orderByNewest = Constants.C_ADDED_TIMESTAMP + " DESC";
    String orderByOldest = Constants.C_ADDED_TIMESTAMP + " ASC";
    String orderByTitleAsc = Constants.C_NAME + " ASC";
    String orderByTitleDesc = Constants.C_NAME + "DESC";

    //for refreshing records refresh with last choosen sort option
    String currentOrderByStatus = orderByNewest;
    //for storage permission
    private static  final int STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;
    private String[] storagePermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.floatingActionBtn);
        recordRv = findViewById(R.id.recordRv);
        //actionBar
        actionBar = getSupportActionBar();
        actionBar.setTitle("All Records");
        //init dbHelper
        dbHelper = new MyDbHelper(this);
        //load records(by default newest record)
        loadRecords(orderByNewest);

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              Intent intent = new Intent(MainActivity.this,AddUpdateRecordActivity.class);
                intent.putExtra("isEditMode",false);//want add new data set false
                startActivity(intent);
            }
        });
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissionImport(){
        ActivityCompat.requestPermissions(this, storagePermission,STORAGE_REQUEST_CODE_IMPORT);

    }

    private void requestStoragePermissionExport(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE_EXPORT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case STORAGE_REQUEST_CODE_EXPORT:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted

                    exportCSV();
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();

                }
            }
            break;

            case STORAGE_REQUEST_CODE_IMPORT:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importCSV();
                } else {
                    Toast.makeText(this, "Storage Permission required...", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }



    private void exportCSV() {
        //path of csv file
        File folder = new File(Environment.getExternalStorageDirectory()+ "/" + "SQLiteBackup");//SQLite backup folder make
        boolean isFolderCreated = false;
        if(!folder.exists()){
            isFolderCreated = folder.mkdir();//created folder if not exists
        }
        Log.d("CSC_TAG", "exportCSV: "+isFolderCreated);
        //file name
        String csvFileName ="SQLite_Backup.csv";
        //complete path and name
        String filePathAndName = folder.toString() + "/" + csvFileName;
        //get records
        ArrayList<ModelRecord> recordList = new ArrayList<>();
        recordList.clear();
        recordList = dbHelper.getAllRecords(orderByOldest);
        try{
            //write csv file
            FileWriter fw = new FileWriter(filePathAndName);
            for(int i =0; i<recordList.size(); i++){
                fw.append(""+recordList.get(i).getId());//id
                fw.append(",");

                fw.append(""+recordList.get(i).getName());//name
                fw.append(",");

                fw.append(""+recordList.get(i).getImage());//id
                fw.append(",");

                fw.append(""+recordList.get(i).getBio());//id
                fw.append(",");

                fw.append(""+recordList.get(i).getPhone());//id
                fw.append(",");

                fw.append(""+recordList.get(i).getEmail());//id
                fw.append(",");

                fw.append(""+recordList.get(i).getDob());//id
                fw.append(",");

                fw.append(""+recordList.get(i).getAddedTime());//id
                fw.append(",");

                fw.append(""+recordList.get(i).getUpdatedTime());//id
                fw.append("\n");
            }
            fw.flush();
            fw.close();
            Toast.makeText(this, "Backup Exported to: "+filePathAndName, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void importCSV() {
        //use same path and fileName to import
        String fileNameAndPath = Environment.getExternalStorageDirectory()+"/SQLiteBackup/" + "SQLite_Backup.csv";
        File csvFile = new File(fileNameAndPath);
        //check if exists or not
        if(csvFile.exists()){
            //backup exists

            try{
                CSVReader csvReader = new CSVReader(new FileReader(csvFile.getAbsolutePath()));
                String[] nextLine;
                while ((nextLine = csvReader.readNext()) != null){
                    //use same order for import ass used for export eg. id is saved on 0 index
                    String ids = nextLine[0];
                    String name = nextLine[1];
                    String image = nextLine[2];
                    String bio = nextLine[3];
                    String phone = nextLine[4];
                    String email = nextLine[5];
                    String dob = nextLine[6];
                    String addedTime = nextLine[7];
                    String updatedTime = nextLine[8];

                    Long timestamp = System.currentTimeMillis();
                    long id = dbHelper.insertRecord(
                            ""+name,
                            ""+image,
                            ""+bio,
                            ""+dob,
                            ""+phone,
                            ""+email,
                            ""+addedTime,
                            ""+updatedTime);
                }
                Toast.makeText(this, "Backup Restored..", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            //backup doesn't exists
            Toast.makeText(this, "No Backup found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRecords(String orderBy) {
        currentOrderByStatus = orderBy;
        AdapterRecord adapterRecord = new AdapterRecord(MainActivity.this,
                dbHelper.getAllRecords(orderBy));
        recordRv.setAdapter(adapterRecord);
        //set  number of records
        actionBar.setSubtitle("Total: "+dbHelper.getRecordsCount());
    }
    private void searchRecords(String query){

        AdapterRecord adapterRecord = new AdapterRecord(MainActivity.this, dbHelper.searchRecords(query));
        recordRv.setAdapter(adapterRecord);
    }

    @Override
    protected void onResume() {
        loadRecords(currentOrderByStatus);//refresh record list
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        //searchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //search when button on keyboard clicked
                searchRecords(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //search as you type
                searchRecords(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id ==R.id.action_sort){
            //show sort options(show dialog)
            sortOptionDialog();
        } else if( id== R.id.action_delete_all){
            //delete all records
            dbHelper.deleteAllData();
            onResume();
        }
        else if(id == R.id.action_backup){
            //backup all record from csv file
            if(checkStoragePermission()){
                //permission allowed
                exportCSV();
            } else{
                //permission not allowed
                requestStoragePermissionExport();
            }
        } else if(id==R.id.action_restore){
            //restore all record from csv file
            if(checkStoragePermission()){
                //permission allowed
                importCSV();
                onResume();

            } else {
                requestStoragePermissionImport();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortOptionDialog() {
        //options to display dialog
        String[] options = {"Title Ascending", "Title Descending", "Newest", "Oldest"};
    //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort By")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle option click
                        if(which==0){
                            //title Ascending
                            loadRecords(orderByTitleAsc);
                        }else if (which == 1){

                            //title des
                            loadRecords(orderByTitleDesc);

                        }else if(which == 2){
                            //newest
                            loadRecords(orderByNewest);

                        }else  if (which == 3){
                            //oldest
                            loadRecords(orderByOldest);

                        }
                    }
                }).create().show();
    }
}