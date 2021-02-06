package com.example.tripreminder2021.adapters;


import android.app.AlertDialog;
import android.content.Context;
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
import com.example.tripreminder2021.zService.FloatingWindowService;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.ArrayList;

public class UpcomingRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingRecyclerViewAdapter.ViewHolder>{

int increasedID=0;
    private ArrayList<TripModel> list;
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


                        addNoteScenario(currentTrip);
                       // firebaseDatabaseServices.updateNotes(currentTrip.getTrip_id(),currentTrip.getNotes());
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

    private void addNoteScenario(TripModel currentTrip) {

        AlertDialog alert;

        final LayoutInflater inflater = LayoutInflater.from(context);
        final View inflate_view = inflater.inflate(R.layout.add_notes_sayout_sample1, null);

       ArrayList<TextInputLayout> mNotesTextInputLayout = new ArrayList<>();
        final LinearLayout currentParent =inflate_view.findViewById(R.id.notes_linearLayout1);

        final View linearLayout = inflater.inflate(R.layout.add_notes_sayout_sample, null);

        final TextInputLayout noteTextInput = linearLayout.findViewById(R.id.note_text_field_input1);
        mNotesTextInputLayout.add(noteTextInput);

        ImageButton subImgBtn = linearLayout.findViewById(R.id.sub_note_img_btn);
        ImageButton subImgBtn1 = inflate_view.findViewById(R.id.sub_note_img_btn1);

        subImgBtn.setOnClickListener(v -> {
            currentParent.removeView(linearLayout);
            mNotesTextInputLayout.remove(noteTextInput);
        });

        subImgBtn1.setOnClickListener(v -> {
            inflater.inflate(R.layout.add_notes_sayout_sample, null);

        });







        currentParent.addView(linearLayout);
        increasedID++;



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(inflate_view);
        builder.setCancelable(false);

        builder.setPositiveButton((R.string.cancel), (dialog, id) -> saveNotes(currentTrip));
        alert = builder.create();
        alert.show();

    }

    private void saveNotes(TripModel trip)
    {
        Log.i("TAG", "saveNotes: "+trip.getNotes().size());
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

    private void generateNoteLayout(View view) {


    }


}
