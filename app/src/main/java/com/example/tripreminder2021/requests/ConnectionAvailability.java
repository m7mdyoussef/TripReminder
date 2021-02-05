package com.example.tripreminder2021.requests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import androidx.lifecycle.LiveData;

import java.util.HashSet;
import java.util.Set;

public class ConnectionAvailability extends LiveData<Boolean> {

    private Context context;

    public ConnectionAvailability(Context context)
    {
        this.context = context;
    }
    public boolean isConnectingToInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return true;
            }
        }
        return false;
    }

}
