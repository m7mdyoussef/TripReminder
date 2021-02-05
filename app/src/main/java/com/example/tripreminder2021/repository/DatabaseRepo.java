package com.example.tripreminder2021.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.state.State;
import androidx.lifecycle.MutableLiveData;

import com.example.tripreminder2021.config.Constants;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.pojo.TripStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseRepo {


    private static DatabaseRepo instance;
    private static DatabaseReference reference;
    private MutableLiveData<ArrayList<TripModel>> tripsReport=new MutableLiveData<>();

    public static DatabaseRepo getInstance()    {
        if (instance==null)
        {
            instance=new DatabaseRepo();
            reference=FirebaseDatabase.getInstance().getReference();
        }
        return instance;
    }

    public void addTripToHistory(String trip_id) {
        reference.child(Constants.TRIP_CHILD_NAME).
                child(Constants.CURRENT_USER_ID).
                child(trip_id).
                child(Constants.SEARCH_CHILD_NAME).setValue(Constants.SEARCH_CHILD_HISTORY_KEY);

    }

    public void changeTripStatus(String trip_id, TripStatus status) {
        reference.child(Constants.TRIP_CHILD_NAME).
                child(Constants.CURRENT_USER_ID).
                child(trip_id).
                child(Constants.STATUS_CHILD_NAME).
                setValue(status);

        saveTheTripInHistory(trip_id);
    }

    private void saveTheTripInHistory(String trip_id)    {
        reference.child(Constants.TRIP_CHILD_NAME).
                child(Constants.CURRENT_USER_ID).
                child(trip_id).
                child(Constants.SEARCH_CHILD_NAME).
                setValue(Constants.SEARCH_CHILD_HISTORY_KEY);

    }

    public void updateTrip(String trip_id, TripModel tripModel) {
        reference.child(Constants.TRIP_CHILD_NAME)
                .child(Constants.CURRENT_USER_ID)
                .child(trip_id)
                .setValue(tripModel);
    }

    public void deleteTrip(String trip_id) {
       reference.child(Constants.TRIP_CHILD_NAME)
                .child(Constants.CURRENT_USER_ID)
                .child(trip_id).removeValue();
    }

    public void addTrip(TripModel trip)    {
        trip.setTrip_id(reference.child(Constants.TRIP_CHILD_NAME)
                .child(Constants.CURRENT_USER_ID)
                .push().getKey());
        reference.child(Constants.TRIP_CHILD_NAME)
                .child(Constants.CURRENT_USER_ID)
                .child(trip.getTrip_id())
                .setValue(trip);
    }

    public void updateNotes(String trip_id, List<String> notes) {
        reference.child(Constants.TRIP_CHILD_NAME).
                child(Constants.CURRENT_USER_ID).
                child(trip_id).
                child("notes").
                setValue(notes);
    }
    public MutableLiveData<ArrayList<TripModel>> getTripsReport(String from,String to) {
        ArrayList<TripModel> allTripsReport=new ArrayList<>();

       /* try {
           Date date1=new SimpleDateFormat("dd-MM-yyyy").parse(from);
           Date date2=new SimpleDateFormat("dd-MM-yyyy").parse(to);

        */


        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        Query query=reference.child(Constants.TRIP_CHILD_NAME).
                child(Constants.CURRENT_USER_ID).
                orderByChild("date").
                startAt(from).endAt(to);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    allTripsReport.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if(dataSnapshot.getValue(TripModel.class).getInclude_in()
                                .equals(Constants.SEARCH_CHILD_HISTORY_KEY))
                        {
                            allTripsReport.add(dataSnapshot.getValue(TripModel.class));
                        }
                    }
                    Log.i("TAG", "onDataChangeall trip size: "+allTripsReport.size());
                    tripsReport.postValue(allTripsReport);
                    Log.i("TAG", "date: "+allTripsReport.get(0).getDate());
                }
                else
                    Log.i("TAG", "onDataChange: nnnnnnnnnnnnnnnnnnnn");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return tripsReport;
    }
}
