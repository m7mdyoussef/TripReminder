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

    private final String TAG = "C-Manager";
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    private Set<Network> validNetworks= new HashSet<>();

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


    private void checkValidNetworks()
    {
        postValue((validNetworks.size()>0));
    }

    @Override
    protected void onInactive() {
        super.onInactive();
    }

    @Override
    protected void onActive() {
        //networkCallback = createNetworkCallback();

    }


}
