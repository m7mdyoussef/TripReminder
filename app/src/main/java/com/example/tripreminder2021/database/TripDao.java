package com.example.tripreminder2021.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tripreminder2021.pojo.TripModel;

import java.util.ArrayList;


@Dao
public interface TripDao
{
    /*
    @Insert
    void insert (TripModel trip);

    @Query("delete from trip_table ")
    void delete_all();

    @Query("select * from trip_table where include_in = 'UPCOMING' ")
    LiveData<ArrayList<TripModel>> getUpcomingTrips();

    @Query("select * from trip_table where include_in = 'HISTORY' ")
    LiveData<ArrayList<TripModel>> getHistoryTrips();

     */
}
