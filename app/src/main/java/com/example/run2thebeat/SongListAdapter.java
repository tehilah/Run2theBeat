package com.example.run2thebeat;

import android.content.Context;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

import java.util.logging.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {
    private ArrayList<Song> songList;
    private OnItemClickListener mListener;
    private OnPlayClickListener mPlayListener;
    private OnNextClickListener mNextListener;
    private OnPrviousClickListener mPreviousListener;
    private SeekBar mSeekBar;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnPlayClickListener {
        void onPlayClick();
    }

    public void setOnPlayClickListener(OnPlayClickListener listener) {
        mPlayListener = listener;
    }

    public interface OnNextClickListener {
        void onNextClick();
    }

    public void setOnNextClickListener(OnNextClickListener listener) {
        mNextListener = listener;
    }

    public interface OnPrviousClickListener {
        void onPreviousClick();
    }

    public void setOnPreviousClickListener(OnPrviousClickListener listener) {
        mPreviousListener = listener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView songName;
        public TextView artist;
        public ImageView songCover;
        public ImageButton pausePlayButton;
        public ImageButton nextButton;
        public ImageButton previousButton;


        public SongViewHolder(@NonNull View item, final OnItemClickListener listener, final OnPlayClickListener playListener, final OnNextClickListener nextListener,
                              final OnPrviousClickListener previousListener) {
            super(item);
            songName = item.findViewById(R.id.song_title);
            songName.setSelected(true);
            artist = item.findViewById(R.id.song_artist);
            artist.setSelected(true);
            songCover = item.findViewById(R.id.song_cover);
//            artist.setMovementMethod(new ScrollingMovementMethod());
            pausePlayButton = item.findViewById(R.id.play_pause);
            nextButton = item.findViewById(R.id.next_song);
            previousButton = item.findViewById(R.id.previous_song);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && position != 0) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            if (pausePlayButton != null) {
                pausePlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (playListener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                playListener.onPlayClick();
                            }
                        }
                    }
                });
            }

            if (nextButton != null) {
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nextListener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                nextListener.onNextClick();

                            }
                        }
                    }
                });
            }

            if (previousButton != null) {
                previousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (previousListener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                previousListener.onPreviousClick();

                            }
                        }
                    }
                });
            }
        }

    }


    public SongListAdapter(ArrayList<Song> mySongList) {
        songList = mySongList;
    }


    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent, false);
        if  (viewType == R.layout.song) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.currently_playing_song, parent, false);
        }
        return new SongViewHolder(v, mListener, mPlayListener, mNextListener, mPreviousListener);
    }


    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song songItem = songList.get(position);
        holder.artist.setText(songItem.getArtist());
        holder.songName.setText(songItem.getTitle());
        holder.songCover.setImageResource(songItem.getSongCover());
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? R.layout.currently_playing_song : R.layout.song;
    }

    public SeekBar getSeekBar() {
        return mSeekBar;
    }

}
