package com.example.run2thebeat;

import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgressAdapter extends FirestoreRecyclerAdapter {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProgressAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull Object o) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }
}
