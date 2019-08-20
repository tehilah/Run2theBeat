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
    private OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

//
//    public void setOnDeleteClickListener(PlaylistAdapter.OnItemClickListener listener) {
//        mListener = listener;
//    }

    public void setOnItemClickListener(PlaylistAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{
        public ImageView playlistImage;
        public TextView avg_bpm;
        public TextView km;
        public PlaylistViewHolder(@NonNull View item, PlaylistAdapter.OnItemClickListener listener) {
            super(item);
            playlistImage = item.findViewById(R.id.image_of_playlist);
            avg_bpm = item.findViewById(R.id.avg_bpm);
            km = item.findViewById(R.id.distance);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                    return true;
                }
            });
        }
    }

    public PlaylistAdapter(ArrayList<PlaylistItem> playlists){ playlistsList = playlists;}

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlaylistViewHolder(v,mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistItem playlistItem = playlistsList.get(position);
        holder.avg_bpm.setText(playlistItem.getName());
        holder.km.setText(playlistItem.getKm());
    }


    @Override
    public int getItemCount() {
        return playlistsList.size();
    }

}
