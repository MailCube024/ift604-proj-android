package com.ift604.ift604_projet.projet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends FragmentActivity {
    private GoogleMap mMap;
    private BroadcastReceiver mReceiver;
    private CheckPositionTask mCheckPosition;
    private PlantBombTask mPlantBomb;
    private DiffuseBombTask mDiffuseBomb;
    private Button mDiffuseButton;
    private boolean mIsDiffusingMode;
    private String mProfile;
    private LatLng mCurrentLatLng;

    //For testing
    LatLng testingBomb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mIsDiffusingMode = false;
        mProfile = "Toto";

        mDiffuseButton = (Button) findViewById(R.id.diffuseButton);

        mDiffuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDiffuseBomb = new DiffuseBombTask(getBaseContext(), mProfile, mDiffuseButton);
                mDiffuseBomb.execute(mCurrentLatLng.latitude, mCurrentLatLng.longitude);

                // For testing
                Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.cancel();
                mDiffuseButton.setVisibility(View.INVISIBLE);
            }
        });

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);

        // Position updates
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Location", "Position received !");

                Double latitude = intent.getDoubleExtra("Lat", 0);
                Double longitude = intent.getDoubleExtra("Long", 0);

                if(mIsDiffusingMode) {
                    mCheckPosition = new CheckPositionTask(getBaseContext(), mProfile, mDiffuseButton);

                    mCheckPosition.execute(latitude, longitude);
                    mCurrentLatLng = new LatLng(latitude, longitude);

                    // For testing
                    double distance = latLngToMeters(mCurrentLatLng, testingBomb);

                    Log.d("Distance", distance + "");

                    long[] closePattern = {0, 200, 1000};
                    long[] veryClosePattern = {0, 200, 500};
                    long[] diffusePattern = {0, 50, 0};
                    Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (distance < 10) {
                        mVibrator.vibrate(diffusePattern, 0);
                        mDiffuseButton.setVisibility(View.VISIBLE);
                    }
                    else if (distance < 50)
                        mVibrator.vibrate(veryClosePattern, 0);
                    else if (distance < 100)
                        mVibrator.vibrate(closePattern, 0);

                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        startService(new Intent(MapActivity.this, LocationService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUpMapIfNeeded();
        stopService(new Intent(MapActivity.this, LocationService.class));

        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mReceiver),
                new IntentFilter("Location")
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onStop();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(!mIsDiffusingMode) {
                    mPlantBomb = new PlantBombTask(getBaseContext(), mProfile);
                    mPlantBomb.execute(latLng.latitude, latLng.longitude);

                    //For testing
                    mIsDiffusingMode = true;
                    testingBomb = latLng;
                }
            }
        });
    }

    // For testing
    // From http://stackoverflow.com/questions/639695/how-to-convert-latitude-or-longitude-to-meters
    private double latLngToMeters(LatLng p1, LatLng p2) {
        double latMid, m_per_deg_lat, m_per_deg_lon, deltaLat, deltaLon,dist_m;

        latMid = (p1.latitude + p2.latitude) / 2.0;


        m_per_deg_lat = 111132.954 - 559.822 * Math.cos(2.0 * latMid) + 1.175 * Math.cos(4.0 * latMid);
        m_per_deg_lon = (3.14159265359/180 ) * 6367449 * Math.cos(latMid);

        deltaLat = Math.abs(p1.latitude - p2.latitude);
        deltaLon = Math.abs(p1.longitude - p2.longitude);

        return Math.sqrt(Math.pow(deltaLat * m_per_deg_lat, 2) + Math.pow(deltaLon * m_per_deg_lon , 2));
    }
}
