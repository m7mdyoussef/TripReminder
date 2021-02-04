package com.example.tripreminder2021.pojo;

import com.google.android.gms.maps.model.LatLng;

public class Distance {
    private LatLng firstLatLng;
    private LatLng secondLatLng;

    public Distance(LatLng firstLatLng, LatLng secondLatLng) {
        this.firstLatLng = firstLatLng;
        this.secondLatLng = secondLatLng;
    }

    public LatLng getFirstLatLng() {
        return firstLatLng;
    }

    public void setFirstLatLng(LatLng firstLatLng) {
        this.firstLatLng = firstLatLng;
    }

    public LatLng getSecondLatLng() {
        return secondLatLng;
    }

    public void setSecondLatLng(LatLng secondLatLng) {
        this.secondLatLng = secondLatLng;
    }
}
