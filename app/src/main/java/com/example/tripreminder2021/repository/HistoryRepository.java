package com.example.tripreminder2021.repository;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tripreminder2021.config.*;
import com.example.tripreminder2021.pojo.TripModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryRepository {

    private ArrayList<TripModel> allTrips=new ArrayList<>();
    private MutableLiveData<ArrayList<TripModel>> trips=new MutableLiveData<>();
    private static HistoryRepository instance;


    public static HistoryRepository getInstance()
    {
        if (instance==null)
        {
            instance=new HistoryRepository();
        }
        return instance;
    }
    public MutableLiveData<ArrayList<TripModel>> getHistoryTrips()
    {
        if (allTrips.size()==0) getTrips();
        trips.setValue(allTrips);
        return trips;
    }
    private void getTrips()
    {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        Query query=reference.child(Constants.TRIP_CHILD_NAME).
                child(Constants.CURRENT_USER_ID).
                        orderByChild(Constants.SEARCH_CHILD_NAME).
                                equalTo(Constants.SEARCH_CHILD_HISTORY_KEY);

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
    }
}
