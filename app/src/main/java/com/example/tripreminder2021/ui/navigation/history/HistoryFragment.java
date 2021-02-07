package com.example.tripreminder2021.ui.navigation.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.tripreminder2021.*;
import com.example.tripreminder2021.adapters.HistoryRecyclerViewAdapter;
import com.example.tripreminder2021.adapters.UpcomingRecyclerViewAdapter;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.requests.InternetConnection;
import com.example.tripreminder2021.ui.activities.MapsActivity;
import com.example.tripreminder2021.viewModels.HistoryViewModel;
import com.example.tripreminder2021.viewModels.UpcomingViewModel;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btn_draw_maps;
    private HistoryRecyclerViewAdapter recyclerViewAdapter;
    private HistoryViewModel historyViewModel;
    private ArrayList<TripModel> myList=new ArrayList<>();
    private ProgressBar progressBar;
    private InternetConnection internetConnection;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        historyViewModel= ViewModelProviders.of(this).get(HistoryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_history, container, false);

        internetConnection=new InternetConnection(getContext());
        recyclerView = root.findViewById(R.id.recycler);
        btn_draw_maps = root.findViewById(R.id.draw_map);
        progressBar=root.findViewById(R.id.history_progress);
        progressBar.setVisibility(View.VISIBLE);

        btn_draw_maps.setOnClickListener(h->openMaps(recyclerViewAdapter.getData()));

        internetConnection.observe(getViewLifecycleOwner(),aBoolean -> {
            if (aBoolean)
                btn_draw_maps.setVisibility(View.VISIBLE);
            else
                btn_draw_maps.setVisibility(View.GONE);
        });

        historyViewModel.init();
        recyclerViewAdapter = new HistoryRecyclerViewAdapter(getContext(),myList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        historyViewModel.getHistoryTrips().observe(getViewLifecycleOwner(), list -> {
            recyclerViewAdapter.setData(list);
        });
        progressBar.setVisibility(View.GONE);
        return root;
    }

    private void openMaps(ArrayList<TripModel> tripModels) {
        if (tripModels.isEmpty()|| tripModels==null)
        {
            Toast.makeText(getContext(), "Nothing to Show", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent=new Intent(getContext(),MapsActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("LIST",(Serializable) tripModels);
            intent.putExtra("BUNDLE",args);
            startActivity(intent);
        }

    }
}
