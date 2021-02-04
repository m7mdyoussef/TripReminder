package com.example.tripreminder2021.zService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


import com.example.tripreminder2021.R;
import com.example.tripreminder2021.pojo.TripModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.tripreminder2021.zService.AlarmEventReciever.RECEIVED_TRIP;
import static com.example.tripreminder2021.zService.AlarmEventReciever.RECEIVED_TRIP_SEND_SERIAL;

public class DialognotificationService extends Service {
    private static final String CHANNEL_ID = "MyDialogService";
    private static final String TAG = "TAG";
    private TripModel tm;
    private final IBinder localBinder = new MyBinder();

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onResult(TripModel result){
       tm=result;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        createNotificationChannel();
        createNotification();

//        Bundle b =intent.getBundleExtra(RECEIVED_TRIP);
//        tm= (TripModel) b.getParcelable(RECEIVED_TRIP_SEND_SERIAL);
        if (tm != null) {
            Log.i("MyService: ", "Object Received " + tm.getTripname());
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

//        Bundle b = intent.getBundleExtra(RECEIVED_TRIP);
//        tm = (TripModel) b.getSerializable(RECEIVED_TRIP_SEND_SERIAL);
        return localBinder;  // ref from inner class MyBinder
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(
                    CHANNEL_ID,
                    "Trip Reminder",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager man = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);

            man.createNotificationChannel(nc);
        }

    }

    private void createNotification() {
        Intent i=new Intent(this, MyDialogActivity.class);
        Bundle b=new Bundle();
        b.putParcelable(RECEIVED_TRIP_SEND_SERIAL, tm);
        i.putExtra(RECEIVED_TRIP, b);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
               i , PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(TAG, "createNotification: tm--> "+(tm!=null));
        // GET TRIP ! to show trip name
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Trip Reminder")
                .setContentText("You have an upcoming trip").setTicker("Notification!")
                .setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_date_range_24px)
                .build();

        startForeground(12354, notification);

    }


    public class MyBinder extends Binder {
        public DialognotificationService getService() {  // ref ml service
            return DialognotificationService.this;
        }

    }

}
