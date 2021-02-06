package com.example.tripreminder2021.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tripreminder2021.pojo.TripModel;


//@Database(entities = TripModel.class,version = 1)
public abstract class TripDatabase extends RoomDatabase
{

    private static TripDatabase instance;

    public abstract TripDao tripDao();  // room generate the code

    public static synchronized TripDatabase getInstance(Context context)
    {
        if (instance==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),
                    TripDatabase.class, "trip_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
