package com.example.safety_speed_tracker;

import android.location.Location;
import android.location.LocationListener;

public class CurrLocation extends Location {
    private boolean bUseMetricUnits = false;
    private final float meterToFeetF = 3.28083989501312f;
    private final double meterToFeetD = 3.28083989501312d;
    private final float mPsTomPh = 2.23693629f;

    public CurrLocation(Location location){
        this(location, true);
    }

    public CurrLocation(Location location, boolean bUseMetricUnits){
        super(location);
        this.bUseMetricUnits = bUseMetricUnits;
    }

    public boolean getUseMetricUnits(){
        return this.bUseMetricUnits;
    }
    public void setUseMetricUnits(boolean bUseMetricUnits){
        this.bUseMetricUnits = bUseMetricUnits;
    }

    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);

        if(!this.getUseMetricUnits()){
            // Converting meters to feet
            nDistance = nDistance * meterToFeetF;
        }

        return nDistance;
    }

    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();

        if(!this.getUseMetricUnits()){
            // Converting meters to feet
            nAltitude = nAltitude * meterToFeetD;
        }

        return nAltitude;
    }

    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed() * 3.6f;

        if(!this.getUseMetricUnits()){
            // Converting meters/second to miles per hour
            nSpeed = super.getSpeed() * mPsTomPh;
        }

        return nSpeed;
    }

    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();

        if(!this.getUseMetricUnits()){
            // Converting meters to feet
            nAccuracy = nAccuracy * meterToFeetF;
        }

        return nAccuracy;
    }
}
