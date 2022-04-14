package com.example.safety_speed_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class ShowTopSpeeds extends AppCompatActivity {

    // Global variables
    SQLInterfacer myDB = new SQLInterfacer(ShowTopSpeeds.this);
    ArrayList<String> id, topSpeed, date;
    SpeedListAdapter sLAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_top_speeds);

        // Initializing toolbar
        Toolbar tempToolBar = findViewById(R.id.showToolbar);
        setSupportActionBar(tempToolBar);

        // Initializing Recycler View
        RecyclerView recyclerView = findViewById(R.id.topSpeedRecyc);

        // Database access
        id = new ArrayList<>();
        topSpeed = new ArrayList<>();
        date = new ArrayList<>();

        // Saving data from db to arrays
        displayData();

        // Creating adapter for recycler view with arrays
        sLAdapter = new SpeedListAdapter(ShowTopSpeeds.this, ShowTopSpeeds.this, id, topSpeed, date);
        recyclerView.setAdapter(sLAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager((ShowTopSpeeds.this)));
    }

    // Inflating toolbar to get toolbar buttons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_buttons, menu);
        return true;
    }

    // Method for defining what the toolbar buttons do
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.homeButton:
                startActivity(new Intent(this, MainActivity.class));
                return true;

            case R.id.listButton:
                return true;

            case R.id.settingsButton:
                startActivity(new Intent(this, settingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Getting data and saving it to arrays
    void displayData(){

        // Getting cursor info
        Cursor cursor = myDB.readAllData();

        // Making sure the db isnt empty
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data.", Toast.LENGTH_SHORT).show();

            // Adding values to arrays
        }else{
            while(cursor.moveToNext()){
                id.add(cursor.getString(0));
                topSpeed.add(cursor.getString(1));
                date.add(cursor.getString(2));

            }
        }
    }
}