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
    private static final String BASE_URL = "http://192.168.0.64/IFT604/";
    private static final String DISTANCE_SERVICE = "Bomb/ClosestDistance?lattitude=%f&longitude=%f&username=%s";
    private static final String DEFUSE_SERVICE = "Bomb/Defuse?bombId=%d&username=%s";
    private static final String PLANT_SERVICE = "Bomb/Plant?lattitude=%f&longitude=%f&username=%s";
    private static final String EVENTS_SERVICE = "Events/State?username=%s";

    public enum State {
        NotStarted,
        Placing,
        WaitingForDefuse,
        Defusing,
        Completed
    }

    private GoogleMap mMap;
    private BroadcastReceiver mLocationReceiver;
    private BroadcastReceiver mGameEventsReceiver;
    private BroadcastReceiver mBombIdReceiver;
    private ClosestDistanceTask mClosestDistance;
    private PlantBombTask mPlantBomb;
    private DefuseBombTask mDefuseBomb;
    private Button mDefuseButton;
    private State mState;
    private String mUsername;
    private Button mStateButton;
    public Integer mBombId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set URLs
        ClosestDistanceTask.mBaseUrl = DefuseBombTask.mBaseUrl = PlantBombTask.mBaseUrl = GameEventsService.mBaseUrl = BASE_URL;
        ClosestDistanceTask.mService = DISTANCE_SERVICE;
        DefuseBombTask.mService = DEFUSE_SERVICE;
        PlantBombTask.mService = PLANT_SERVICE;
        GameEventsService.mService = EVENTS_SERVICE;

        ///For testing
        mUsername = "Toto";
        //

        /*Bundle b = getIntent().getExtras();
        if(b != null) {
            mUsername = b.getString("username");*/

            mDefuseButton = (Button) findViewById(R.id.defuseButton);
            mStateButton = (Button) findViewById(R.id.stateButton);

            mDefuseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDefuseBomb = new DefuseBombTask(getBaseContext(), mDefuseButton, mUsername);
                    mDefuseBomb.execute(mBombId);
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

                    if (mState == State.Defusing) {
                        mClosestDistance = new ClosestDistanceTask(getBaseContext(), mDefuseButton, mUsername);

                        mClosestDistance.execute(latitude, longitude);
                    }
                }
            };

            // Handler for receiving messages from GameEventsService
            mGameEventsReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("Events", "State received !");

                    switch (intent.getIntExtra("State", 0)) {
                        case 0:
                            mState = State.NotStarted;
                            mStateButton.setText("Not started");
                            break;
                        case 1:
                            mState = State.Placing;
                            mStateButton.setText("Placing");
                            break;
                        case 2:
                            mState = State.WaitingForDefuse;
                            mStateButton.setText("Waiting for defuse");
                            break;
                        case 3:
                            mState = State.Defusing;
                            mStateButton.setText("Defusing");
                            break;
                        case 4:
                            mState = State.Completed;
                            mStateButton.setText("Completed");
                            break;
                        default:
                            break;
                    }
                }
            };

            mBombIdReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("Distance", "BombId received !");

                    mBombId = intent.getIntExtra("BombId", 0);
                }
            };
       // }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        if(mUsername != null)
            startService(new Intent(MapActivity.this, GameEventsService.class).putExtra("username", mUsername));
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
        LocalBroadcastManager.getInstance(this).registerReceiver((mBombIdReceiver), new IntentFilter("Distance"));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGameEventsReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBombIdReceiver);
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
                Log.d("Map", "LongClick");
                if(mState == State.Placing) {
                    mPlantBomb = new PlantBombTask(getBaseContext(), mUsername);
                    mPlantBomb.execute(latLng.latitude, latLng.longitude);
                }
            }
        });
    }
}
