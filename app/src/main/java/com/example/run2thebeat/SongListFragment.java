package com.example.run2thebeat;

import java.util.ArrayList;
import java.util.Collections;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.ImageButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import android.widget.SeekBar;
import android.widget.Toast;


public class SongListFragment extends Fragment {

    private static String TAG = "SongListFragment";
    public static ArrayList<Song> songList;
    public static ArrayList<Song> selectedPlaylist;
    private static SongListAdapter mAdapter;
    public static int currentlyPlayingPosition = 1;
    private static boolean isAdded;
    public int nextToPlay = 0;
    public static MutableLiveData<Integer> curBPMLiveData;
    public ImageButton imageButton;
    private static boolean isPlaying;
    //    Binding variables
    private static PlayerService mBoundService;
    private boolean mIsBound;
    private MyListener mListener;
    // --- seek bar variables ---
    private static SeekBar seekBar;
    private Intent seekbarIntent;
    private RecyclerView songRecyclerView;
    public static final String BROADCAST_SEEKBAR = "com.example.run2thebeat.SongListFragment.BROADCAST_SEEKBAR";


    private static ArrayList<Song> allSongsList = new ArrayList<Song>();
    private static Song popNum1 = new Song(1, "I Dont Care", "Ed Sheeran & Justin Bieber ", "pop", "Ed Sheeran & Justin Bieber I Dont Care (Official Audio).mp3", 102, R.drawable.i_dont_care);
    private static Song popNum2 = new Song(2, "Faith", "Stevie Wonder ft. Ariana Grande", "pop", "Stevie Wonder - Faith ft. Ariana Grande.mp3", 158, R.drawable.faith);
    private static Song popNum3 = new Song(3, "Bananas", "Static and Benel", "pop", "סטטיק ובן אל תבורי - בננות (Prod. By Jordi).mp3", 124, R.drawable.bananas);
    private static Song countryNum1 = new Song(4, "Before He Cheats", "Carrie Underwood", "country", "Carrie Underwood - Before He Cheats.mp3", 148, R.drawable.before_he_cheats);
    private static Song countryNum2 = new Song(5, "Heartache On The Dance Floor", "Jon Pardi", "country", "Jon Pardi - Heartache On The Dance Floor (Audio).mp3", 116, R.drawable.heartache_on_the_dancefloor);
    private static Song popNum4 = new Song(6, "Counting Stars", "OneRepublic", "pop", "OneRepublic - Counting Stars.mp3", 122, R.drawable.counting_stars);
    private static Song popNum5 = new Song(7, "Can't Hold Us", "Macklemore", "pop", "Can't Hold Us - Macklemore .mp3", 146, R.drawable.cant_hold_us);
    private static Song latin1 = new Song(8, "Mia", "Bad Bunny ft. Drake", "latin", "Mia.mp3", 97, R.drawable.mia);
    private static Song latin2 = new Song(9, "Calma", "Pedro Capo, Farruko", "latin", "Con Calma.mp3", 127, R.drawable.con_calma);
    private static Song rock1 = new Song(10, "Paint It Black", "The Rolling Stones", "rock", "The Rolling Stones - Paint It, Black.mp3", 160, R.drawable.bob_marley);
    private static Song rock2 = new Song(11, "For Reasons Unknown", "The Killers", "rock", "For Reasons Unknown - The Killers.mp3", 140, R.drawable.bob_marley);
    private static Song rock3 = new Song(12, "The Pretender", "Foo Fighters", "rock", "Foo Fighters - The Pretender.mp3", 170, R.drawable.bob_marley);
    private static Song rock4 = new Song(13, "Wanted Dead Or Alive", "Bon Jovi", "rock", "Bon Jovi - Wanted Dead Or Alive.mp3", 150, R.drawable.bob_marley);
    private static Song rock5 = new Song(14, "Eye Of The Tiger", "Survivor", "rock", "Survivor - Eye Of The Tiger.mp3", 109, R.drawable.bob_marley);
    private static Song popNum6 = new Song(15, "We Found Love", "Rihanna", "pop", "Rihanna - We Found Love.mp3", 130, R.drawable.bob_marley);
    private static Song popNum7 = new Song(16, "Girls Just Want To Have Fun", "Cyndi Lauper", "pop", "Cyndi Lauper - Girls Just Want To Have Fun.mp3", 120, R.drawable.bob_marley);
    private static Song popNum8 = new Song(17, "Take Back The Night", "Justin Timberlake", "pop", "Justin Timberlake - Take Back The Night.mp3", 109, R.drawable.bob_marley);


