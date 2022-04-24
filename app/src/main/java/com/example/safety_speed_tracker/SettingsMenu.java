package com.example.safety_speed_tracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class SettingsMenu extends AppCompatActivity {

    // Declaring global values
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String IS_METRIC = "isMetric";
    private boolean isMetric;
    RadioButton mphButton;
    RadioButton kphButton;
    Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        // Filling attributes
        mphButton = findViewById(R.id.mphButton);
        kphButton = findViewById(R.id.kphButton);
        settingsToolbar = findViewById(R.id.settingsToolbar);

        // Initializing toolbar
        setSupportActionBar(settingsToolbar);

        // Creating shared prefs
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        isMetric = sharedPrefs.getBoolean(IS_METRIC, false);

        if(isMetric){
            kphButton.setChecked(true);
        }else{
            mphButton.setChecked(true);
        }

        // Is Metric Checkbox
        mphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Metric check
                if(mphButton.isChecked()){
                    isMetric = false;
                    editor.putBoolean(IS_METRIC, isMetric);
                }
                editor.commit();
            }
        });

        // Is Metric Checkbox
        kphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Metric check
                if(kphButton.isChecked()){
                    isMetric = true;
                    editor.putBoolean(IS_METRIC, isMetric);
                }
                editor.commit();
            }
        });

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
                startActivity(new Intent(this, ShowTopSpeeds.class));
                return true;

            case R.id.settingsButton:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}