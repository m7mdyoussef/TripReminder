package com.example.tripreminder2021.zService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.ui.activities.AddBtnActivity;

public class AlarmEventReciever extends BroadcastReceiver {
    public static final String RECEIVED_TRIP = "RECEIVED_TRIP";
    public static final String RECEIVED_TRIP_SEND_SERIAL = "RECEIVED_TRIP_SEND_SERIAL";

    private Context context;


    //recieve from other applications
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle b = intent.getBundleExtra(AddBtnActivity.NEW_TRIP_OBJECT);
        TripModel tm = (TripModel) b.getParcelable(AddBtnActivity.NEW_TRIP_OBJ_SERIAL);

        if (tm != null) {
            displayAlert(tm);
            Log.d("OnReceive", "Trip name" + tm.getTripname());
        }

    }

    private void displayAlert(TripModel tm) {

        //joe
        Intent i = new Intent(context, MyDialogActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(RECEIVED_TRIP_SEND_SERIAL, tm);

        i.putExtra(RECEIVED_TRIP, b);
        i.setClass(context, MyDialogActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
