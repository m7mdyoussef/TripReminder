package com.example.tripreminder2021.ui.navigation.upComing;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripreminder2021.*;
import com.example.tripreminder2021.adapters.UpcomingRecyclerViewAdapter;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.pojo.TripStatus;
import com.example.tripreminder2021.repository.FirebaseDatabaseServices;
import com.example.tripreminder2021.requests.InternetConnection;
import com.example.tripreminder2021.ui.activities.AddBtnActivity;
import com.example.tripreminder2021.ui.activities.UpcomingTripsActivity;
import com.example.tripreminder2021.viewModels.UpcomingViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class UpcomingFragment extends Fragment {

    private RecyclerView recyclerView;
    private UpcomingRecyclerViewAdapter recyclerViewAdapter;
    private UpcomingViewModel upcomingViewModel;
    private ArrayList<TripModel> myList=new ArrayList<>();
    private ProgressBar progressBar;
    private TextView textView;
    private InternetConnection internetConnection;
    FloatingActionButton fab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        upcomingViewModel= ViewModelProviders.of(this).get(UpcomingViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.recycler);
        progressBar=root.findViewById(R.id.upcoming_progress);
        textView=root.findViewById(R.id.text_no_upcoming);
        progressBar.setVisibility(View.VISIBLE);
        internetConnection=new InternetConnection(getContext());
        upcomingViewModel.init();

        recyclerViewAdapter = new UpcomingRecyclerViewAdapter(myList,getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddBtnActivity.class);
                startActivityForResult(i, 55);
            }
        });


        internetConnection.observe(getViewLifecycleOwner(),aBoolean -> {
            if (aBoolean)
            {
                upcomingViewModel.getUpcomingTrips().observe(getViewLifecycleOwner(), new Observer<ArrayList<TripModel>>() {
                    @Override
                    public void onChanged(ArrayList<TripModel> list) {
                        if (list.size()==0){
                            recyclerView.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                        }
                        else {
                            recyclerView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                            recyclerViewAdapter.setData(list);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            else {
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        upcomingViewModel.getUpcomingTrips().observe(getViewLifecycleOwner(), new Observer<ArrayList<TripModel>>() {
            @Override
            public void onChanged(ArrayList<TripModel> list) {
                if (list.size()==0) {
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    recyclerViewAdapter.setData(list);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        return root;
    }
}
