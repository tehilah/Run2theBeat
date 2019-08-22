package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import java.util.ArrayList;

public class SongsOfAPlaylistActivity extends AppCompatActivity {
    private RecyclerView playlistsRecyclerView;
    private SelectedPlaylistAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Song> playlist = new ArrayList<Song>();
    private PlaylistItem playlistItem;
    private ImageButton backButton;
    private Button startRunButton;
    private String docName;
    private String TAG = "SongsOfPlaylistActivity";

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
        playlistItem = (PlaylistItem)getIntent().getSerializableExtra("selectedPlaylist");
        playlist  = playlistItem.mPlayList;
        docName = getIntent().getStringExtra("docName");
        buildRecyclerView();

        startRunButton = findViewById(R.id.choose_new_playlist);
        Intent intent = new Intent(this,RunActivity.class);
        startRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("playlist",playlist);
                startActivity(intent);

            }
        });


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
                playlistItem.setSongsList(playlist);
                deleteSongFromFirebase();

            }
        });
    }

    public void deleteSongFromFirebase(){
        ShowPlaylistsFragment.collectionPlaylistRef.document(docName).update("mPlayList",playlist);
        ShowPlaylistsFragment.playlistItemMutableLiveData.postValue(playlistItem);
    }
}
