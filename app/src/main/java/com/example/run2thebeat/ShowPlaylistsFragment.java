package com.example.run2thebeat;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShowPlaylistsFragment extends Fragment {
    private String TAG  = "ShowPlaylistFragment";
    public ArrayList<PlaylistItem> playlistsList = new ArrayList<PlaylistItem>();
    private RecyclerView playlistsRecyclerView;
    private PlaylistAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    private CollectionReference collectionUserRef;
    private CollectionReference collectionPlaylistRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ExecutorService executor = Executors.newCachedThreadPool();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getPlaylists(view);
        buildRecyclerView(view);

    }

    public void getPlaylists( View view){
        initUser();
        collectionPlaylistRef.get().addOnCompleteListener((task) ->{
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())){
                            Map<String,Object> map = doc.getData();
                            ArrayList<Song> songList = (ArrayList<Song>) map.get("mPlayList");
                            String mDate = (String) map.get("mDate");
                            PlaylistItem playlistItem = new PlaylistItem(songList,mDate);
                            playlistsList.add(playlistItem);
                        }
                        buildRecyclerView(view);
                    }
                    else{
                        Log.d("TAG", "Error getting Documents: ", task.getException());
                    }
//                }
//            });
            Log.d(TAG, "PLAYLIST SIZE "+ playlistsList.size());

        });
        Log.d(TAG, "PLAYLIST SIZE "+ playlistsList.size());
    }

    public void buildRecyclerView(View v){
        playlistsRecyclerView = v.findViewById(R.id.playlists_recycler);
        playlistsRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext() ,2, LinearLayoutManager.HORIZONTAL,false);
        mAdapter = new PlaylistAdapter(playlistsList);
        playlistsRecyclerView.setAdapter(mAdapter);
        playlistsRecyclerView.setLayoutManager(layoutManager);

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
