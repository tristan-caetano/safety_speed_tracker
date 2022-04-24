package com.example.safety_speed_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SQLInterfacer extends SQLiteOpenHelper {


    // Global variables
    private Context context;

    // SQL string constants
    private static final String DATABASE_NAME = "TopSpeedTracker.db";
    private static final byte DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "speed";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SPEED = "top_speed";
    private static final String COLUMN_DATE = "date";

    // Constructor
    public SQLInterfacer(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SPEED + " TEXT, " +
                COLUMN_DATE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Sending values to database to be saved
    void addTopSpeed(String speed, String date){

        // Allows us to write to db
        SQLiteDatabase db = this.getWritableDatabase();

        // Stores values until they are ready to be sent to db
        ContentValues cv = new ContentValues();

        // Saving each value to their respective column
        cv.put(COLUMN_SPEED, speed);
        cv.put(COLUMN_DATE, date);

        // Storing row into db
        long result = db.insert(TABLE_NAME, null, cv);

        // Notifying the user if it was successful or not
        if(result == -1){
            Toast.makeText(context, "Failed to record top speed.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Top speed successfully recorded!", Toast.LENGTH_SHORT).show();
        }
    }

    // Deleting specific row
    void deleteRow(String row_id){

        // Accessing db
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});

        // Notifying the user if it was successful or not
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    // Getting all data from db to be displayed in app
    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
