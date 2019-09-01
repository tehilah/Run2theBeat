package com.example.run2thebeat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.widget.LinearLayout.VERTICAL;

public class FinishRunScreenActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tv_title;
    private TextView tv_avgPace;
    private TextView tv_duration;
    private TextView tv_km;
    private TextView tv_avgBpm;
    private ArrayList<LatLng> latLngs;
    public ArrayList<Song> selectedPlaylist = new ArrayList<>();
    private String TAG = "FinishPlaylistFragment";
    private SelectedPlaylistAdapter mAdapter;
    private RecyclerView playlistRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button savePlaylist;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private CollectionReference collectionUserRef;
    private CollectionReference collectionPlaylistRef;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private SupportMapFragment mMap;
    private AppBarLayout appBar;
    private CollapsingToolbarLayout c;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_run_screen);
        mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap.getMapAsync(this);//remember getMap() is deprecated!
        initVariables();
        setTextViews();
        selectedPlaylist = SongListFragment.selectedPlaylist;
        buildRecyclerView();

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isVisible = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    c.setTitleEnabled(true);
                    toolbar.setTitle(tv_title.getText().toString());
                    isVisible = true;
                } else if (isVisible) {
                    c.setTitleEnabled(false);
                    toolbar.setTitle("");
                    isVisible = false;
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        loadSavedRoute(googleMap);
    }

    private void loadSavedRoute(final GoogleMap googleMap) {
        ArrayList<Point> points = this.getIntent().getExtras().getParcelableArrayList("POINTS");
        latLngs = convertPointToLatlng(points);
        drawPolyline(googleMap, latLngs);
        if (latLngs.size() > 0) {
            moveCamera(latLngs.get(0), 17f, googleMap);
        }
    }

    private void moveCamera(LatLng latLng, float zoom, GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    public static void drawPolyline(GoogleMap googleMap, ArrayList<LatLng> latLngs) {
        PolylineOptions rectLine = new PolylineOptions().width(20)
                .color(Color.parseColor("#035aff"))
                .addAll(latLngs);
        googleMap.addPolyline(rectLine);

    }

    public static ArrayList<LatLng> convertPointToLatlng(ArrayList<Point> points) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Point point : points) {
            latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        return latLngs;
    }

    public void buildRecyclerView() {
        playlistRecyclerView = findViewById(R.id.play_list_recycler);
        playlistRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new SelectedPlaylistAdapter(selectedPlaylist);
        playlistRecyclerView.setAdapter(mAdapter);
        playlistRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(playlistRecyclerView.getContext(),
                VERTICAL);
        playlistRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter.setOnDeleteClickListener(new SelectedPlaylistAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                selectedPlaylist.remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        });
        Intent i = new Intent(this, NavigationBarActivity.class);
        savePlaylist = findViewById(R.id.save_playlist_button);
        savePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlaylist.size() > 0) {
                    getDialog(i);
                }else{
                    getWarningDialog();
                }
            }
        });

    }

    public void savePlaylist(Intent i, String name) {

        initUser();
        final Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final String theDate = formatter.format(date);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                String kmText = tv_km.getText().toString() + " km";
                PlaylistItem playlistItem = new PlaylistItem(selectedPlaylist, theDate, name, kmText);
                collectionPlaylistRef.add(playlistItem);

            }
        });

        StringBuffer responseText = new StringBuffer();
        responseText.append("Playlist saved");
        Toast.makeText(FinishRunScreenActivity.this, responseText, Toast.LENGTH_SHORT).show();
        startActivity(i);

    }

    private void getWarningDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        alert.setMessage("The playlist is empty");
        alert.setTitle("Failed to save");
        alert.setIcon(R.drawable.ic_warning_black_24dp);

        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    private void getDialog(Intent i) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
//        View v = findViewById(R.layout.edit_text_alert_dialog);
//        alert.setView(v);
        final EditText edittext = new EditText(this);
        alert.setMessage("Enter playlist name:");
        alert.setView(edittext);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String playlistName = edittext.getText().toString();
                savePlaylist(i, playlistName);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    private void initUser() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            collectionUserRef = db.collection(currentUser.getUid());
            collectionPlaylistRef = collectionUserRef.document("Document playlist").collection("Playlists");
        }
    }


    private void setTextViews() {
        Intent intent = getIntent();
        tv_title.setText(intent.getStringExtra("TITLE"));
        tv_avgPace.setText(intent.getStringExtra("AVG_PACE"));
        String avgBPM = intent.getIntExtra("AVG_BPM", 0) + "";
        tv_avgBpm.setText(avgBPM);
        tv_km.setText(intent.getStringExtra("KM"));
        tv_duration.setText(intent.getStringExtra("DURATION"));
    }

    private void initVariables() {
        tv_title = findViewById(R.id.title);
        tv_avgPace = findViewById(R.id.avg_pace);
        tv_duration = findViewById(R.id.duration);
        tv_km = findViewById(R.id.kilometers);
        tv_avgBpm = findViewById(R.id.avg_bpm);
        appBar = findViewById(R.id.appBar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        c = findViewById(R.id.collapsing_toolbar);
        c.setTitleEnabled(false);
    }


    public void finish(View view) {
        startActivity(new Intent(this, NavigationBarActivity.class));
        finish();
    }
}
