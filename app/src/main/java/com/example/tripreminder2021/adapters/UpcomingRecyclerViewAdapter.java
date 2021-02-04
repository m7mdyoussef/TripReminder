package com.example.tripreminder2021.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.net.Uri;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tripreminder2021.R;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.pojo.TripStatus;
import com.example.tripreminder2021.repository.FirebaseDatabaseServices;
import com.example.tripreminder2021.ui.activities.AddBtnActivity;
import com.example.tripreminder2021.zService.FloatingWindowService;
import com.example.tripreminder2021.zService.MyDialogActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class UpcomingRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingRecyclerViewAdapter.ViewHolder>{

    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private ArrayList<TripModel> list;

    //private TripModel currentTrip;
    private Context context;
    FirebaseDatabaseServices firebaseDatabaseServices;

    public UpcomingRecyclerViewAdapter(ArrayList<TripModel> list,Context context)
    {
        this.context=context;
        this.list=list;
        firebaseDatabaseServices=new FirebaseDatabaseServices();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_card_row,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TripModel currentTrip = list.get(position);

        holder.tripName.setText(currentTrip.getTripname());
        holder.startLoc.setText(currentTrip.getStartloc());
        holder.endLoc.setText(currentTrip.getEndloc());
        holder.time.setText(currentTrip.getTime());
        holder.date.setText(currentTrip.getDate());
        holder.startNow.setOnClickListener(v -> {

            firebaseDatabaseServices.addTripToHistory(currentTrip.getTrip_id());
            firebaseDatabaseServices.changeTripStatus(currentTrip.getTrip_id(),TripStatus.Done);



            EventBus.getDefault().postSticky(currentTrip);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Intent intent =new Intent(context, FloatingWindowService.class);
                context.startService(intent);

            } else if (Settings.canDrawOverlays(context)) {
                Intent intent =new Intent(context, FloatingWindowService.class);
               context.startService(intent);

            }

            Uri gmIntentUri = Uri.parse("google.navigation:q=" + currentTrip.getEndloc());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        });

        holder.popMenu.setOnClickListener(view -> showPopupMenu(holder.popMenu,currentTrip));

    }





    private void showPopupMenu(View view,TripModel currentTrip) {
        // inflate menu
        androidx.appcompat.widget.PopupMenu popup = new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.upcoming_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_upcoming_show_notes:
                        //currentTrip.getNotes();
                        return true;

                    case R.id.action_upcoming_add_notes:

                        firebaseDatabaseServices.updateNotes(currentTrip.getTrip_id(),currentTrip.getNotes());
                        return true;

                    case R.id.action_upcoming_edit_trip:

                        editTrip(currentTrip);
                        return true;

                    case R.id.action_upcoming_cancel_trip:

                        firebaseDatabaseServices.changeTripStatus(currentTrip.getTrip_id(), TripStatus.Canceled);
                        return true;

                    case R.id.action_upcoming_delete_trip:

                        showDeleteAlertDialog(currentTrip.getTrip_id());
                        return true;

                    default:
                }
                return false;
            }
        });
        popup.show();
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tripName;
        private Button popMenu;
        private TextView startLoc;
        private TextView endLoc;
        private TextView time;
        private TextView date;
        private Button startNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tripName=itemView.findViewById(R.id.trip_name_id);
            popMenu=itemView.findViewById(R.id.pop_menu_id);
            startLoc=itemView.findViewById(R.id.start_loc_id);
            endLoc=itemView.findViewById(R.id.end_loc_id);
            time=itemView.findViewById(R.id.Time_id);
            date=itemView.findViewById(R.id.Date_id);
            startNow=itemView.findViewById(R.id.startnow);
        }
    }
    private void editTrip(TripModel tripModel)
    {
        Intent intent = new Intent(context, AddBtnActivity.class);
        Bundle Bundle = new Bundle();
        Bundle.putParcelable("LAST_VALUES", tripModel);
        intent.putExtras(Bundle);
    }

    private void showDeleteAlertDialog(String trip_id)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Sure you want to delete the trip");
        alertDialogBuilder.setPositiveButton("Yes",
                (arg0, arg1) -> firebaseDatabaseServices.deleteTrip(trip_id));

        alertDialogBuilder.setNegativeButton("Cancel",
                (arg0, arg1) -> {
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void setData(ArrayList<TripModel> list)
    {
        this.list=list;
        notifyDataSetChanged();
    }

}
