package com.example.safety_speed_tracker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class Swiper extends ItemTouchHelper.SimpleCallback {
    SpeedListAdapter sla;

    Swiper(SpeedListAdapter spa) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.sla = spa;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        this.sla.deleteItem(position);
    }
}
