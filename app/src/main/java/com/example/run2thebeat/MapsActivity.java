package com.example.run2thebeat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    // constants
    private static final float DEFAULT_ZOOM = 16f;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    // vars
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionUserRef;
    private CollectionReference collectionRouteRef;
    private Button mSaveBtn;
    private LocationManager locationManager;
    private ArrayList<HashMap> allPoints = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LatLng lastLocation;
    private LocationCallback mLocationCallback;
    private DateFormat df = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
    private ExecutorService executor = Executors.newCachedThreadPool();


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        df.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        if (mLocationPermissionsGranted) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    COURSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission was not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            mMap.setMyLocationEnabled(true);
            updateLocation();
        }
    }

    private void updateLocation() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(2000)
                .setInterval(4000);
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
                moveCamera(newLocation, DEFAULT_ZOOM);
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
        getUser();
    }

    private void getUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            collectionUserRef =  db.collection(currentUser.getUid());
            collectionRouteRef = collectionUserRef.document("Document Routes").collection("Routes");
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

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
                                Log.d(TAG, "onComplete: location exists");
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
        HashMap<String, Object> location = new HashMap<>();
        String timestamp = df.format(new Date());
        location.put("latitude", point.latitude);
        location.put("longitude", point.longitude);
        location.put("id", timestamp);
        allPoints.add(location);
        Toast.makeText(MapsActivity.this, "saved point", Toast.LENGTH_SHORT).show();
    }


    public void saveMap(View view) {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        final String timestamp = df.format(new Date());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CollectionReference collectionPointsRef = collectionRouteRef
                        .document(timestamp)
                        .collection("Points");
                for (HashMap map : allPoints) {
                    collectionPointsRef.add(map);
                }
            }

        });
        Toast.makeText(MapsActivity.this, "map saved", Toast.LENGTH_SHORT).show();
    }
}
