package com.example.run2thebeat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RunFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton fab;
    private LinearLayout mediaPlayerLayout;
    private boolean isFabOpen;
    private ImageView playPause;
    private Button newPlaylistBtn;
    private static PlayerService mBoundService;
    private boolean mIsBound;
    private TextView songTitle;
    private TextView songArtist;
    private boolean mBroadcastIsRegistered;
    private SeekBar seekBar;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private ImageButton nextButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doBindService();
        initViews(view);

        fab.setOnClickListener(this);
        newPlaylistBtn.setOnClickListener(this);
        playPause.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        getChildFragmentManager().beginTransaction().replace(R.id.saved_playlists_fragment,
                new ShowPlaylistsFragment()).commit();
    }


    private void initViews(View view) {
        newPlaylistBtn = view.findViewById(R.id.choose_new_playlist);
        fab = view.findViewById(R.id.fab);
        mediaPlayerLayout = view.findViewById(R.id.media_player_layout);
        playPause = view.findViewById(R.id.play_pause);
        songTitle = view.findViewById(R.id.song_title);
        songArtist = view.findViewById(R.id.song_artist);
        seekBar = view.findViewById(R.id.seekbar);
        nextButton = view.findViewById(R.id.next_song);
        fab_open = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.media_player_open);
        fab_close = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.media_player_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.rotate_backward);
    }


    /**
     * Check if media player is paused or if its null. if the player is null then don't show floating
     * action bar at all. if it is not null, show the fab and update the relevant textViews.
     */
    private void checkForUpdates() {
        if (!mBoundService.isMediaStarted()) { // check if player is in pause mode or not set at all
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
            if (SongListFragment.songList.size() > 0) { // update the song title and artist
                updateTextViews(SongListFragment.currentlyPlayingPosition);
            }
            if (!mBoundService.isPlaying()) { // make sure playPause button is in the right state
                playPause.setImageResource(R.drawable.ic_play);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                startAnimation();
                break;
            case R.id.choose_new_playlist:
                Intent i = new Intent(getActivity(), MusicListActivity.class);
                startActivity(i);
                break;
            case R.id.play_pause:
                playOrPause();
                break;
            case R.id.next_song:
                playNext();
                break;
        }
    }

    private void startAnimation() {
        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            mediaPlayerLayout.startAnimation(fab_close);
            mediaPlayerLayout.setVisibility(View.GONE);
            isFabOpen = false;

        } else {
            fab.startAnimation(rotate_forward);
            mediaPlayerLayout.setVisibility(View.VISIBLE);
            mediaPlayerLayout.startAnimation(fab_open);
            isFabOpen = true;
        }
    }

    private void playOrPause() {
        if (mBoundService != null && mBoundService.isPlaying()) {
            mBoundService.pausePlayer();
            playPause.setImageResource(R.drawable.ic_play);
        } else if (mBoundService != null) {
            mBoundService.playPlayer();
            playPause.setImageResource(R.drawable.ic_pause);
        }

    }

    private void playNext() {
        if (mBoundService != null) {
            SongListFragment.playSong(SongListFragment.currentlyPlayingPosition + 1);
            updateTextViews(SongListFragment.currentlyPlayingPosition + 1);
        }
    }

    /*
  Binding functions
   */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((PlayerService.LocalBinder) service).getService();
            Toast.makeText(getContext(), "local service connected", Toast.LENGTH_SHORT).show();
            checkForUpdates();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(PlayerService.SONG_ENDED)) {
                updateTextViews(SongListFragment.currentlyPlayingPosition + 1);
            }
            updateUI(intent);
        }
    };

    private void updateTextViews(int position) {
        if(position >= SongListFragment.songList.size()){
            position = 1;
        }
        String songName = SongListFragment.songList.get(position).getTitle();
        String artist = SongListFragment.songList.get(position).getArtist();
        songTitle.setText(songName);
        songArtist.setText(artist);
    }

    @Override
    public void onResume() {
        // register receiver
        if (!mBroadcastIsRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PlayerService.SONG_ENDED);
            intentFilter.addAction(PlayerService.BROADCAST_ACTION);
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
            mBroadcastIsRegistered = true;
            Toast.makeText(getContext(), "register broadcast receiver", Toast.LENGTH_SHORT).show();
        }
        if (mBoundService != null) {
            checkForUpdates();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mBroadcastIsRegistered) {
            getActivity().unregisterReceiver(broadcastReceiver);
            mBroadcastIsRegistered = false;
            Toast.makeText(getContext(), "unregister broadcast receiver", Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }

    private void updateUI(Intent serviceIntent) {
        if (seekBar != null) {
            String counter = serviceIntent.getStringExtra("Counter");
            String mediaMax = serviceIntent.getStringExtra("mediaMax");
            if (counter != null && mediaMax != null) {
                int seekBarProgress = Integer.parseInt(counter);
                int seekMax = Integer.parseInt(mediaMax);
                seekBar.setMax(seekMax);
                seekBar.setProgress(seekBarProgress);
            }
        }
    }

}
