package com.example.tripreminder2021.repository;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;

import com.example.tripreminder2021.config.*;
import com.example.tripreminder2021.pojo.TripModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UpcomingRepository {

    private MutableLiveData<ArrayList<TripModel>> trips=new MutableLiveData<>();
    private static UpcomingRepository instance;

    public static UpcomingRepository getInstance()
    {
        if (instance==null)
        {
            instance=new UpcomingRepository();
        }
        return instance;
    }
    public MutableLiveData<ArrayList<TripModel>> getUpcomingTrips()
    {
        ArrayList<TripModel> allTrips=new ArrayList<>();

        allTrips.clear();
        trips.setValue(allTrips);

        Log.i("TAG", "getTrips: "+Constants.CURRENT_USER_ID);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        Query query=reference.child(Constants.TRIP_CHILD_NAME)
                .child(Constants.CURRENT_USER_ID).
                        orderByChild(Constants.SEARCH_CHILD_NAME).
                        equalTo(Constants.SEARCH_CHILD_UPCOMING_KEY);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.i("TAG", "onChildAdded: ");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.i("TAG", "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.i("TAG", "onChildRemoved: ");

                allTrips.clear();
                trips.postValue(allTrips);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.i("TAG", "onChildMoved: ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.i("TAG", "onCancelled: ");
            }
        });
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    allTrips.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        allTrips.add(dataSnapshot.getValue(TripModel.class));
                    }
                    trips.postValue(allTrips);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return trips;
    }
}
