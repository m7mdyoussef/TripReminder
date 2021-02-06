package com.example.tripreminder2021.ui.navigation.report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripreminder2021.*;
import com.example.tripreminder2021.config.DistanceCalculator;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.pojo.TripStatus;
import com.example.tripreminder2021.requests.InternetConnection;
import com.example.tripreminder2021.viewModels.ReportViewModel;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Calendar;

public class ReportFragment extends Fragment {

    private ReportViewModel reportViewModel;
    private DatePickerDialog datePickerDialog;


    private TextView from,to;

    private ImageView start_date_picker ;
    private ImageView end_date_picker ;
    private Button show_report;
    private ProgressBar progressBar;

    private RelativeLayout relativeLayoutReport;

    private TextView txt_date_range1;
    private TextView txt_date_range2;
    private TextView total_trips;
    private TextView total_done_trips;
    private TextView total_canceled_trips;
    private TextView average_distance;
    private TextView average_time;

    private ArrayList<TripModel> trips=new ArrayList<TripModel>();

    private InternetConnection abc;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reportViewModel =
                ViewModelProviders.of(this).get(ReportViewModel.class);
        View root = inflater.inflate(R.layout.fragment_reports, container, false);
        abc=new InternetConnection(getContext());
        abc.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                {
                    show_report.setEnabled(true);
                }
                else
                {
                    show_report.setEnabled(false);
                }

            }
        });

        initViews(root);

        start_date_picker.setOnClickListener(v ->
           setStartDate());

        end_date_picker.setOnClickListener(v ->
            setEndDate());

        show_report.setOnClickListener(v ->
            action_showReport());

        reportViewModel.getReportedList(from.getText().toString(),to.getText().toString())
                .observe(getViewLifecycleOwner(), list -> {
                    int arr[]=new int[2];
                    double distance=0.0d;
                    double time=0.0d;
                    distance=getAverageDistance(list);
                    time=getTime(list);
                    arr=getDoneCancelSize(list);

                    relativeLayoutReport.setVisibility(View.VISIBLE);
                    total_trips.setText(list.size()+" ");
                    total_done_trips.setText(arr[0]+" ");
                    total_canceled_trips.setText(arr[1]+" ");

                    average_distance.setText((int)distance+" "+getString(R.string.km));
                    average_time.setText((int)distance/50+" "+getString(R.string.hour));
                    progressBar.setVisibility(View.GONE);
                });
        return root;
    }
    private void action_showReport() {

        relativeLayoutReport.setVisibility(View.GONE);
        Log.i("TAG", "click: ");
        String start="".concat(from.getText().toString());
        String To ="".concat(to.getText().toString());
        if(start.equals(null) ||start.isEmpty()||To.isEmpty()||To.equals(null))
            Toast.makeText(getContext(), "Enter The Duration First", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            trips = reportViewModel.getReportedList(start, To).getValue();
            txt_date_range1.setText(start);
            txt_date_range2.setText(To);
        }
    }

    private void setEndDate() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        // date picker dialog
        datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) ->
                to.setText(dayOfMonth+"-"+(monthOfYear + 1)+"-"+ year1)
                , year, month, day);
        datePickerDialog.show();
    }

    private void setStartDate() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        // date picker dialog
        datePickerDialog = new DatePickerDialog(getContext(), (view, year12, monthOfYear, dayOfMonth) -> from.setText(dayOfMonth+"-"+(monthOfYear + 1)+"-"+ year12), year, month, day);
        datePickerDialog.show();
    }

    private void initViews(View root) {

        from=root.findViewById(R.id.date_Selected_start);
        to=root.findViewById(R.id.date_Selected_end);
        start_date_picker = root.findViewById(R.id.date_Picker_start);
        end_date_picker = root.findViewById(R.id.date_Picker_end);
        show_report = root.findViewById(R.id.showReport);

        relativeLayoutReport=root.findViewById(R.id.relative_report);
        relativeLayoutReport.setVisibility(View.GONE);

        progressBar=root.findViewById(R.id.report_progress);

        txt_date_range1=root.findViewById(R.id.txt_date_range1);
        txt_date_range2=root.findViewById(R.id.txt_date_range2);
        total_trips=root.findViewById(R.id.txt_total_trips);
        total_canceled_trips=root.findViewById(R.id.txt_total_canceled_trips);
        total_done_trips=root.findViewById(R.id.txt_total_done_trips);
        average_distance=root.findViewById(R.id.txt_total_distance);
        average_time=root.findViewById(R.id.txt_average_time);
    }

    private int[] getDoneCancelSize(ArrayList<TripModel> list) {
     int doneSize=0;
     int canceledSize=0;
     int arr[]=new int[2];
     for (int i=0;i<list.size();i++)
     {
         if (list.get(i).getStatus().equals(TripStatus.Done.toString()))
              doneSize++;
         if (list.get(i).getStatus().equals(TripStatus.Canceled.toString()))
              canceledSize++;

     }
     arr[0]=doneSize;
     arr[1]=canceledSize;
        return arr;
    }

    private double getAverageDistance(ArrayList<TripModel>list){
         DistanceCalculator.getInstance(getContext());
         return DistanceCalculator.getTotalDistance(list)/1000;
    }
    private double getTime(ArrayList<TripModel> list){

        return 25.05;
    }
}