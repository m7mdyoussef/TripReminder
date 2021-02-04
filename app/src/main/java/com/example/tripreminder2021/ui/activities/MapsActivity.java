package com.example.tripreminder2021.ui.activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tripreminder2021.R;
import com.example.tripreminder2021.pojo.TripModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<String> StartPoints = new ArrayList<>();
    private ArrayList<String> EndPoints = new ArrayList<>();
    private ArrayList<TripModel> Trips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        Trips = (ArrayList<TripModel>) args.getSerializable("LIST");

        if (Trips.size() > 0 || Trips != null) {
            for (int i = 0; i < Trips.size(); i++) {
                EndPoints.add(Trips.get(i).getEndloc());
                StartPoints.add(Trips.get(i).getStartloc());
            }
            mapFragment.getMapAsync(this);
        } else
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(googleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        List<Address> addresses1;
        List<Address> addresses2;
        LatLng Loc1 = null;
        LatLng Loc2 =null;
        Geocoder geocoder = new Geocoder(this);
        try {
            for (int i = 0; i < StartPoints.size(); i++) {

                addresses1 = geocoder.getFromLocationName(StartPoints.get(i), 3);
                addresses2 = geocoder.getFromLocationName(EndPoints.get(i), 3);

                for (Address a : addresses1)
                    if (a.hasLatitude() && a.hasLongitude())
                        Loc1 = new LatLng(a.getLatitude(), a.getLongitude());

                for (Address a : addresses2)
                    if (a.hasLatitude() && a.hasLongitude())
                        Loc2 = new LatLng(a.getLatitude(), a.getLongitude());

                Random random=new Random();
                int color=Color.argb(255,random.nextInt(256),
                        random.nextInt(255), random.nextInt(255));

                mMap.addMarker(new MarkerOptions().position(Loc1).title("start_Point "+Trips.get(i).getDate()).
                        icon(BitmapDescriptorFactory.defaultMarker(Color.alpha(color))));
                mMap.addMarker(new MarkerOptions().position(Loc2).title("end_point  "+Trips.get(i).getNotes()).
                        icon(BitmapDescriptorFactory.defaultMarker(Color.alpha(color))));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Loc1, 1f));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
                mMap.addPolyline(new PolylineOptions().add(Loc1).add(Loc2).width(15f).color(color));

            }

        } catch (IOException e) {
            // handle the exception
        }
    }
}