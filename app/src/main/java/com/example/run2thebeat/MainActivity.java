package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private VideoView video;
    private MediaPlayer mediaPlayer;
    private View hiddenPanel;
    private int currentVideoPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        hiddenPanel = findViewById(R.id.fragment_container);
        mAuth = FirebaseAuth.getInstance();
        final LoginFragment loginFragment = new LoginFragment();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent i = new Intent(this, NavigationBarActivity.class);
        if (currentUser != null) { // if user is signed in
            startActivity(i);
            finish();
        }

        video = findViewById(R.id.video_view);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.run_muted);
        video.setVideoURI(uri);
        video.start();

//        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer = mp;
////                mediaPlayer.setLooping(true);
//
//                if (currentVideoPosition != 0) {
//                    mediaPlayer.seekTo(currentVideoPosition);
//                    mediaPlayer.start();
//                }
//            }
//        });

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                if (savedInstanceState == null) {
//                    video.setVisibility(View.GONE);
//                    getSupportFragmentManager().beginTransaction()
//                            .add(R.id.fragment_container, loginFragment)
//                            .commit();
//                }
                slideUpDown();
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, loginFragment)
                    .commit();


            }
        });


//        final LoginFragment loginFragment = new LoginFragment();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) { // if user is signed in
//            Intent i = new Intent(this, NavigationBarActivity.class);
//            startActivity(i);
//            finish();
//        } else if (savedInstanceState == null) {
////            getSupportFragmentManager().beginTransaction()
////                    .add(R.id.fragment_container, loginFragment)
////                    .commit();
//
//
//        }
    }


    public void slideUpDown() {
        if (!isPanelShown()) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(this,
                    R.anim.bottom_up);

            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
        }
    }

    private boolean isPanelShown() {
        return hiddenPanel.getVisibility() == View.VISIBLE;
    }
}
