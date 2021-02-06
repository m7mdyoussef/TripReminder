package com.example.tripreminder2021.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.tripreminder2021.database.TripDao;
import com.example.tripreminder2021.database.TripDatabase;
import com.example.tripreminder2021.pojo.TripModel;

import java.util.ArrayList;
import java.util.List;

public class TripRepository {

    /*
    private TripDao tripDao;

    private LiveData<ArrayList<TripModel>> upcomingTrips;
    private LiveData<ArrayList<TripModel>> historyTrips;

    public TripRepository(Context context)
    {
        TripDatabase database=TripDatabase.getInstance(context);
        tripDao=database.tripDao();
        upcomingTrips=tripDao.getUpcomingTrips();
        historyTrips=tripDao.getHistoryTrips();
    }
    public void insert(TripModel trip)
    {
        tripDao.insert(trip);
    }
    public void delete_all()
    {
        tripDao.delete_all();
    }
    public LiveData<ArrayList<TripModel>> getHistoryTrips()
    {
        return tripDao.getHistoryTrips();
    }
    public LiveData<ArrayList<TripModel>> getUpcomingTrips()
    {
        return tripDao.getUpcomingTrips();
    }

     */

}
