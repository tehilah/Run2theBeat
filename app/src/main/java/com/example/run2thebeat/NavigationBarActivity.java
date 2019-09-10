package com.example.run2thebeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationBarActivity extends AppCompatActivity {

    private boolean mBroadcastIsRegistered;
    private SongListFragment.MyBroadcastReceiver myReceiver;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);

        // register receiver
        if (!mBroadcastIsRegistered) {
            myReceiver = new SongListFragment.MyBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PlayerService.SONG_ENDED);
            intentFilter.addAction(PlayerService.BROADCAST_ACTION);
            registerReceiver(myReceiver, intentFilter);
            mBroadcastIsRegistered = true;
        }


        //startService
        intent = new Intent(this, PlayerService.class);
        startService(intent);

        if(savedInstanceState == null) {

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        bottomNavigation.setSelectedItemId(R.id.nav_run);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RunFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_run:
                    selectedFragment = new RunFragment();
                    break;
                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
                    break;
                case R.id.nav_progress:
                    selectedFragment = new ProgressFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
            return true;
        }
    };


    @Override
    protected void onDestroy() {
        if (mBroadcastIsRegistered) {
            unregisterReceiver(myReceiver);
            mBroadcastIsRegistered = false;
        }
        stopService(intent);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
