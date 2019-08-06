package com.example.run2thebeat;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FinishPlaylistFragment extends Fragment {
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
    public static MediaPlayer mediaPlayer  = new MediaPlayer();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finish_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        selectedPlaylist = (ArrayList<Song>)getActivity().getIntent().getSerializableExtra("selectedPlaylist");
        selectedPlaylist.remove(0); //remove currently playing song
        buildRecyclerView(view);

    }



    public void buildRecyclerView(final View view){
        playlistRecyclerView = view.findViewById(R.id.play_list_recycler);
        playlistRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new SelectedPlaylistAdapter(selectedPlaylist);
        playlistRecyclerView.setAdapter(mAdapter);
        playlistRecyclerView.setLayoutManager(layoutManager);

        mAdapter.setOnDeleteClickListener(new SelectedPlaylistAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                selectedPlaylist.remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        });
        savePlaylist = view.findViewById(R.id.save_playlist_button);
        savePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUser();
                final Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final String theDate =  formatter.format(date);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        PlaylistItem playlistItem = new PlaylistItem(selectedPlaylist,theDate);
                        collectionPlaylistRef.add(playlistItem);

                    }
                });
                StringBuffer responseText = new StringBuffer();
                responseText.append("Playlist saved");
                Toast.makeText(getContext(),responseText,Toast.LENGTH_LONG).show();
            }
        });

    }


    private void initUser() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            collectionUserRef = db.collection(currentUser.getUid());
            collectionPlaylistRef = collectionUserRef.document("Document playlist").collection("Playlists");
        }
    }

}
