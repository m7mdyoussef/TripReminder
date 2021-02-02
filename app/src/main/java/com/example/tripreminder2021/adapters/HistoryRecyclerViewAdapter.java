package com.example.tripreminder2021.adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tripreminder2021.R;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.repository.FirebaseDatabaseServices;

import java.util.ArrayList;
import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder>{

    private ArrayList<TripModel> list;
    private TripModel current;
    private Context context;
    FirebaseDatabaseServices firebaseDatabaseServices;
    public HistoryRecyclerViewAdapter(Context context,ArrayList<TripModel> list)
    {
        this.context=context;
        this.list=list;
        firebaseDatabaseServices=new FirebaseDatabaseServices();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerViewAdapter.ViewHolder holder, int position) {
        current = list.get(position);

        holder.tripName.setText(current.getTripname());
        holder.startLoc.setText(current.getStartloc());
        holder.endLoc.setText(current.getEndloc());
        holder.time.setText(current.getTime());
        holder.date.setText(current.getDate());
        holder.status.setText(current.getStatus());

        holder.popMenu.setOnClickListener(view -> showPopupMenu(holder.popMenu));
    }
    private void showPopupMenu(View view) {
        // inflate menu
        androidx.appcompat.widget.PopupMenu popup = new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.history_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_history_view_notes:
                    //current.getNotes();
                    Log.i("TAG", "onMenuItemClick: " + current.getTripname());
                    return true;
                case R.id.action_history_delete_note:
                    showDeleteAlertDialog(current.getTrip_id());
                    Log.i("TAG", "onMenuItemClick: " + current.getDate());
                    return true;
                default:
            }
            return false;
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
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
    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tripName;
        private Button popMenu;
        private TextView startLoc;
        private TextView endLoc;
        private TextView time;
        private TextView date;
        private TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tripName=itemView.findViewById(R.id.trip_name_id);
            popMenu=itemView.findViewById(R.id.pop_menu_id);
            startLoc=itemView.findViewById(R.id.start_loc_id);
            endLoc=itemView.findViewById(R.id.end_loc_id);
            time=itemView.findViewById(R.id.Time_id);
            date=itemView.findViewById(R.id.Date_id);
            status=itemView.findViewById(R.id.status);
        }
    }
    public void setData(ArrayList<TripModel> list)
    {
        this.list=list;
        notifyDataSetChanged();
    }
}
