package com.example.safety_speed_tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safety_speed_tracker.SQLInterfacer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;

public class SpeedListAdapter extends RecyclerView.Adapter<SpeedListAdapter.MyViewHolder> {

    // Global Variables
    private Context context;
    private Activity activity;
    private ArrayList id, topSpeed, date;

    // Setting constructor
    SpeedListAdapter(Context context,
                     Activity activity,
                     ArrayList id,
                     ArrayList topSpeed,
                     ArrayList date){
        this.activity = activity;
        this.context = context;
        this.id = id;
        this.topSpeed = topSpeed;
        this.date = date;
    }

    // Setting up inflator
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_top_speeds, parent, false);
        return new MyViewHolder(view);
    }

    // Assigning text fields and buttons for each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.idText.setText(String.valueOf(id.get(position)));
        holder.topSpeedTxt.setText(String.valueOf(topSpeed.get(position)));
        holder.dateTxt.setText(String.valueOf(date.get(position)));

    }

    // Deleting item in list
    public void deleteItem(int position){

        // Alert dialogue to make sure the user wants to delete the SQL entry
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Cancelling deletion
        builder.setMessage("Are you sure you want to delete this entry?")

                // If the user selects yes
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    // Deleting the SQL entry
                    String delID = (String.valueOf(id.get(position))).trim();
                    SQLInterfacer dbi = new SQLInterfacer(context);
                    dbi.deleteRow(delID);
                    Intent menuIntent = new Intent(context, ShowTopSpeeds.class);
                    activity.startActivity(menuIntent);
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                    Intent menuIntent = new Intent(context, ShowTopSpeeds.class);
                    activity.startActivity(menuIntent);
                });

        // Showing alert
        AlertDialog alert = builder.create();
        alert.show();

    }

    // Returning how many rows in db
    @Override
    public int getItemCount() {
        return id.size();
    }

    // ViewHolder class
    public class MyViewHolder extends RecyclerView.ViewHolder {

        // Declaring buttons
        TextView idText, topSpeedTxt, dateTxt;

        // Setting up buttons
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            idText = itemView.findViewById(R.id.id);
            topSpeedTxt = itemView.findViewById(R.id.topSpeedTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
        }
    }
}
