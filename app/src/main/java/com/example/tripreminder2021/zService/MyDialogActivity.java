package com.example.tripreminder2021.zService;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.tripreminder2021.R;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.pojo.TripStatus;
import com.example.tripreminder2021.repository.FirebaseDatabaseServices;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import java.io.Serializable;

import static com.example.tripreminder2021.zService.AlarmEventReciever.RECEIVED_TRIP;
import static com.example.tripreminder2021.zService.AlarmEventReciever.RECEIVED_TRIP_SEND_SERIAL;


public class MyDialogActivity extends Activity {

    private static final String TAG = "TAG";
    DialognotificationService mService;
    AlertDialog alertDialog;
    android.app.AlertDialog alert;
    boolean started = false;

    private FirebaseDatabaseServices firebaseDatabaseServices;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_dialog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }


        firebaseDatabaseServices=new FirebaseDatabaseServices();


        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        Intent i = getIntent();
        Bundle b = i.getBundleExtra(RECEIVED_TRIP);
            TripModel tm = (TripModel) b.getParcelable(RECEIVED_TRIP_SEND_SERIAL);
        if (tm != null) {
            startAlarmRingTone(r);

            AlertDialog.Builder Builder = new AlertDialog.Builder(this)
                    .setMessage("Your Trip: \" " + tm.getTripname() + "\" is now on...")
                    .setTitle("Trip Reminder")
                    .setIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setNegativeButton("Snooze", (dialog, which) -> {
                        Toast.makeText(MyDialogActivity.this, "Trip Snooze", Toast.LENGTH_SHORT).show();
                        stopAlarmRingTone(r);
                        startDialogService(tm);

                        finish();
                    })
                    .setPositiveButton("Start Trip", (dialogInterface, i1) -> {
                        Toast.makeText(MyDialogActivity.this, "Trip Will Start", Toast.LENGTH_SHORT).show();
                        tm.setStatus("Done!");

                        firebaseDatabaseServices.addTripToHistory(tm.getTrip_id());
                        firebaseDatabaseServices.changeTripStatus(tm.getTrip_id(), TripStatus.Done);

                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + tm.getEndloc());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        stopAlarmRingTone(r);
                        startActivity(mapIntent);
                        EventBus.getDefault().postSticky(tm);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            Intent intent =new Intent(MyDialogActivity.this, FloatingWindowService.class);
                            startService(intent);
                            finish();
                        } else if (Settings.canDrawOverlays(this)) {
                            Intent intent =new Intent(MyDialogActivity.this, FloatingWindowService.class);
                            startService(intent);
                            finish();
                        } else {
                            askPermission();
                            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
                        }

                        finish();
                    })
                    .setNeutralButton("Cancel Trip", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MyDialogActivity.this, "Trip Canceled", Toast.LENGTH_SHORT).show();
                            tm.setStatus("Canceled!");

                            firebaseDatabaseServices.addTripToHistory(tm.getTrip_id());
                            firebaseDatabaseServices.changeTripStatus(tm.getTrip_id(),TripStatus.Canceled);

                            stopAlarmRingTone(r);
                            alertDialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelable(false).setOnDismissListener(dialog -> {
                        stopAlarmRingTone(r);

                    });


            alertDialog = Builder.create();
            alertDialog.show();

        } else {
            Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show();
        }

    }

// floating widget permission
    private void askPermission() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
        }
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    public void startDialogService(TripModel tm) {
        Intent service = new Intent(this, DialognotificationService.class);

   
        EventBus.getDefault().postSticky(tm);

        service.putExtra(RECEIVED_TRIP_SEND_SERIAL, (Serializable) tm);
        service.putExtra("test", "MEMO");
        startService(service);
        bindService(service, mServiceConnection, BIND_ADJUST_WITH_ACTIVITY);
        alertDialog.dismiss();
        /*
         * Flag for: If binding from an app that is visible or user-perceptible,
         * lower the target service's importance to below the perceptible level. This allows
         * the system to (temporarily) expunge the bound process from memory to make room for more
         * important user-perceptible processes.
         */
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((DialognotificationService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void startAlarmRingTone(Ringtone r) {
        r.play();
    }

    public void stopAlarmRingTone(Ringtone r) {
        r.stop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            if (checkPermission()) {
            } else {
                reqPermission();
            }
        }
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                reqPermission();
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }

    }

    private void reqPermission() {
        final android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Screen overlay detected");
        alertBuilder.setMessage("Enable 'Draw over other apps' in your system setting.");
        alertBuilder.setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, RESULT_OK);
            }
        });
        alert = alertBuilder.create();
        alert.show();
    }




}
