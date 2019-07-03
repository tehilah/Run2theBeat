package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;


public class RunDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment mMap;
    private LinearLayout linlaHeaderProgress;
    private ArrayList<LatLng> latLngs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_run_details);
        setProgressBarIndeterminateVisibility(true);
        linlaHeaderProgress = findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap.getMapAsync(this);//remember getMap() is deprecated!
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        loadSavedRoute(googleMap);
    }

    private void drawPolyline(GoogleMap googleMap) {
        PolylineOptions rectLine = new PolylineOptions().width(20)
                .color(Color.parseColor("#fd9771"))
                .addAll(latLngs);
        googleMap.addPolyline(rectLine);

    }

    private void convertPointToLatlng(ArrayList<Point> points) {
        latLngs = new ArrayList<>();
        for (Point point : points) {
            latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
    }

    private void loadSavedRoute(final GoogleMap googleMap) {
        ArrayList<Point> points = this.getIntent().getExtras().getParcelableArrayList("POINTS");
        convertPointToLatlng(points);
        drawPolyline(googleMap);
        moveCamera(latLngs.get(0), 17f, googleMap);
        linlaHeaderProgress.setVisibility(View.GONE);
    }

    private void moveCamera(LatLng latLng, float zoom, GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

}
