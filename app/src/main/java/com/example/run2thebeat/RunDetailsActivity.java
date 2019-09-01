package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.example.run2thebeat.ProgressFragment.CONFIRM_DELETE;


public class RunDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment mMap;
    private ArrayList<LatLng> latLngs;
    private TextView duration;
    private TextView distance;
    private TextView avgBPM;
    private TextView title;
    private TextView avgPace;
    private String documentRef;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_run_details);
        setProgressBarIndeterminateVisibility(true);
        mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap.getMapAsync(this);//remember getMap() is deprecated!
        duration = findViewById(R.id.duration);
        distance = findViewById(R.id.kilometers);
        avgBPM = findViewById(R.id.avg_bpm);
        avgPace = findViewById(R.id.avg_pace);
        title = findViewById(R.id.title);
        db = FirebaseFirestore.getInstance();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.silver_map));
        loadSavedRoute(googleMap);
    }

    private void drawPolyline(GoogleMap googleMap) {
        PolylineOptions rectLine = new PolylineOptions().width(20)
                .color(Color.parseColor("#035aff"))
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
        String dur = this.getIntent().getExtras().getString("DURATION");
        String dist = this.getIntent().getExtras().getString("DISTANCE");
        String pace = this.getIntent().getExtras().getString("AVG_PACE");
        String date = this.getIntent().getExtras().getString("DATE");
        documentRef = this.getIntent().getExtras().getString("DOC_REF");
        int bpm = this.getIntent().getExtras().getInt("AVG_BPM");
        duration.setText(dur);
        distance.setText(dist);
        avgBPM.setText(String.valueOf(bpm));
        avgPace.setText(pace);
        title.setText(date);
        convertPointToLatlng(points);
        drawPolyline(googleMap);
        if (latLngs.size() != 0) {
            moveCamera(latLngs.get(0), 17f, googleMap);
        }

    }

    private void moveCamera(LatLng latLng, float zoom, GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void deleteRun(View view) {
        getDialog();
    }


    /**
     * Asynchronous class for deleting messages
     */
    private static class DeleteMessageAsyncTask extends AsyncTask<DocumentReference, Void, Void> {
        @Override
        protected Void doInBackground(DocumentReference... references) {
            references[0].delete();
            return null;
        }
    }

    public void getDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        alertDialogBuilder
                .setMessage(CONFIRM_DELETE)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, delete message
                        DocumentReference itemRef = db.document(documentRef);
                        new DeleteMessageAsyncTask().execute(itemRef);
                        finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close the dialog box and do nothing
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
