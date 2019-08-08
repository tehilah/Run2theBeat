package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class SongsOfAPlaylistActivity extends AppCompatActivity {
    private RecyclerView playlistsRecyclerView;
    private SelectedPlaylistAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Song> playlist = new ArrayList<Song>();
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_of_aplaylist);
        backButton = findViewById(R.id.back_to_playlists);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        playlist  = (ArrayList<Song>)getIntent().getSerializableExtra("selectedPlaylist");
        Log.d("TAG", "the playlist " +playlist);
        buildRecyclerView();

    }

    public void buildRecyclerView(){
        playlistsRecyclerView = findViewById(R.id.songs_of_playlists_recycler);
        playlistsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mAdapter = new SelectedPlaylistAdapter(playlist);
        playlistsRecyclerView.setAdapter(mAdapter);
        playlistsRecyclerView.setLayoutManager(layoutManager);
        mAdapter.setOnDeleteClickListener(new SelectedPlaylistAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                playlist.remove(position);
                mAdapter.notifyItemRemoved(position);

            }
        });
    }
}
