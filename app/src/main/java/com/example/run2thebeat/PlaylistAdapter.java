package com.example.run2thebeat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private ArrayList<PlaylistItem> playlistsList;

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{
        public ImageView playlistImage;
        public TextView avg_bpm;
        public TextView km;
        public PlaylistViewHolder(@NonNull View item) {
            super(item);
            playlistImage = item.findViewById(R.id.image_of_playlist);
            avg_bpm = item.findViewById(R.id.avg_bpm);
            km = item.findViewById(R.id.km);
        }
    }

    public PlaylistAdapter(ArrayList<PlaylistItem> playlists){ playlistsList = playlists;}

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistItem playlistItem = playlistsList.get(position);
//        holder.avg_bpm.setText("jjjj");
//        holder.km.setText("mmmmm");

    }


    @Override
    public int getItemCount() {
        return playlistsList.size();
    }

}