    public interface MyListener {

        /**
         * Called when the RecyclerView has been created
         *
         * @param recyclerView the recyclerView that was just created
         */
        void onListViewCreated(RecyclerView recyclerView);
    }

    public void setFragmentListener(MyListener listener){
        mListener = listener;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // bind to service
        doBindService();

        seekbarIntent = new Intent();
        seekbarIntent.setAction(BROADCAST_SEEKBAR);

        imageButton = view.findViewById(R.id.play_pause);
        selectedPlaylist = new ArrayList<>();
        curBPMLiveData = new MutableLiveData<Integer>();
        songList = new ArrayList<>();

        createSongList();
        getSongList();
        buildRecyclerView(view);
        startListeners(view);

        curBPMLiveData.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                onBPMChange(integer);
            }
        });

    }

    public void createSongList() {
        allSongsList.add(popNum1);
        allSongsList.add(popNum2);
        allSongsList.add(popNum3);
        allSongsList.add(countryNum1);
        allSongsList.add(countryNum2);
        allSongsList.add(popNum4);
        allSongsList.add(popNum5);
        allSongsList.add(latin1);
        allSongsList.add(latin2);
        allSongsList.add(rock1);
        allSongsList.add(rock2);
        allSongsList.add(rock3);
        allSongsList.add(rock4);
        allSongsList.add(rock5);
        allSongsList.add(popNum6);
        allSongsList.add(popNum7);
        allSongsList.add(popNum8);

        //Collections.sort(allSongsList);
    }

    public void getSongList() {
        //retrieve the audio file information
        songList.clear();
        songList.add(new Song(000, "", "", "", "", 0, 0)); //currently playing song. set to nothing at first
        ArrayList<String> selectedGenres = (ArrayList<String>) getActivity().getIntent().getSerializableExtra("genres");
        ArrayList<Song> savedPlaylist = (ArrayList<Song>) getActivity().getIntent().getSerializableExtra("playlist");
        if (savedPlaylist != null) {
            songList.addAll(savedPlaylist);
        } else if (selectedGenres.size() == 0) {//no generes selected
            for (int j = 0; j < allSongsList.size(); j++) {
                Song song = allSongsList.get(j);
                if (!songList.contains(song)) {
                    songList.add(song);
                }
            }
        } else {
            for (int i = 0; i < selectedGenres.size(); i++) {
                String genre = selectedGenres.get(i);
                for (int j = 0; j < allSongsList.size(); j++) {
                    Song song = allSongsList.get(j);
                    if (song.getGenre().equals(genre)) {
                        if (!songList.contains(song)) {
                            songList.add(song);
                        }
                    }
                }
            }
        }

        if (songList.size() > 1) { //we set the first song in the list to be the first song from songList
            //because we are going to play it right away
            Collections.sort(songList);
            songList.set(0, songList.get(1));
        }
    }

    public void buildRecyclerView(final View view) {
        songRecyclerView = view.findViewById(R.id.list);
        songRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new SongListAdapter(songList);
        songRecyclerView.setAdapter(mAdapter);
        songRecyclerView.setLayoutManager(layoutManager);
        seekBar = mAdapter.getSeekBar();
//        if (mListener != null) {
//            mListener.onListViewCreated(songRecyclerView);
//        }
    }

    private void startListeners(View view) {
        mAdapter.setOnSeekBarChangeListener(new SongListAdapter.OnSeekChangeListener() {
            @Override
            public void onSeekChange(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int seekPos = seekBar.getProgress();
                    seekbarIntent.putExtra(PlayerService.SEEK_POS, seekPos);
                    getActivity().sendBroadcast(seekbarIntent);
                }
            }
        });

        mAdapter.setOnNextClickListener(new SongListAdapter.OnNextClickListener() {
            @Override
            public void onNextClick() {
                playSong(currentlyPlayingPosition + 1);
            }
        });

        mAdapter.setOnPlayClickListener(new SongListAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick() {
                playOrPause(view);
            }
        });

        mAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                imageButton = view.findViewById(R.id.play_pause);
                imageButton.setImageResource(R.drawable.ic_pause);
                playSong(position);
            }
        });

        mAdapter.setOnPreviousClickListener(new SongListAdapter.OnPreviousClickListener() {
            @Override
            public void onPreviousClick() {
                playSong(currentlyPlayingPosition - 1);
            }
        });
    }

    public static void playSong(int position) {
        if (position <= 0) {
            return;
        }
        if (position >= songList.size()) {
            position = 1;
        }

        Song song = songList.get(position);
        final int pos = position;
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child(song.getGenre()).child(song.getFullName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (mBoundService != null) {
                    mBoundService.startPlayer(uri.toString());
                    isAdded = false;
                } else {
                    Log.d(TAG, "onSuccess: service is null");
                }

                isPlaying = true;
                swapItem(pos);
                currentlyPlayingPosition = pos;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Failed to load from storage");
            }
        });

    }

    public static void swapItem(int position) {
        Song nowPlaying = songList.get(position);
        songList.set(0, nowPlaying);
        mAdapter.notifyDataSetChanged();
    }


    public void playOrPause(View view) {
        imageButton = view.findViewById(R.id.play_pause);
        if (isPlaying) {
            mBoundService.pausePlayer();
            imageButton.setImageResource(R.drawable.ic_play);
            isPlaying = false;
        } else {
            mBoundService.playPlayer();
            imageButton.setImageResource(R.drawable.ic_pause);
            isPlaying = true;
        }

    }


    public void onBPMChange(int BPM) {
//        Song curSong = songList.get(currentlyPlayingPosition);
//        int curSongBPM = curSong.getSongBPM();
//
        Song currentlyPlaying = songList.get(currentlyPlayingPosition);
        if (currentlyPlaying.getSongBPM() < BPM) {
            for (int i = currentlyPlayingPosition + 1; i < songList.size(); i++) {
                int bpm = songList.get(i).getSongBPM();
                if (bpm <= BPM + 10 && bpm >= BPM - 10) {
                    nextToPlay = i;
                    Log.d(TAG, "the index to play " + nextToPlay);
                    break;
                }
            }
        } else {
            for (int i = currentlyPlayingPosition - 1; i > 0; i--) {
                int bpm = songList.get(i).getSongBPM();
                if (bpm <= BPM + 10 && bpm >= BPM - 10) {
                    nextToPlay = i;
                    break;
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    /*
    Binding functions
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((PlayerService.LocalBinder) service).getService();
            Toast.makeText(getContext(), "local service connected", Toast.LENGTH_SHORT).show();
            playSong(1);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
            Toast.makeText(getContext(), "local service disconnected", Toast.LENGTH_SHORT).show();

        }
    };


    private void doBindService() {
        getActivity().bindService(new Intent(getActivity(), PlayerService.class),
                mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            getContext().unbindService(mConnection);
            mIsBound = false;
        }
    }


    private static void updateUI(Intent serviceIntent) {
        seekBar = mAdapter.getSeekBar();
        if (seekBar != null) {
            String counter = serviceIntent.getStringExtra("Counter");
            String mediaMax = serviceIntent.getStringExtra("mediaMax");
            if (counter != null && mediaMax != null) {
                int seekBarProgress = Integer.parseInt(counter);
                int seekMax = Integer.parseInt(mediaMax);
                seekBar.setMax(seekMax);
                seekBar.setProgress(seekBarProgress);
                if(!isAdded && seekBarProgress >= seekMax /2){
                    selectedPlaylist.add(songList.get(currentlyPlayingPosition));
                    isAdded = true;
                }
            }
        }

    }

    public static class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(PlayerService.SONG_ENDED)) {
                selectedPlaylist.add(songList.get(currentlyPlayingPosition));
                currentlyPlayingPosition = currentlyPlayingPosition >= songList.size() ? 0 : currentlyPlayingPosition; // check if last song was reached. if it has then play the first song again
                playSong(currentlyPlayingPosition + 1);
            } else {
                updateUI(intent);
            }

            // if(action.equals(BROADCAST_ACTION){updateUI();} todo: maybe add this instead of else
            //todo: see if this can be fixed with seek (the current position is unclear what it will be)
        }
    }

    @Override
    public void onResume() {
        // Notify listener that RecyclerView is now created
        if (mListener != null) {
            mListener.onListViewCreated(songRecyclerView);
        }
        super.onResume();


    }
}



