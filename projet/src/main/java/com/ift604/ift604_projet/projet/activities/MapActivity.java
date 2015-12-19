package com.ift604.ift604_projet.projet.activities;

import android.content.*;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.ift604.ift604_projet.projet.R;
import com.ift604.ift604_projet.projet.services.CommunicationService;
import com.ift604.ift604_projet.projet.services.GameEventsService;
import com.ift604.ift604_projet.projet.services.LocationService;

public class MapActivity extends FragmentActivity {
    private static final long[] closePattern = { 0, 200, 1000 };
    private static final long[] veryClosePattern = { 0, 200, 500 };
    private static final long[] defusePattern = { 0, 50, 0 };

    public enum State {
        NotStarted,
        Placing,
        WaitingForDefuse,
        Defusing,
        Completed
    }

    private Vibrator mVibrator;
    private GoogleMap mMap;
    private BroadcastReceiver mLocationReceiver;
    private BroadcastReceiver mGameEventsReceiver;
    private BroadcastReceiver mBombIdReceiver;
    private LocalBroadcastManager mBroadcaster;
    private Button mDefuseButton;
    private Button mStateButton;
    private State mState;
    public Integer mBombId;

    private CommunicationService boundService;
    private boolean isServiceBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            boundService = ((CommunicationService.CommunicationBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            boundService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, CommunicationService.class), connection, Context.BIND_AUTO_CREATE);
        isServiceBound = true;
    }

    void doUnbindService() {
        if (isServiceBound) {
            // Detach our existing connection.
            unbindService(connection);
            isServiceBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        doBindService();

        mBroadcaster = LocalBroadcastManager.getInstance(this);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mDefuseButton = (Button) findViewById(R.id.defuseButton);
        mStateButton = (Button) findViewById(R.id.stateButton);

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);

        mDefuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDefused = boundService.DefuseBomb(mBombId);

                Toast msg;
                if (isDefused) {
                    msg = Toast.makeText(MapActivity.this, "Bomb defused !", Toast.LENGTH_LONG);
                    mDefuseButton.setVisibility(View.INVISIBLE);
                    mVibrator.cancel();
                } else {
                    msg = Toast.makeText(MapActivity.this, "Bomb not defused !", Toast.LENGTH_LONG);
                }

                msg.show();
            }
        });

        // Handler for receiving messages from LocationService
        mLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Location", "Position received !");

                Double latitude = intent.getDoubleExtra("Lat", 0);
                Double longitude = intent.getDoubleExtra("Long", 0);

                if (mState == State.Defusing) {
                    Pair<Integer, Double> bombIdDistance = boundService.ClosestBomb(latitude, longitude);
                    Integer bombId = bombIdDistance.first;
                    Double distance = bombIdDistance.second;

                    if(bombId != -1) {
                        Intent newIntent = new Intent("Distance");
                        newIntent.putExtra("BombId", bombId);

                        mBroadcaster.sendBroadcast(newIntent);

                        if (distance < 10) {
                            mVibrator.vibrate(defusePattern, 0);
                            mDefuseButton.setVisibility(View.VISIBLE);
                        } else if (distance < 50)
                            mVibrator.vibrate(veryClosePattern, 0);
                        else if (distance < 100)
                            mVibrator.vibrate(closePattern, 0);
                    }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        startService(new Intent(MapActivity.this, GameEventsService.class).putExtra("username", boundService.GetUsername()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUpMapIfNeeded();
        stopService(new Intent(MapActivity.this, GameEventsService.class));
        stopService(new Intent(MapActivity.this, LocationService.class));

        mVibrator.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBroadcaster.registerReceiver((mLocationReceiver), new IntentFilter("Location"));
        mBroadcaster.registerReceiver((mGameEventsReceiver), new IntentFilter("GameEvents"));
        mBroadcaster.registerReceiver((mBombIdReceiver), new IntentFilter("Distance"));
    }

    @Override
    protected void onStop() {
        mBroadcaster.unregisterReceiver(mLocationReceiver);
        mBroadcaster.unregisterReceiver(mGameEventsReceiver);
        mBroadcaster.unregisterReceiver(mBombIdReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
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
                    boolean isPlanted = boundService.PlantBomb(latLng.latitude, latLng.longitude);
                    Toast msg;

                    if (isPlanted)
                        msg = Toast.makeText(MapActivity.this, "Bomb planted !", Toast.LENGTH_LONG);
                    else
                        msg = Toast.makeText(MapActivity.this, "Bomb not planted !", Toast.LENGTH_LONG);

                    msg.show();
                }
            }
        });
    }
}
