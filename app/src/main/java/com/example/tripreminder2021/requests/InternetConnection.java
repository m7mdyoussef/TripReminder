package com.example.tripreminder2021.requests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.util.HashSet;
import java.util.Set;

public class InternetConnection  extends LiveData<Boolean> {

    private Context context;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private Set<Network> networkSet=new HashSet<>();


    public InternetConnection(Context context){
        this.context=context;
        connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    private void checkValidNetworks()
    {
         postValue(networkSet.size() > 0);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActive() {
        networkCallback=new InternetCallBack();
        NetworkRequest networkRequest= new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(networkRequest,networkCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class InternetCallBack extends ConnectivityManager.NetworkCallback{
        @Override
        public void onAvailable(@NonNull Network network) {
            Log.d("TAG", "onAvailable: ${network}"+network);
            NetworkCapabilities networkCapabilities=connectivityManager
                    .getNetworkCapabilities(network);
            boolean isInternet=networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

            Log.d("TAG", "onAvailable: ${network}, $isInternet"+isInternet);
            if(isInternet ==  true){
                networkSet.add(network);
            }
            checkValidNetworks();
        }

        @Override
        public void onLost(@NonNull Network network) {
            Log.d("TAG", "onLost: ${network}"+network);
            networkSet.remove(network);
            checkValidNetworks();
        }
    }

}
