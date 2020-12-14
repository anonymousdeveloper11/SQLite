package com.example.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDbHelper extends SQLiteOpenHelper {
    //now create database helper class that contains all crud method
    public MyDbHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table on that db
        db.execSQL(Constants.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //upgrade database(if there is any structure change db Version)
        //drop older db if exists
        db.execSQL("DROP TABLE IF EXISTS "+ Constants.TABLE_NAME);
        //create table again
        onCreate(db);
    }
    //inset record to db
    public long insertRecord(String name, String image, String bio, String dob, String phone, String email,
                             String addedTime, String updatedTime){
        //get writable database because we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //id will be inserted automatically as we set AUTOINCREMENT in query
        //inset data
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_EMAIL, email);
        values.put(Constants.C_IMAGE, image);
        values.put(Constants.C_PHONE, phone);
        values.put(Constants.C_BIO, bio);
        values.put(Constants.C_DOB, dob);
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime);
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime);

        //inset row ,it will return record id of saved data
        long id = db.insert(Constants.TABLE_NAME, null, values);
        //close db connection
        db.close();
        //return id of inserted record
        return id;
    }
//update existing record to db
    public void updateRecord(String id,String name, String image, String bio, String dob, String phone, String email,
                             String addedTime, String updatedTime){
        //get writable database because we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //id will be inserted automatically as we set AUTOINCREMENT in query
        //inset data
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_EMAIL, email);
        values.put(Constants.C_IMAGE, image);
        values.put(Constants.C_PHONE, phone);
        values.put(Constants.C_BIO, bio);
        values.put(Constants.C_DOB, dob);
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime);
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime);

        //inset row ,it will return record id of saved data
         db.update(Constants.TABLE_NAME, values, Constants.C_ID +" = ?", new String[]{id});
        //close db connection
        db.close();
        //return id of inserted record

    }

    //get all data
    public ArrayList<ModelRecord> getAllRecords(String orderBy){
        //orderBy query will allow to start data eg, newest/oldest first, name ascending/descending order
        //it will return list or records since we have used return type ArrayList<ModelRecord>

        ArrayList<ModelRecord> records = new ArrayList<>();
        //query to select records
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY " + orderBy;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        //looping through all records and add to list
        if(cursor.moveToFirst()){
            do{
                ModelRecord modelRecord = new ModelRecord(
                        ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_BIO)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_DOB)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_PHONE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))

                );
                //add records to list
                records.add(modelRecord);
            }while (cursor.moveToNext());
        }
        //close db connection
        db.close();
        //return the list
        return records;
    }
//search data
    public ArrayList<ModelRecord> searchRecords(String query){
        //orderBy query will allow to start data eg, newest/oldest first, name ascending/descending order
        //it will return list or records since we have used return type ArrayList<ModelRecord>

        ArrayList<ModelRecord> records = new ArrayList<>();
        //query to select records
        String selectQuery = " SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.C_NAME + " LIKE '%" + query +"%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        //looping through all records and add to list
        if(cursor.moveToFirst()){
            do{
                ModelRecord modelRecord = new ModelRecord(
                        ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_BIO)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_DOB)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_PHONE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))

                );
                //add records to list
                records.add(modelRecord);
            }while (cursor.moveToNext());
        }
        //close db connection
        db.close();
        //return the list
        return records;
    }

    //get number of records
    public int getRecordsCount(){
        String countQuery = " SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //delete records using id

    public void deleteData(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.C_ID + " = ?", new String[] {id});
        db.close();
    }

    //delete all data from Table
    public  void deleteAllData(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(" DELETE FROM " + Constants.TABLE_NAME);
        db.close();
    }

}
