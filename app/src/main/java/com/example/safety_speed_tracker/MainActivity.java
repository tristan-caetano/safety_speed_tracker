package com.example.safety_speed_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    // Global variables
    TextView maxSpeedText;
    TextView currSpeedText;
    Button recordButton;
    float currMaxSpeed = 0;
    boolean doRecord = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maxSpeedText = findViewById(R.id.maxSpeedText);
        currSpeedText = findViewById(R.id.currSpeedText);
        recordButton = findViewById(R.id.recordSpeed);

        // Check that user granted permission for GPS use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            doStuff();
        }
        this.updateSpeed(null);
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

        if(location != null){
            location.setUseMetricUnits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        if(currMaxSpeed < nCurrentSpeed){currMaxSpeed = nCurrentSpeed;}

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();

        Formatter fmt2 = new Formatter(new StringBuilder());
        fmt2.format(Locale.US, "%5.1f", currMaxSpeed);
        String strCurrMaxSpeed = fmt2.toString();

        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");
        strCurrMaxSpeed = strCurrMaxSpeed.replace(" ", "0");

        if(this.useMetricUnits()){
            currSpeedText.setText(strCurrentSpeed + "km/h");
            maxSpeedText.setText(strCurrMaxSpeed + "km/h");
        } else{
            currSpeedText.setText(strCurrentSpeed + "m/h");
            maxSpeedText.setText(strCurrMaxSpeed + "m/h");
        }
    }

    private boolean useMetricUnits(){
        // TODO: get metric bool from shared prefs
        return false;
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
}