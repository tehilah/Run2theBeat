package com.example.run2thebeat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {
    // constants
    private static final float DEFAULT_ZOOM = 16f;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // vars
    private GoogleMap mMap;
    private Double distance = 0.0;
    private Boolean mLocationPermissionsGranted = false;
    private FirebaseFirestore db;
    private CollectionReference collectionUserRef;
    private CollectionReference collectionRouteRef;
    private ArrayList<Point> allPoints;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LatLng lastLocation;
    private LocationCallback mLocationCallback;
    private DateFormat df;
    private ExecutorService executor;
    private Route route;
    private Button mButtonStop;
    private Chronometer chronometer;
    private boolean mTimerRunning;
    private long pauseOffset;
    private TextView previewDist;
    private double prevDist = 0.0;
    private SensorManager sensorManager;
    private TextView tv_bpm;
    private TextView tv_avg_bpm;
    private TextView tv_avg_pace;
    private Sensor countSensor;
    private int stepsCounter = 0;
    private int curBPM;
    private ArrayList<Integer> stepsPerKm;
    private Button startBtn;
    private Button pauseBtn;
    private int sumBPM = 0;
    private int countBPM = 0; // save counter to calculate average bpm
    private double avgPace = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initVariables();
        getLocationPermission();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        initUser();
        getDeviceLocation();
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedTime = SystemClock.elapsedRealtime()
                        - chronometer.getBase();
                Log.d(TAG, "onChronometerTick: " + String.valueOf(elapsedTime));
                if ((elapsedTime % 60000 > 0) && (elapsedTime % 60000 <= 1000)) {
                    Log.d(TAG, "onChronometerTick: got it");
                    updateBPM();
                }
            }
        });
    }

    private void updatePace() {
        long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        avgPace = elapsedTime / (60*distance);
        tv_avg_pace.setText(String.valueOf(avgPace));
    }

    private void initVariables() {
        db = FirebaseFirestore.getInstance();
        executor = Executors.newCachedThreadPool();
        df = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        allPoints = new ArrayList<>();
        mButtonStop = findViewById(R.id.stop_button);
        chronometer = findViewById(R.id.chronometer);
        previewDist = findViewById(R.id.distance);
        tv_bpm = findViewById(R.id.bpm);
        tv_avg_bpm = findViewById(R.id.avg_bpm);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepsPerKm = new ArrayList<>();
        startBtn = findViewById(R.id.start_button);
        pauseBtn = findViewById(R.id.pause_button);
        tv_avg_pace = findViewById(R.id.avg_pace);
    }

    private void updateLocation() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(2000) // 2 seconds
                .setInterval(5000); // 5 seconds
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LatLng newLocation = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
                Log.d(TAG, "onLocationResult: \n " +
                        "lat: " + locationResult.getLastLocation().getLatitude() + "\n" +
                        "long: " + locationResult.getLastLocation().getLongitude());
                drawPolyline(newLocation);
//                moveCamera(newLocation, DEFAULT_ZOOM);
                if (mTimerRunning) {
                    if (updateDistance(newLocation)) { // if location really changed
                        lastLocation = newLocation;
                        savePoint(newLocation);
                        updatePace();
                    }
//                    updateBPM();
                }
            }
        };
        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, getMainLooper());

    }

    private void drawPolyline(LatLng newLocation) {
        PolylineOptions rectLine = new PolylineOptions().width(40).color(Color.BLUE).add(lastLocation, newLocation);
        mMap.addPolyline(rectLine);
        Log.d(TAG, "drawPolyline: added line");
    }

    private void initUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            collectionUserRef = db.collection(currentUser.getUid());
            collectionRouteRef = collectionUserRef.document("Document Routes").collection("Routes");
        }
    }

    private void getDeviceLocation() {

        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                mMap.setMyLocationEnabled(true);
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                LatLng loc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                lastLocation = loc;
                                moveCamera(loc,
                                        DEFAULT_ZOOM);
                                savePoint(loc);
                            } else {
                                Log.d(TAG, "onComplete: location is null");
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public boolean updateDistance(LatLng location) {
        double curDist = SphericalUtil.computeDistanceBetween(lastLocation, location);
        if (Math.abs(curDist - prevDist) >= 0.1) {
            distance += curDist;
            Toast.makeText(this, "Distance: " + FormatDateTimeDist.getDist(distance), Toast.LENGTH_LONG).show();
            previewDist.setText(FormatDateTimeDist.getDist(distance));
            return true;
        }
        return false;
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
                Log.d(TAG, "getLocationPermission: permission granted");
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Method: onPause");
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, countSensor);
        }
        // todo: figure out why this crashes the app
//        if (mFusedLocationProviderClient != null) {
//            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
//        }
    }

    private void savePoint(LatLng point) {
        allPoints.add(new Point(point.latitude, point.longitude));
    }

    public void startChronometer(View v) {
        if (!mTimerRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            mTimerRunning = true;
            mButtonStop.setVisibility(View.GONE);
            updateLocation();
            v.setEnabled(false);
            pauseBtn.setEnabled(true);
        }
    }

    private void updateBPM() {
        if (stepsCounter > 0) {
            tv_bpm.setText(String.valueOf(stepsCounter));
            curBPM = stepsCounter;
            stepsCounter = 0;
            Toast.makeText(this, "one minute passed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "updateBPM: cur bpm " + String.valueOf(curBPM));
            Log.d(TAG, "updateBPM: average1 bpm " + String.valueOf(sumBPM));
            sumBPM += curBPM;
            countBPM++;
            int curAvgBpm = sumBPM / countBPM;
            tv_avg_bpm.setText(String.valueOf(curAvgBpm));
            Log.d(TAG, "updateBPM: average2 bpm " + String.valueOf(curAvgBpm));

        }
    }

    public void pauseChronometer(View v) {
        if (mTimerRunning) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            mTimerRunning = false;
            mButtonStop.setVisibility(View.VISIBLE);
        }
        v.setEnabled(false);
        startBtn.setEnabled(true);
    }

    public void stopChronometer(View v) {
        Log.d(TAG, "Method: stopChronometer");
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        final String timestamp = df.format(new Date());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int bpm = countBPM == 0 ? 0 : sumBPM / countBPM; // check in case the user did not move and he tries to save the map. we avoid dividing by zero
                route = new Route(timestamp, allPoints,
                        FormatDateTimeDist.getTimeOfDay(),
                        FormatDateTimeDist.getTime(pauseOffset),
                        FormatDateTimeDist.getDist(distance),
                        bpm,
                        String.valueOf(avgPace));
                collectionRouteRef.add(route);
                pauseOffset = 0;
            }

        });
        chronometer.setBase(SystemClock.elapsedRealtime());
        Toast.makeText(MapsActivity.this, "map saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Method: onResume");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Method: onStop:");
        super.onStop();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, countSensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepsCounter++;
        }
//        if(pauseOffset%60 == 0){
//            Toast.makeText(this, "one minute passed", Toast.LENGTH_SHORT).show();
//            tv_bpm.setText(String.valueOf(stepsCounter));
//            curBPM = stepsCounter;
//            stepsCounter = 0;
//        }
        Log.d(TAG, "onSensorChanged: steps: " + String.valueOf(stepsCounter));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


//todo: in order to use this app in background I need to save all the parameters (elapsed time, distance, steps) in a shared pointer and then load them in onResume. or look into using a Service