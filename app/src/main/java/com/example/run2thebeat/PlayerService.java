package com.example.run2thebeat;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {

    public static final String SONG_ENDED = "song ended";
    public static final String SAVE_SONG = "save song";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    public static final String START_MEDIA_PLAYER = "start media player";
    private final IBinder mBinder = new LocalBinder();

    //---- variables for seekbar processing ----
    private String sentSeekPos;
    private int seekPosition;
    private int mediaPosition;
    private int mediaMax;
    private final Handler handler = new Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "seek progress";
    private Intent seekIntent;

    public class LocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "--Service Created--", Toast.LENGTH_SHORT).show();

        seekIntent = new Intent(BROADCAST_ACTION);

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.reset();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "--Service Started--", Toast.LENGTH_SHORT).show();
        setUpHandler();
        return START_STICKY;
    }

    private void setUpHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // update UI every second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        @Override
        public void run() {
            logMediaPosition();
            handler.postDelayed(this, 1000);
        }
    };

    private void logMediaPosition() {
        if (mediaPlayer.isPlaying()) {
            mediaPosition = mediaPlayer.getCurrentPosition();

            mediaMax = mediaPlayer.getDuration();
            seekIntent.putExtra("Counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediaMax", String.valueOf(mediaMax));
            seekIntent.putExtra("songEnded", String.valueOf(songEnded));
            LocalBroadcastManager.getInstance(this).sendBroadcast(seekIntent);
        }
    }

    public void startPlayer(String url) {
        if (mediaPlayer.isPlaying()) {
            int songLength = mediaPlayer.getDuration();
            int howLong = mediaPlayer.getCurrentPosition();
            if (howLong >= songLength / 2) {
                Intent intent = new Intent();
                intent.setAction(SAVE_SONG);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
        mediaPlayer.stop();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void pausePlayer() {
        mediaPlayer.pause();
    }

    public void playPlayer() {
        mediaPlayer.start();
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "--Service Destroyed--", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        handler.removeCallbacks(sendUpdatesToUI);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!mp.isPlaying()) {
            mp.start();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();
        }
        Intent intent = new Intent();
        intent.setAction(SONG_ENDED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED " + extra, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN " + extra, Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
}
