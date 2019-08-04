package com.example.run2thebeat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {
    private String TAG = "MusicListActivity";
    MyCustomAdapter dataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        Log.d("TAG", "checklist: here1");
        displayListView();
        Log.d("TAG", "checklist: here5");
        checkButtonClicked();
        checkStartRunClicked();
    }

    private void displayListView() {
        String[] genre_names = {"pop", "avant-garde", "electronic", "hip hop", "latin", "country",
                "jazz", "rock", "rap", "rbandsoul"};

        ArrayList<Genre> genresList = new ArrayList<>();
        for (String genre_name: genre_names){
            Genre genre = new Genre(genre_name, false);
            genresList.add(genre);
        }
        dataAdapter = new MyCustomAdapter(this, R.layout.lv_item, R.id.genre,genresList);
        ListView listView = findViewById(R.id.list_view1);
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Genre genre = (Genre) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "clicked on row: "+genre.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyCustomAdapter extends ArrayAdapter<Genre>{
        private ArrayList<Genre> genreList;

        public MyCustomAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<Genre> genres) {
            super(context, resource, textViewResourceId, genres);
            this.genreList = new ArrayList<>();
            this.genreList.addAll(genres);
        }

        public ArrayList<Genre> getGenreList() {
            return genreList;
        }

        private class ViewHolder{
            TextView genre_name;
            CheckBox checkBox;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            super.getView(position, convertView, parent);

            ViewHolder holder = null;
            if(convertView == null){
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.lv_item, null);

                holder = new ViewHolder();
                holder.genre_name = (TextView) convertView.findViewById(R.id.genre);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);

                holder.checkBox.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Genre genre = (Genre) cb.getTag();
                        Toast.makeText(getApplicationContext(), "clicked on Checkbox: " + genre.getName(), Toast.LENGTH_SHORT).show();
                        genre.setSelected(cb.isChecked());
                    }
                });
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            Genre genre = genreList.get(position);
            holder.genre_name.setText(genre.getName());
            holder.checkBox.setChecked(genre.getSelected());
            holder.checkBox.setTag(genre);

            return convertView;
        }
    }

    public void checkButtonClicked(){
        Button button = findViewById(R.id.find_selected);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");
                ArrayList<Genre> genreList = dataAdapter.genreList;

                for (int i=0; i<genreList.size(); i++){
                    Genre genre = genreList.get(i);
                    if(genre.getSelected()){
                        responseText.append("\n"+genre.getName());
                    }
                }
                Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkStartRunClicked(){

        Button startRunButton = findViewById(R.id.start_run_button);
        final Intent intent = new Intent(this,RunningScreen.class);
        startRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Genre> genreList = dataAdapter.genreList;
                ArrayList<String> selectedGeneres = new ArrayList<String>();
                for (int i=0; i<genreList.size(); i++){
                    Genre genre = genreList.get(i);
                    if(genre.getSelected()){
                        selectedGeneres.add(genre.getName());
                    }
                }
                intent.putExtra("generes",selectedGeneres);
                startActivity(intent);
            }
        });
    }
}
