package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private VideoView video;
    private MediaPlayer mediaPlayer;
    private View hiddenPanel;
    private int currentVideoPosition;
    private TypeWriter tw;
    private LoginFragment loginFragment;
    private LinearLayout rootLayout;
    private CardView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        hiddenPanel = findViewById(R.id.fragment_container);
        mAuth = FirebaseAuth.getInstance();
        loginFragment = new LoginFragment();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent i = new Intent(this, NavigationBarActivity.class);
        if (currentUser != null) { // if user is signed in
            startActivity(i);
            finish();
        }

        rootLayout = findViewById(R.id.layout_root);
        logo = findViewById(R.id.logo);
        tw = findViewById(R.id.app_name);
        TextPaint paint = tw.getPaint();
        float width = paint.measureText(tw.getText().toString());

        Shader textShader = new LinearGradient(0, 0, width, tw.getTextSize(),
                new int[]{
                        Color.parseColor("#C840E9"),
                        Color.parseColor("#622374"),
                        Color.parseColor("#08090D"),
                }, null, Shader.TileMode.CLAMP);
        tw.getPaint().setShader(textShader);
        tw.setTextColor(Color.parseColor("#C840E9"));
        tw.setTextAppearance(R.style.styleA);

        tw.setText("");
        tw.setCharacterDelay(150);
        tw.animateText("Run2theBeat");

        tw.setVariableChangeListener(new TypeWriter.VariableChangeListener() {
            @Override
            public void onVariableChanged(boolean titleIsDone) {
                if (titleIsDone) {
                    AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                    AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    tw.startAnimation(fadeOut);
                    fadeOut.setDuration(3000);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tw.setVisibility(View.GONE);
                            displayLogin();

//                            logo.setVisibility(View.VISIBLE);
//                            logo.startAnimation(fadeIn);
//                            fadeIn.setDuration(3000);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    fadeIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    displayLogin();

                                }
                            }, 1000); // delay one second

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


                }
            }
        });


//        video = findViewById(R.id.video_view);
//        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.run_muted);
//        video.setVideoURI(uri);
//        video.start();
//
//
//        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                slideUpDown();
//                getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, loginFragment)
//                    .commit();
//
//
//            }
//        });
    }

    private void displayLogin() {
        logo.setVisibility(View.GONE);
        slideUpDown();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, loginFragment)
                .commit();
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
