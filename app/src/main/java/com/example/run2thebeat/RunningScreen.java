package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class RunningScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_screen);
        TextView textView = (TextView)findViewById(R.id.running_details);
        Button finishRun = (Button)findViewById(R.id.stop_run_button);
        final Intent intent = new Intent(this,FinishRunActivity.class);



        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment,
                new SongListFragment()).commit();


        finishRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                intent.putExtra("selectedPlaylist",SongListFragment.selectedPlaylist);
                startActivity(intent);

            }
        });



    }

}
