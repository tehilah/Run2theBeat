package com.example.run2thebeat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{
    // constants
    private static final float DEFAULT_ZOOM = 16f;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    static final long DETECTION_INTERVAL_IN_MILLISECONDS = 30 * 1000;
    public static final int CONFIDENCE = 70;

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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        initVariables();
        initUser();
        getDeviceLocation();
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


    }

    private void updateLocation() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(2000)
                .setInterval(10000);
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
                    updateDistance(newLocation);
                }
                lastLocation = newLocation;
                savePoint(newLocation);

            }
        };
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, getMainLooper());
    }

    private void drawPolyline(LatLng newLocation) {
        PolylineOptions rectLine = new PolylineOptions().width(40).color(Color.BLUE).add(lastLocation, newLocation);
        mMap.addPolyline(rectLine);
        Log.d(TAG, "drawPolyline: added line");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        getLocationPermission();

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

    public void updateDistance(LatLng location) {
        distance += SphericalUtil.computeDistanceBetween(lastLocation, location);
        Toast.makeText(this, "Distance: " + FormatDateTimeDist.getDist(distance), Toast.LENGTH_LONG).show();

// Location.distanceBetween(
//                lastLocation.latitude,
//                lastLocation.longitude,
//                location.latitude,
//                location.longitude,
//                results);
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
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
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
            previewDist.setText(FormatDateTimeDist.getDist(distance));
        }
    }

    public void pauseChronometer(View v) {
        if (mTimerRunning) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            mTimerRunning = false;
        }
        mButtonStop.setVisibility(View.VISIBLE);
    }

    public void stopChronometer(View v) {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        final String timestamp = df.format(new Date());
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                String dist = String.format(Locale.getDefault(), "%d.",
                route = new Route(timestamp, allPoints, FormatDateTimeDist.getTimeOfDay(),
                        FormatDateTimeDist.getTime(pauseOffset), FormatDateTimeDist.getDist(distance));
                collectionRouteRef.add(route);
                pauseOffset = 0;
            }

        });
        chronometer.setBase(SystemClock.elapsedRealtime());
        Toast.makeText(MapsActivity.this, "map saved", Toast.LENGTH_SHORT).show();
    }


}
