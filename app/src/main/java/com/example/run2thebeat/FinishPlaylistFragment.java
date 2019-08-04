package com.example.run2thebeat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FinishPlaylistFragment extends Fragment {
    public ArrayList<Song> selectedPlaylist = new ArrayList<>();
    private SelectedPlaylistAdapter mAdapter;
    private RecyclerView playlistRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finish_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        selectedPlaylist = (ArrayList<Song>)getActivity().getIntent().getSerializableExtra("selectedPlaylist");
        selectedPlaylist.remove(0); //remove currently playing song
        buildRecyclerView(view);
    }



    public void buildRecyclerView(final View view){
        playlistRecyclerView = view.findViewById(R.id.play_list_recycler);
        playlistRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new SelectedPlaylistAdapter(selectedPlaylist);
        playlistRecyclerView.setAdapter(mAdapter);
        playlistRecyclerView.setLayoutManager(layoutManager);
        mAdapter.setOnItemClickListener(new SelectedPlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });

    }


}
