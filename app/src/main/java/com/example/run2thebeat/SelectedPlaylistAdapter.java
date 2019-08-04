package com.example.run2thebeat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectedPlaylistAdapter extends RecyclerView.Adapter<SelectedPlaylistAdapter.SongViewHolder>{

    private ArrayList<Song> songList;
    private SelectedPlaylistAdapter.OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(SelectedPlaylistAdapter.OnItemClickListener listener){
        mListener = listener;
    }



    public static class SongViewHolder extends RecyclerView.ViewHolder{
        public TextView songName;
        public TextView artist;
        public SongViewHolder(@NonNull View item){
            super(item);
            songName = item.findViewById(R.id.song_title);
            artist = item.findViewById(R.id.song_artist);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    public SelectedPlaylistAdapter(ArrayList<Song> myPlayList){
        songList = myPlayList;
    }


    @NonNull
    @Override
    public SelectedPlaylistAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent, false);
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent,false);

        return new SelectedPlaylistAdapter.SongViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull SelectedPlaylistAdapter.SongViewHolder holder, int position) {
        Song songItem = songList.get(position);
        holder.artist.setText(songItem.getArtist());
        holder.songName.setText(songItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
