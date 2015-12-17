package com.ift604.ift604_projet.projet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private static final String BASE_URL = "http://localhost:60631/";
    private static final String DISTANCE_SERVICE = "Bomb/ClosestDistance?latitude=%1$,.2f&longitude=%1$,.2f";
    private static final String DEFUSE_SERVICE = "Bomb/Defuse?bombId=%d";
    private static final String PLANT_SERVICE = "Bomb/Plant?latitude=%1$,.2f&longitude=%1$,.2f";

    private GoogleMap mMap;
    private BroadcastReceiver mLocationReceiver;
    private BroadcastReceiver mGameEventsReceiver;
    private ClosestDistanceTask mClosestDistance;
    private PlantBombTask mPlantBomb;
    private DefuseBombTask mDefuseBomb;
    private Button mDefuseButton;
    private Boolean mIsDefusingMode;
    private String mProfile;
    private LatLng mCurrentLatLng;
    public Integer mBombId;

    //For testing
    LatLng testingBomb;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set URLs
        ClosestDistanceTask.mBaseUrl = DefuseBombTask.mBaseUrl = PlantBombTask.mBaseUrl = BASE_URL;
        ClosestDistanceTask.mService = DISTANCE_SERVICE;
        DefuseBombTask.mService = DEFUSE_SERVICE;
        PlantBombTask.mService = PLANT_SERVICE;

        //For testing
        mIsDefusingMode = false;
        mProfile = "Toto";
        //

        mDefuseButton = (Button) findViewById(R.id.defuseButton);

        mDefuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDefuseBomb = new DefuseBombTask(getBaseContext(), mProfile, mDefuseButton);
                mDefuseBomb.execute(mBombId);

                // For testing
                Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.cancel();
                mDefuseButton.setVisibility(View.INVISIBLE);
                //
            }
        });

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);

        // Handler for receiving messages from LocationService
        mLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Location", "Position received !");

                Double latitude = intent.getDoubleExtra("Lat", 0);
                Double longitude = intent.getDoubleExtra("Long", 0);

                if (mIsDefusingMode) {
                    mClosestDistance = new ClosestDistanceTask(getBaseContext(), mDefuseButton);

                    mClosestDistance.execute(latitude, longitude);
                    mCurrentLatLng = new LatLng(latitude, longitude);

                    // For testing
                    double distance = latLngToMeters(mCurrentLatLng, testingBomb);

                    Log.d("Distance", distance + "");

                    long[] closePattern = {0, 200, 1000};
                    long[] veryClosePattern = {0, 200, 500};
                    long[] defusePattern = {0, 50, 0};
                    Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (distance < 10) {
                        mVibrator.vibrate(defusePattern, 0);
                        mDefuseButton.setVisibility(View.VISIBLE);
                    } else if (distance < 50)
                        mVibrator.vibrate(veryClosePattern, 0);
                    else if (distance < 100)
                        mVibrator.vibrate(closePattern, 0);
                    //
                }
            }
        };

        // Handler for receiving messages from GameEventsService
        mGameEventsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        startService(new Intent(MapActivity.this, GameEventsService.class));
        startService(new Intent(MapActivity.this, LocationService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUpMapIfNeeded();
        stopService(new Intent(MapActivity.this, GameEventsService.class));
        stopService(new Intent(MapActivity.this, LocationService.class));

        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mLocationReceiver), new IntentFilter("Location"));
        LocalBroadcastManager.getInstance(this).registerReceiver((mGameEventsReceiver), new IntentFilter("GameEvents"));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver);
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
                if(!mIsDefusingMode) {
                    mPlantBomb = new PlantBombTask(getBaseContext(), mProfile);
                    mPlantBomb.execute(latLng.latitude, latLng.longitude);

                    //For testing
                    mIsDefusingMode = true;
                    testingBomb = latLng;
                    //
                }
            }
        });
    }

    // For testing
    // From http://stackoverflow.com/questions/639695/how-to-convert-latitude-or-longitude-to-meters
    private double latLngToMeters(LatLng p1, LatLng p2) {
        double latMid, m_per_deg_lat, m_per_deg_lon, deltaLat, deltaLon;

        latMid = (p1.latitude + p2.latitude) / 2.0;


        m_per_deg_lat = 111132.954 - 559.822 * Math.cos(2.0 * latMid) + 1.175 * Math.cos(4.0 * latMid);
        m_per_deg_lon = (3.14159265359/180 ) * 6367449 * Math.cos(latMid);

        deltaLat = Math.abs(p1.latitude - p2.latitude);
        deltaLon = Math.abs(p1.longitude - p2.longitude);

        return Math.sqrt(Math.pow(deltaLat * m_per_deg_lat, 2) + Math.pow(deltaLon * m_per_deg_lon , 2));
    }
    //
}
