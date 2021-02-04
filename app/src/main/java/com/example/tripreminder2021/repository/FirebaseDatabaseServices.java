package com.example.tripreminder2021.repository;
import androidx.lifecycle.MutableLiveData;

import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.pojo.TripStatus;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseServices {

    public void addTripToHistory(String trip_id) {
        DatabaseRepo.getInstance().addTripToHistory(trip_id);
    }
    public void changeTripStatus(String trip_id, TripStatus status) {
        DatabaseRepo.getInstance().changeTripStatus(trip_id,status);
    }
    public void updateTrip(String trip_id, TripModel tripModel) {
        DatabaseRepo.getInstance().updateTrip(trip_id,tripModel);
    }
    public void updateNotes(String trip_id, List<String> notes)
    {
        DatabaseRepo.getInstance().updateNotes(trip_id,notes);
    }
    public void deleteTrip(String trip_id) {
        DatabaseRepo.getInstance().deleteTrip(trip_id);
    }

    public void addTrip(TripModel trip)
    {

        DatabaseRepo.getInstance().addTrip(trip);
    }
    public MutableLiveData<ArrayList<TripModel>> getTripsReport(String from, String to)
    {

        return DatabaseRepo.getInstance().getTripsReport(from,to);
    }
}
