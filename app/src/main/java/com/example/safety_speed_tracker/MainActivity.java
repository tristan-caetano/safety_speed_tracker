package com.example.safety_speed_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LocationListener {

    // Global variables
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String IS_METRIC = "isMetric";
    TextView maxSpeedText;
    TextView currSpeedText;
    Button recordButton;
    Button playButton;
    Button pauseButton;
    Button stopButton;
    float currMaxSpeed = 0;
    boolean doRecord = false;
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maxSpeedText = findViewById(R.id.maxSpeedText);
        currSpeedText = findViewById(R.id.currSpeedText);
        recordButton = findViewById(R.id.recordSpeed);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        Toolbar mainToolbar = findViewById(R.id.mainToolbar);

        // Initializing toolbar
        setSupportActionBar(mainToolbar);

        // Check that user granted permission for GPS use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            doStuff();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Setting up notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My Notification")
                .setSmallIcon(R.drawable.car)
                .setContentTitle("Safety Speed Tracker")
                .setContentText("Your speed is currently being tracked!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Open in background."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);

        // Record button
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If its already recording
                if(doRecord){

                    managerCompat.cancelAll();

                    // Tell user their top speed was recorded
                    Toast.makeText(MainActivity.this, "Recording top speed.", Toast.LENGTH_SHORT).show();

                    // Set boolean, get date, and format top speed
                    doRecord = false;
                    DecimalFormat df = new DecimalFormat("##.##");
                    df.setRoundingMode(RoundingMode.DOWN);
                    String tempMax = df.format(currMaxSpeed);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String currDate = sdf.format(new Date());
                    SQLInterfacer sql = new SQLInterfacer(MainActivity.this);

                    // Reset current max speed for next record
                    currMaxSpeed = 0;

                    // Changing sent string based on metric boolean
                    if (MainActivity.this.useMetricUnits()) {
                        currSpeedText.setText("000.0" + "km/h");
                        maxSpeedText.setText("000.0" + "km/h");
                        tempMax += " km/h";
                    } else {
                        currSpeedText.setText("000.0" + "m/h");
                        maxSpeedText.setText("000.0" + "m/h");
                        tempMax += " m/h";
                    }

                    // Sending date and max speed to SQL
                    sql.addTopSpeed(tempMax, currDate);

                // If its not already recording
                }else{

                    managerCompat.notify(1, builder.build());

                    // Letting the user know their speed is being recorded
                    Toast.makeText(MainActivity.this, "Starting speed recording.", Toast.LENGTH_SHORT).show();
                    doRecord = true;
                }
            }
        });

        // If true, record speed
        if(doRecord) {
            this.updateSpeed(null);
        }
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
                return true;

            case R.id.listButton:
                startActivity(new Intent(this, ShowTopSpeeds.class));
                return true;

            case R.id.settingsButton:
                startActivity(new Intent(this, SettingsMenu.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location != null){
            CurrLocation myLocation = new CurrLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection.", Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(CurrLocation location){
        float nCurrentSpeed = 0;
        if(doRecord) {
            if (location != null) {
                location.setUseMetricUnits(this.useMetricUnits());
                nCurrentSpeed = location.getSpeed();
            }

            if (currMaxSpeed < nCurrentSpeed) {
                currMaxSpeed = nCurrentSpeed;
            }

            Formatter fmt = new Formatter(new StringBuilder());
            fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
            String strCurrentSpeed = fmt.toString();

            Formatter fmt2 = new Formatter(new StringBuilder());
            fmt2.format(Locale.US, "%5.1f", currMaxSpeed);
            String strCurrMaxSpeed = fmt2.toString();

            strCurrentSpeed = strCurrentSpeed.replace(" ", "0");
            strCurrMaxSpeed = strCurrMaxSpeed.replace(" ", "0");

            if (this.useMetricUnits()) {
                currSpeedText.setText(strCurrentSpeed + "km/h");
                maxSpeedText.setText(strCurrMaxSpeed + "km/h");
            } else {
                currSpeedText.setText(strCurrentSpeed + "m/h");
                maxSpeedText.setText(strCurrMaxSpeed + "m/h");
            }
        }
    }

    private boolean useMetricUnits(){
        // Creating shared prefs
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        boolean isMetric = sharedPrefs.getBoolean(IS_METRIC, false);
        return isMetric;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                finish();
            }
        }
    }

    public void play(View v) {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.song);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void pause(View v) {
        if (player != null) {
            player.pause();
        }
    }

    public void stop(View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }



}