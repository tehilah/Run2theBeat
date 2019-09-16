package com.example.run2thebeat;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.run2thebeat.MyNotificationChannel.CHANNEL_ID_1;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {

    public static final String SONG_ENDED = "com.example.run2thebeat.PlayerService.SONG_ENDED";
    public static final String SEEK_POS = "com.example.run2thebeat.PlayerService.SEEK_POS";

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    private String mTitle;
    private String mArtist;
    private int mSongCover;
    private ImageButton playPauseBtn;

    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean mIsBroadcastRegistered;
    private boolean hasMusicStarted; // variable to notify whether media player is in pause or has not been set a data source at all
    private NotificationManagerCompat mNotificationManagerCompat;

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
//        Toast.makeText(this, "--Service Created--", Toast.LENGTH_SHORT).show();

        seekIntent = new Intent(BROADCAST_ACTION); // intent for moving seekbar (not user changing it)
        mNotificationManagerCompat = NotificationManagerCompat.from(this);

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
//        Toast.makeText(this, "--Service Started--", Toast.LENGTH_SHORT).show();

        // register broadcast receiver
        if (!mIsBroadcastRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SongListFragment.BROADCAST_SEEKBAR);
            registerReceiver(broadcastReceiver, intentFilter);
            mIsBroadcastRegistered = true;
        }

        Intent broadcastIntent = new Intent();
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        if (mManager == null) {
            initMediaSessions();
        }
        handleIntent(intent);

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
            //---- variables for seekbar processing ----
            int mediaPosition = mediaPlayer.getCurrentPosition();
            int mediaMax = mediaPlayer.getDuration();
            seekIntent.putExtra("Counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediaMax", String.valueOf(mediaMax));
            seekIntent.putExtra("songEnded", String.valueOf(songEnded));
            sendBroadcast(seekIntent);
        }
    }

    public void startPlayer(String url) {
        mediaPlayer.stop();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            hasMusicStarted = true;
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
        hasMusicStarted = false;
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        handler.removeCallbacks(sendUpdatesToUI);
        if (mIsBroadcastRegistered) {
            unregisterReceiver(broadcastReceiver);
            mIsBroadcastRegistered = false;
        }
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
        sendBroadcast(intent);
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
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }
    };

    private void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra(SEEK_POS, 0);
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
            mediaPlayer.seekTo(seekPos);
            setUpHandler();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isMediaStarted() {

        return hasMusicStarted;
    }

    public void sendNotification(String title, String artist, int songCover, boolean isPlaying) {
        mTitle = title;
        mArtist = artist;
        mSongCover = songCover;
        NotificationCompat.Action action;
        if (isPlaying) {
            action = generateAction(R.drawable.ic_pause, "pause", ACTION_PAUSE);
        } else {
            action = generateAction(R.drawable.ic_play, "play", ACTION_PLAY);
        }
        Bitmap largeImage = BitmapFactory.decodeResource(getResources(), songCover);

        Notification channel = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_1)
                .setContentTitle(title)
                .setContentText(artist)
                .setLargeIcon(largeImage)
                .setSmallIcon(R.drawable.ic_queue_music_white)
                .addAction(action)
                .addAction(generateAction(R.drawable.ic_next_song, "next", ACTION_NEXT))
                .setOnlyAlertOnce(true)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))
                .build();
        mNotificationManagerCompat.notify(1, channel);
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();

        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            mController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            mController.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
            mController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            mController.getTransportControls().stop();
        }
    }

    private void initMediaSessions() {
        mSession = new MediaSession(this, "MusicService");
        mController = new MediaController(getApplicationContext(), mSession.getSessionToken());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setCallback(new MediaSession.Callback() {
                                 @Override
                                 public void onPlay() {
                                     super.onPlay();
                                     playPlayer();
                                     sendNotification(mTitle, mArtist, mSongCover, true);
                                     playPauseBtn.setImageResource(R.drawable.ic_pause);
                                     SongListFragment.isPlaying = true;

                                 }

                                 @Override
                                 public void onPause() {
                                     super.onPause();
                                     pausePlayer();
                                     sendNotification(mTitle, mArtist, mSongCover, false);
                                     playPauseBtn.setImageResource(R.drawable.ic_play);
                                     SongListFragment.isPlaying = false;
                                 }

                                 @Override
                                 public void onSkipToNext() {
                                     super.onSkipToNext();
                                     SongListFragment.chooseNext();
                                     sendNotification(mTitle, mArtist, mSongCover, true);
                                 }

                                 @Override
                                 public void onSkipToPrevious() {
                                     super.onSkipToPrevious();
//                                     buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                                 }


                                 @Override
                                 public void onStop() {
                                     super.onStop();
                                     //Stop media player here
                                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                     notificationManager.cancel(1);
                                     Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
                                     stopService(intent);
                                 }

//                                 @Override
//                                 public void onSeekTo(long pos) {
//                                     super.onSeekTo(pos);
//                                 }
                             }
        );
    }

    private NotificationCompat.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action(icon, title, pendingIntent);
    }

    public void initPlayPauseBtn(ImageButton imageButton) {
        playPauseBtn = imageButton;
    }
}

