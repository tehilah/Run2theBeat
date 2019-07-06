package com.example.run2thebeat;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ProgressFragment extends Fragment {
    private ArrayList<SavedRunItem> savedRuns;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        savedRuns = new ArrayList<>();
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Sunday morning run", "10.00 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Tuesday Afternoon run", "3.00 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Friday evening run", "6.5 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Wednesday night run", "4.3 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Monday morning run", "7.00 km") );
        savedRuns.add(new SavedRunItem(R.drawable.ic_running, "Sunday afternoon run", "8.5 km") );

        buildRecyclerView(view);
    }

    private void buildRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); // todo: delete this if not working right
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MyAdapter(savedRuns);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), "clicked item: "+(""+position), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getContext(), RunDetailsActivity.class);
                startActivity(i);
            }
        });
    }
}
