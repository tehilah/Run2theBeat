package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment mMap;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private CollectionReference routeRef;
    private LinearLayout linlaHeaderProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_run_details);
        setProgressBarIndeterminateVisibility(true);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap.getMapAsync(this);//remember getMap() is deprecated!
        initializeCollectionRefs();
    }

    private void initializeCollectionRefs() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            routeRef =  db.collection(currentUser.getUid())
                    .document("Document Routes")
                    .collection("Routes")
                    .document("01 Jul 2019, 15:20:41").collection("Points");

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        googleMap.setMyLocationEnabled(true);
        loadSavedRoute(googleMap);
    }

    private void drawPolyline(ArrayList<LatLng> allPoints, GoogleMap googleMap) {
        PolylineOptions rectLine = new PolylineOptions().width(20).color(Color.parseColor("#fd9771")).addAll(allPoints);
        googleMap.addPolyline(rectLine);

    }

    private void loadSavedRoute(final GoogleMap googleMap) {
        final ArrayList<LatLng> allPoints = new ArrayList<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                routeRef.orderBy("id", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                            Map<String, Object> location = query.getData();
                            Double latitude = Double.valueOf(location.get("latitude").toString());
                            Double longitude = Double.valueOf(location.get("longitude").toString());
                            LatLng latLng = new LatLng(latitude, longitude);
                            allPoints.add(latLng);
                        }
                        drawPolyline(allPoints, googleMap);
                        moveCamera(allPoints.get(0), 17f, googleMap);
                        linlaHeaderProgress.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom, GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}
