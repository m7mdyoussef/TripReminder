package com.example.tripreminder2021.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.repository.FirebaseDatabaseServices;
import com.example.tripreminder2021.repository.HistoryRepository;

import java.util.ArrayList;
import java.util.Date;

public class ReportViewModel extends ViewModel {

    private MutableLiveData<ArrayList<TripModel>> tripReportedList=new MutableLiveData<>();
    private FirebaseDatabaseServices firebaseDatabaseServices=new FirebaseDatabaseServices();

    public MutableLiveData<ArrayList<TripModel>> getReportedList(long from, long to) {
        tripReportedList=firebaseDatabaseServices.getTripsReport(from, to);
        return tripReportedList;
    }
}