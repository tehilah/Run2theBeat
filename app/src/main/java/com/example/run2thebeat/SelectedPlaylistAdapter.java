package com.example.run2thebeat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectedPlaylistAdapter extends RecyclerView.Adapter<SelectedPlaylistAdapter.SongViewHolder> {

    private ArrayList<Song> songList;
    private SelectedPlaylistAdapter.OnDeleteClickListener mDelete;
    public ImageButton imageButton;


    public interface OnDeleteClickListener {
        void onDeleteClick(int position);

    }

    public void setOnDeleteClickListener(SelectedPlaylistAdapter.OnDeleteClickListener listener) {
        mDelete = listener;
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView songName;
        public TextView artist;
        public ImageButton deleteSongButton;
//        public ImageView songCover;

        public SongViewHolder(@NonNull final View item, final SelectedPlaylistAdapter.OnDeleteClickListener deleteListener) {
            super(item);
            songName = item.findViewById(R.id.song_title);
            artist = item.findViewById(R.id.song_artist);
            deleteSongButton = item.findViewById(R.id.icon_delete_song);
//            songCover = item.findViewById(R.id.image_of_song);

            deleteSongButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deleteListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            deleteListener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }


    public SelectedPlaylistAdapter(ArrayList<Song> myPlayList) {
        songList = myPlayList;
    }


    @NonNull
    @Override
    public SelectedPlaylistAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent, false);

        return new SelectedPlaylistAdapter.SongViewHolder(v, mDelete);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedPlaylistAdapter.SongViewHolder holder, final int position) {
        Song songItem = songList.get(position);
        holder.artist.setText(songItem.getArtist());
        holder.artist.setTextColor(Color.WHITE);
        holder.songName.setText(songItem.getTitle());
        holder.songName.setTextColor(Color.WHITE);
//        holder.songCover.setImageResource(songItem.getSongCover());
        holder.deleteSongButton.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
