package com.example.tripreminder2021.config;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.tripreminder2021.pojo.Distance;
import com.example.tripreminder2021.pojo.TripModel;
import com.example.tripreminder2021.ui.activities.UpcomingTripsActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DistanceCalculator {


    private static ArrayList<TripModel> tripModels=new ArrayList<>();
    private static ArrayList<String> StartPoints = new ArrayList<>();
    private static ArrayList<String> EndPoints = new ArrayList<>();
    private static ArrayList<Distance> distanceArrayList=new ArrayList<>();
    private static Context context;
    private static DistanceCalculator distanceCalculator;

    private DistanceCalculator(Context context)
    {
        this.context=context;
    }
    public static DistanceCalculator getInstance(Context context)
    {
        if (distanceCalculator==null) {
            distanceCalculator = new DistanceCalculator(context);
        }
        return distanceCalculator;
    }
    private static ArrayList<Distance> getLatLngList(ArrayList<TripModel> Trips)
    {
        ArrayList<Distance> distanceArrayList=new ArrayList<>();
        List<Address> addresses1;
        List<Address> addresses2;
        LatLng Loc1 = null;
        LatLng Loc2 =null;
        Geocoder geocoder = new Geocoder(context);

        for (int i = 0; i < Trips.size(); i++) {
            EndPoints.add(Trips.get(i).getEndloc());
            StartPoints.add(Trips.get(i).getStartloc());

            try {
                addresses1 = geocoder.getFromLocationName(StartPoints.get(i), 5);
                addresses2 = geocoder.getFromLocationName(EndPoints.get(i), 5);

                for (Address a : addresses1)
                    if (a.hasLatitude() && a.hasLongitude())
                        Loc1 = new LatLng(a.getLatitude(), a.getLongitude());

                for (Address a : addresses2)
                    if (a.hasLatitude() && a.hasLongitude())
                        Loc2 = new LatLng(a.getLatitude(), a.getLongitude());

                distanceArrayList.add(new Distance(Loc1,Loc2));


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return distanceArrayList;
    }
    private static double distance (LatLng loc1, LatLng loc2 )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(loc2.latitude-loc1.latitude);
        double lngDiff = Math.toRadians(loc2.longitude-loc1.longitude);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(loc1.latitude)) * Math.cos(Math.toRadians(loc2.latitude)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;
        return new Double(distance * meterConversion).doubleValue();
    }

    public static double getTotalDistance(ArrayList<TripModel> tripModels)
    {
        distanceArrayList=getLatLngList(tripModels);
        double sum=0;
        for (int i=0;i<distanceArrayList.size();i++) {
            sum=sum+distance(distanceArrayList.get(i).getFirstLatLng(),distanceArrayList.get(i).getSecondLatLng());
        }
        return sum;
    }

}

