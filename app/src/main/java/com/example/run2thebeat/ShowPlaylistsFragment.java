package com.example.run2thebeat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.run2thebeat.ProgressFragment.CONFIRM_DELETE;

public class ShowPlaylistsFragment extends Fragment {
    private String TAG  = "ShowPlaylistFragment";
    public ArrayList<PlaylistItem> playlistsList = new ArrayList<PlaylistItem>();
    public ArrayList<String> namesOfDocs = new ArrayList<String>();
    private RecyclerView playlistsRecyclerView;
    private PlaylistAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static FirebaseAuth mAuth;
    public static CollectionReference collectionUserRef;
    public static CollectionReference collectionPlaylistRef;
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static FirebaseUser currentUser;
    private ExecutorService executor = Executors.newCachedThreadPool();
    public static MutableLiveData<PlaylistItem> playlistItemMutableLiveData;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getPlaylists(view);

    }

    public void getPlaylists( View view){
        initUser();
        collectionPlaylistRef.get().addOnCompleteListener((task) ->{
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String docName ="";
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())){
                            PlaylistItem playlistItem = doc.toObject(PlaylistItem.class);
                            docName = doc.getId();
                            namesOfDocs.add(docName);
                            playlistsList.add(playlistItem);
                        }

                        if(playlistsList.size() ==0){
                            TextView textView = view.findViewById(R.id.no_saved_playlists);
                            textView.setVisibility(View.VISIBLE);
                        }

                        mHandler.post(new Runnable(){
                            @Override
                            public void run(){
                                buildRecyclerView(view);
                            }
                        });
                    }
                    else{
                        Log.d("TAG", "Error getting Documents: ", task.getException());
                    }
                }
            });

        });
        Log.d(TAG,"the size " +playlistsList.size());
    }

    public void buildRecyclerView(View v){
        playlistsRecyclerView = v.findViewById(R.id.playlists_recycler);
        playlistsRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext() ,2, LinearLayoutManager.HORIZONTAL,false);
        mAdapter = new PlaylistAdapter(playlistsList);
        playlistsRecyclerView.setAdapter(mAdapter);
        playlistsRecyclerView.setLayoutManager(layoutManager);

        mAdapter.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
            Context c = getContext();
            final Intent intent = new Intent(mContext,SongsOfAPlaylistActivity.class);
            @Override
            public void onItemClick(int position) {
                playlistItemMutableLiveData = new MutableLiveData<PlaylistItem>();
                intent.putExtra("selectedPlaylist",playlistsList.get(position));
                intent.putExtra("docName", namesOfDocs.get(position));
                startActivity(intent);
                playlistItemMutableLiveData.observe(getViewLifecycleOwner(), new Observer<PlaylistItem>() {
                    @Override
                    public void onChanged(PlaylistItem playlistItem) {
                        playlistsList.set(position,playlistItem);
                    }
                });
            }

            @Override
            public void onDeleteClick(int position) {
                getDialog(position);

            }
        });
    }

    public void getDialog(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        alertDialogBuilder
                .setMessage(CONFIRM_DELETE)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, delete message
                        playlistsList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        collectionPlaylistRef.document(namesOfDocs.get(position)).delete();
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

    public static void initUser() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            collectionUserRef = db.collection(currentUser.getUid());
            collectionPlaylistRef = collectionUserRef.document("Document playlist").collection("Playlists");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
