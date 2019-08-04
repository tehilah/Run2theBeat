package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class FinishRunActivity extends AppCompatActivity {
    public ArrayList<Song> playlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_run);

        getSupportFragmentManager().beginTransaction().replace(R.id.playlist_fragment,
                new FinishPlaylistFragment()).commit();


    }
}
