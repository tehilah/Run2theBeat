package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class RunningScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_screen);
        checkOpenPlaylistButton();

    }

    public void checkOpenPlaylistButton(){

        Button openPlaylistButton = (Button) findViewById(R.id.open_playlist_button);
        final Intent intent = new Intent(this,SongListActivity.class);
        openPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> generes = (ArrayList<String>) getIntent().getSerializableExtra("generes");
                intent.putExtra("generes",generes);
                startActivity(intent);
            }
        });
    }
}
