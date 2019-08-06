package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinishRunScreenActivity extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_avgPace;
    private TextView tv_duration;
    private TextView tv_km;
    private TextView tv_avgBpm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_run_screen);
        initVariables();
        setTextViews();
    }

    private void setTextViews() {
        Intent intent = getIntent();
        tv_title.setText(intent.getStringExtra("TITLE"));
        tv_avgPace.setText(intent.getStringExtra("AVG_PACE"));
        String avgBPM = intent.getIntExtra("AVG_BPM", 0) + "";
        tv_avgBpm.setText(avgBPM);
        tv_km.setText(intent.getStringExtra("KM"));
        tv_duration.setText(intent.getStringExtra("DURATION"));
    }

    private void initVariables() {
        tv_title = findViewById(R.id.title);
        tv_avgPace = findViewById(R.id.avg_pace);
        tv_duration = findViewById(R.id.duration);
        tv_km = findViewById(R.id.kilometers);
        tv_avgBpm = findViewById(R.id.avg_bpm);
    }


    public void finish(View view) {
        startActivity(new Intent(this, NavigationBarActivity.class));
        finish();
    }
}
