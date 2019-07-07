package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class RunningScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_screen);
        TextView textView = (TextView)findViewById(R.id.running_details);


        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment,
                new SongListFragment()).commit();


    }






}
