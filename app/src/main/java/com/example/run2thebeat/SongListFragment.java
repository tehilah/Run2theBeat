package com.example.run2thebeat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.media.MediaPlayer;
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

public class SongListFragment extends Fragment {

    private String TAG = "SongListFragment";
    private ArrayList<Song> songList = new ArrayList<>();
    private RecyclerView songRecyclerView;
    private SongListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private MediaPlayer mediaPlayer  = new MediaPlayer();


    private static ArrayList<Song> allSongsList =new ArrayList<Song>();
    public static Map<Song,String> songsAndUrl = new HashMap<Song,String>();
    private static Song popNum1 = new Song(1,"I Dont Care","Ed Sheeran & Justin Bieber ","Pop");
    private static Song popNum2 = new Song(2,"Faith","Stevie Wonder ft. Ariana Grande","Pop");
    private static Song popNum3 = new Song(3,"Bananas"," static and benel","Pop");
    private static Song countryNum1 = new Song(4,"Before He Cheats","Carrie Underwood","Country");
    private static Song countryNum2 = new Song(5,"Heartache On The Dance Floor","Jon Pardi","Country");


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,@NonNull Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        createSongList();
        getSongList();
        createDictionaries();

        buildRecyclerView(view);

    }


    public void createSongList(){
        allSongsList.add(popNum1);
        allSongsList.add(popNum2);
        allSongsList.add(popNum3);
        allSongsList.add(countryNum1);
        allSongsList.add(countryNum2);
    }

    public static void createDictionaries() {

        songsAndUrl.put(popNum1,"https://firebasestorage.googleapis.com/v0/b/run-2-the-beat.appspot.com/o/pop%2FEd%20Sheeran%20%26%20Justin%20Bieber%20I%20Dont%20Care%20(Official%20Audio).mp3?alt=media&token=2bba621d-45f8-40ad-a6c1-3386b8117fcf");
        songsAndUrl.put(popNum2,"https://firebasestorage.googleapis.com/v0/b/run-2-the-beat.appspot.com/o/pop%2FStevie%20Wonder%20-%20Faith%20ft.%20Ariana%20Grande.mp3?alt=media&token=0dde674b-bad1-4342-85d1-674c86bbad17");
        songsAndUrl.put(popNum3,"https://firebasestorage.googleapis.com/v0/b/run-2-the-beat.appspot.com/o/pop%2F%D7%A1%D7%98%D7%98%D7%99%D7%A7%20%D7%95%D7%91%D7%9F%20%D7%90%D7%9C%20%D7%AA%D7%91%D7%95%D7%A8%D7%99%20-%20%D7%91%D7%A0%D7%A0%D7%95%D7%AA%20(Prod.%20By%20Jordi).mp3?alt=media&token=c3edc41a-fb55-4f7b-9f5d-c79b58033bf6");
        songsAndUrl.put(countryNum1,"https://firebasestorage.googleapis.com/v0/b/run-2-the-beat.appspot.com/o/country%2FCarrie%20Underwood%20-%20Before%20He%20Cheats.mp3?alt=media&token=9c06a18b-0f42-4855-8ebe-996ff9e0f8f7");
        songsAndUrl.put(countryNum2,"https://firebasestorage.googleapis.com/v0/b/run-2-the-beat.appspot.com/o/country%2FJon%20Pardi%20-%20Heartache%20On%20The%20Dance%20Floor%20(Audio).mp3?alt=media&token=229ad0c5-e110-455e-a501-cdd9b43a0df9");

    }

    public void getSongList() {
        //retrieve the audio file information
        songList.clear();
        ArrayList<String> selectedGeners = (ArrayList<String>) getActivity().getIntent().getSerializableExtra("generes");

        for (int i=0; i<selectedGeners.size(); i++){
            String genere = selectedGeners.get(i);
            for (int j=0; j<allSongsList.size();j++){
                Song song = allSongsList.get(j);
                if(song.getGenre().equals(genere)){
                    if(!songList.contains(song)){
                        songList.add(song);
                    }

                }
            }
        }

    }


    public void buildRecyclerView(final View view){
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
                swapeItem(position,0);
            }
        });

        mAdapter.setOnPlayClickListener(new SongListAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick() {
                playOrPause(view);
            }
        });
        
    }

    public void playSong(int position) {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();

        Song song = songList.get(position);
        String songURL = songsAndUrl.get(song);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        try {
            mediaPlayer.setDataSource(songURL);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepare();
        } catch (IOException o) {

        }
    }

    public void swapeItem(int fromPosition,int toPosition){
        Collections.swap(songList, fromPosition, toPosition);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }


    public void playOrPause(View view){
        ImageButton imageButton = (ImageButton)view.findViewById(R.id.play_pause);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            imageButton.setImageResource(R.drawable.ic_play);
        }
        else {
            mediaPlayer.start();
            imageButton.setImageResource(R.drawable.ic_pause);
        }

    }


}
