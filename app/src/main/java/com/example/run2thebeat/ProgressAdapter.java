package com.example.run2thebeat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgressAdapter extends FirestoreRecyclerAdapter<SavedRunItem, ProgressAdapter.MyViewHolder> {

    private OnItemClickListener mListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot ds, int position);
    }

    public void setOnItemClickListener(ProgressAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ProgressAdapter(@NonNull FirestoreRecyclerOptions<SavedRunItem> options, Context c) {
        super(options);
        context = c;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SavedRunItem currentItem) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        currentItem.setContext(context);
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.title.setText(currentItem.getDateDescription());
        holder.date.setText(df.format(currentItem.getDate()));
        holder.km.setText(currentItem.getDistance());
        holder.avgTime.setText(currentItem.getAvgPace());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new MyViewHolder(v, mListener);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public DocumentReference getRoute(int position){
        return getSnapshots().getSnapshot(position).getReference();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView title;
        public TextView date;
        public TextView km;
        public TextView avgTime;


        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            km = itemView.findViewById(R.id.bottom_text_km);
            avgTime = itemView.findViewById(R.id.bottom_text_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(getSnapshots().getSnapshot(position), position);
                        }
                    }
                }
            });
        }
    }


}
