package com.example.tripreminder2021.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripreminder2021.adapters.HistoryRecyclerViewAdapter;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.repository.HistoryRepository;
import com.example.tripreminder2021.repository.UpcomingRepository;

import java.util.ArrayList;

public class HistoryViewModel extends ViewModel {

    private MutableLiveData<ArrayList<TripModel>> tripList;

    public void init()
    {
        if (tripList!=null)
            return;
        tripList= HistoryRepository.getInstance().getHistoryTrips();
    }

    public MutableLiveData<ArrayList<TripModel>> getHistoryTrips() {
        tripList=HistoryRepository.getInstance().getHistoryTrips();
        return tripList;
    }

}