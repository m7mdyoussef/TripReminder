package com.example.tripreminder2021.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.repository.FirebaseDatabaseServices;
import com.example.tripreminder2021.repository.HistoryRepository;

import java.util.ArrayList;

public class ReportViewModel extends ViewModel {

    private MutableLiveData<ArrayList<TripModel>> tripReportedList=new MutableLiveData<>();
    private FirebaseDatabaseServices firebaseDatabaseServices=new FirebaseDatabaseServices();

    public MutableLiveData<ArrayList<TripModel>> getReportedList(String from, String to) {
        tripReportedList=firebaseDatabaseServices.getTripsReport(from, to);
        return tripReportedList;
    }
}