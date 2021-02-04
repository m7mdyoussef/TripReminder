package com.example.tripreminder2021.zService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.tripreminder2021.R;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.ui.activities.UpcomingTripsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;



public class FloatingWindowService extends Service implements View.OnClickListener {
    private final IBinder localBinder = new MyBinder();

    TripModel mTripModel;
    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private ListView ListView;
    private boolean listOn = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onResult(TripModel result){
        mTripModel=result;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);


        //  getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (mWindowManager != null) {
            mWindowManager.addView(mFloatingView, params);
        }


        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);
         ListView = mFloatingView.findViewById(R.id.list);
        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
        expandedView.setOnClickListener( this);

        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
                        collapsedView.setVisibility(View.GONE);
                        expandedView.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
//        openapp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ListView listview = new ListView(FloatingWindowService.this);
//                String aarray[] = {"View notes from Home"};
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(FloatingWindowService.this,
//                        android.R.layout.simple_list_item_1, aarray);
//                listview.setAdapter(adapter);
//
//                if (!listOn) {
//                    ll.addView(listview);
//                } else {
//                    ll.removeView(listview);
//                }
//
//            }
//        });
//        openapp.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Intent i = new Intent(FloatingWindowService.this, UpcomingTripsActivity.class);
//                i.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//                wm.removeView(ll);
//                return false;
//            }
//        });
//    }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    public class MyBinder extends Binder {
        public FloatingWindowService getService() {
            return FloatingWindowService.this;
        }

    }

//    public void setTripModel(TripModel tm) {
//        this.mTripModel = tm;
//    }


        @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutExpanded:
                //switching views
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

                List<String> notes = mTripModel.getNotes();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(FloatingWindowService.this,
                        android.R.layout.simple_list_item_1, notes);
                ListView.setAdapter(adapter);

                break;

            case R.id.buttonClose:
                //closing the widget
                stopSelf();
                break;
        }
    }

}

