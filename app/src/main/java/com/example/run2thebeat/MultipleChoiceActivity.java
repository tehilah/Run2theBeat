package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MultipleChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice);
    }

    public void ChooseMusicGenres(View view){

        Intent i = new Intent(this, MusicListActivity.class);
        startActivity(i);
    }

//    public void openMap(View view) {
//        Intent i = new Intent(this, MapsActivity.class);
//        startActivity(i);
//    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
