package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class SavedRunsActivity extends AppCompatActivity {

    private ArrayList<SavedRunItem> savedRuns;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_runs);

        savedRuns = new ArrayList<>();
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Sunday morning run", "10.00 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Tuesday Afternoon run", "3.00 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Friday evening run", "6.5 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Wednesday night run", "4.3 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Monday morning run", "7.00 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Sunday afternoon run", "8.5 km") );

        buildRecyclerView();


    }

    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); // todo: delete this if not working right
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MyAdapter(savedRuns);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getApplicationContext(), "clicked item: "+(""+position), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), RunDetailsActivity.class);
                startActivity(i);
            }
        });
    }


}
