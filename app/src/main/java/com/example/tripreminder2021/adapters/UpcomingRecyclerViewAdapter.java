package com.example.tripreminder2021.adapters;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.net.Uri;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tripreminder2021.R;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.pojo.TripStatus;
import com.example.tripreminder2021.repository.FirebaseDatabaseServices;
import com.example.tripreminder2021.ui.activities.AddBtnActivity;
import com.example.tripreminder2021.zService.AlarmEventReciever;
import com.example.tripreminder2021.ui.activities.ShowNotesActivity;
import com.example.tripreminder2021.zService.FloatingWindowService;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.List;

public class UpcomingRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingRecyclerViewAdapter.ViewHolder>{

    private ArrayList<TripModel> list;
    private Context context;
    private FirebaseDatabaseServices firebaseDatabaseServices;

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
        holder.date.setText(String.valueOf(currentTrip.getDate()));
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
        androidx.appcompat.widget.PopupMenu popup = new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.upcoming_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_upcoming_show_notes:

                        PopupMenu popup = new PopupMenu(context,view);
                        for (int i = 0; i < currentTrip.getNotes().size(); i++) {
                            popup.getMenu().add(Menu.NONE, 1, Menu.NONE, currentTrip.getNotes().get(i));
                        }
                        popup.show();
                        return true;

                    case R.id.action_upcoming_add_notes:
                        Intent intent = new Intent(context, ShowNotesActivity.class);
                        Bundle Bundle = new Bundle();
                        Bundle.putSerializable("CURRENT_NOTES",(Serializable) currentTrip);
                        intent.putExtra("ADD_BUNDLE",Bundle);
                        context.startActivity(intent);
                        return true;

                    case R.id.action_upcoming_edit_trip:

                        AlarmManager alarmManager3 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent myIntent3 = new Intent(context, AlarmEventReciever.class);
                        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(
                                context, currentTrip.getRandomNumber(), myIntent3, PendingIntent.FLAG_UPDATE_CURRENT);
                        if (alarmManager3 != null) {
                            alarmManager3.cancel(pendingIntent3);
                        }
                        pendingIntent3.cancel();

                        editTrip(currentTrip);
                        return true;

                    case R.id.action_upcoming_cancel_trip:



                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent myIntent = new Intent(context, AlarmEventReciever.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                context, currentTrip.getRandomNumber(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        if (alarmManager != null) {
                            alarmManager.cancel(pendingIntent);
                        }
                        pendingIntent.cancel();


                        firebaseDatabaseServices.changeTripStatus(currentTrip.getTrip_id(), TripStatus.Canceled);
                        return true;

                    case R.id.action_upcoming_delete_trip:

                        AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent myIntent2 = new Intent(context, AlarmEventReciever.class);
                        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(
                                context, currentTrip.getRandomNumber(), myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
                        if (alarmManager2 != null) {
                            alarmManager2.cancel(pendingIntent2);
                        }
                        pendingIntent2.cancel();

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
        Bundle.putSerializable("TRIP",(Serializable) tripModel);
        intent.putExtra("EDIT_BUNDLE",Bundle);
        context.startActivity(intent);
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
