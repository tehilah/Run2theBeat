package com.example.run2thebeat;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;

import static com.example.run2thebeat.MusicListActivity.PREFS_NAME;

public class SongListFragment extends Fragment {

    private String TAG = "SongListFragment";
    public ArrayList<Song> songList = new ArrayList<>();
    public static ArrayList<Song> selectedPlaylist = new ArrayList<>();
    private RecyclerView songRecyclerView;
    private SongListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentlyPlayingPosition = 1;
    private TextView tv_artist;


    private static ArrayList<Song> allSongsList = new ArrayList<Song>();
    private static Song popNum1 = new Song(1, "I Dont Care", "Ed Sheeran & Justin Bieber ", "pop", "Ed Sheeran & Justin Bieber I Dont Care (Official Audio).mp3");
    private static Song popNum2 = new Song(2, "Faith", "Stevie Wonder ft. Ariana Grande", "pop", "Stevie Wonder - Faith ft. Ariana Grande.mp3");
    private static Song popNum3 = new Song(3, "Bananas", " static and benel", "Pop", "סטטיק ובן אל תבורי - בננות (Prod. By Jordi).mp3");
    private static Song countryNum1 = new Song(4, "Before He Cheats", "Carrie Underwood", "country", "Carrie Underwood - Before He Cheats.mp3");
    private static Song countryNum2 = new Song(5, "Heartache On The Dance Floor", "Jon Pardi", "country", "Jon Pardi - Heartache On The Dance Floor (Audio).mp3");


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "getSongList: created");
        createSongList();
        getSongList();
        buildRecyclerView(view);
        playSong(1);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.reset();
            }
        });

    }


    public void createSongList() {
        allSongsList.add(popNum1);
        allSongsList.add(popNum2);
        allSongsList.add(popNum3);
        allSongsList.add(countryNum1);
        allSongsList.add(countryNum2);
    }

    public void getSongList() {
        //retrieve the audio file information
        songList.clear();
        songList.add(new Song(000,"","","","")); //currently playing song. set to nothing at first
        ArrayList<String> selectedGenres = (ArrayList<String>) getActivity().getIntent().getSerializableExtra("genres");

        if(selectedGenres.size() ==0) {//no generes selected
            for (int j = 0; j < allSongsList.size(); j++) {
                Song song = allSongsList.get(j);
                if (!songList.contains(song)) {
                    songList.add(song);
                }
            }
        }
        else {
            for (int i = 0; i < selectedGenres.size(); i++) {
                String genere = selectedGenres.get(i);
                for (int j = 0; j < allSongsList.size(); j++) {
                    Song song = allSongsList.get(j);
                    if (song.getGenre().equals(genere)) {
                        if (!songList.contains(song)) {
                            songList.add(song);
                        }

                    }
                }
            }
        }

        if(songList.size()>1){ //we set the first song in the list to be the first song from songList
            //because we are going to play it right away
            songList.set(0,songList.get(1));
        }

        selectedPlaylist = songList;
    }


    public void buildRecyclerView(final View view) {
        songRecyclerView = view.findViewById(R.id.list);
        songRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new SongListAdapter(songList);
        songRecyclerView.setAdapter(mAdapter);
        songRecyclerView.setLayoutManager(layoutManager);
        mAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                playSong(position);
            }
        });

        mAdapter.setOnPlayClickListener(new SongListAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick() {
                playOrPause(view);
            }
        });

        mAdapter.setOnNextClickListener(new SongListAdapter.OnNextClickListener() {
            @Override
            public void onNextClick() {
                playSong(currentlyPlayingPosition + 1);
            }
        });
        mAdapter.setOnPreviousClickListener(new SongListAdapter.OnPrviousClickListener() {
            @Override
            public void onPreviousClick() {
                playSong(currentlyPlayingPosition - 1);
            }
        });

    }

    public void playSong(final int position) {
        if (position <= 0 || position >= songList.size()) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();
        Song song = songList.get(position);

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child(song.getGenre()).child(song.getFullName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    mediaPlayer.setDataSource(uri.toString());
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mediaPlayer.prepareAsync();
                    swapItem(position);
                    currentlyPlayingPosition = position;
                } catch (IOException o) {
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.reset();
                if (position < songList.size() - 1) {
                    playSong(position + 1);
                }

            }
        });

    }

    public void swapItem(int position) {
        Song nowPlaying = songList.get(position);
        songList.set(0, nowPlaying);
        ArrayList<Song> newList = new ArrayList<Song>();
        newList.addAll(songList);
        songList.clear();
        songList.addAll(newList);
        mAdapter.notifyDataSetChanged();
    }


    public void playOrPause(View view) {
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.play_pause);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imageButton.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            imageButton.setImageResource(R.drawable.ic_pause);
        }

    }


    //todo: might be able to delete both these methods

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
            }
        });
    }
}
