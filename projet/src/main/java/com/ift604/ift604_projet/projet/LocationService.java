package com.ift604.ift604_projet.projet;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocalBroadcastManager mBroadcaster;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        mBroadcaster = LocalBroadcastManager.getInstance(this);

        Log.d("Location", "Service started !");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();

        Log.d("Location", "Service stopped !");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("Location", "API connected !");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("Location", "API connection failed !");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasAccuracy()) {
            Log.d("Location", "Accuracy : " + location.getAccuracy());
            if (location.getAccuracy() < 20) {
                if (location != null) {
                    Log.d("Location", "Changed !");
                    Intent intent = new Intent("Location");
                    intent.putExtra("Lat", location.getLatitude());
                    intent.putExtra("Long", location.getLongitude());
                    mBroadcaster.sendBroadcast(intent);
                }
            }
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}
