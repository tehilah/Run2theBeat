package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class CountDownActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;
    TextView count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);
        count= findViewById(R.id.count);
        Intent intent = new Intent(getBaseContext(),MapsActivity.class);
        countDownTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onFinish() {
                countDownTimer.cancel();
                startActivity(intent);
                finish();


            }
            @Override
            public void onTick(long millisUntilFinished) {
                count.setText(String.valueOf(millisUntilFinished/1000));
            }
        };
        countDownTimer.start();
    }
}
