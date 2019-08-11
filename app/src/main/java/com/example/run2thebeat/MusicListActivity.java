package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MusicListActivity extends AppCompatActivity {
    String[] GENRE_NAMES = {"Pop", "Avant-Garde", "Electronic", "Hip Hop", "Latin", "Country",
            "Jazz", "Rock", "Rap", "R&B & Soul"};
    MyCustomAdapter dataAdapter = null;
    public static final String PREFS_NAME = "MyPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        displayListView();
        checkStartRunClicked();
    }

    private void displayListView() {

        ArrayList<Genre> genresList = new ArrayList<>();
        for (String genre_name : GENRE_NAMES) {
            Genre genre = new Genre(genre_name, false);
            genresList.add(genre);
        }
        ListView listView = findViewById(R.id.list_view1);

        dataAdapter = new MyCustomAdapter(this, genresList);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Genre genre = genresList.get(position);

                if (genre.getSelected()) {
                    genre.setSelected(false);
                } else {
                    genre.setSelected(true);
                }

                genresList.set(position, genre); // todo; find out what this does

                dataAdapter.updateRecords(genresList);
//                view.setSelected(true);
                Toast.makeText(getApplicationContext(), "clicked on row: " + genre.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyCustomAdapter extends BaseAdapter {
        private ArrayList<Genre> genreList;
        Activity activity;
        LayoutInflater inflater;

        //short to create constructer using command+n for mac & Alt+Insert for window


        public MyCustomAdapter(Activity activity, ArrayList<Genre> list) {
            this.activity = activity;
            this.genreList = list;
            inflater = activity.getLayoutInflater();
        }


        @Override
        public int getCount() {
            return genreList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder = null;

            if (view == null) {

                view = inflater.inflate(R.layout.lv_item, viewGroup, false);

                holder = new ViewHolder();

                holder.genreName = (TextView) view.findViewById(R.id.genre);
                holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);

                view.setTag(holder);
            } else
                holder = (ViewHolder) view.getTag();

            Genre genre = genreList.get(i);
            holder.genreName.setText(genre.getName());
            holder.checkBox.setChecked(genre.getSelected());


            return view;

        }

        public void updateRecords(ArrayList<Genre> genres) {
            this.genreList = genres;
            notifyDataSetChanged();
        }

        class ViewHolder {

            TextView genreName;
            CheckBox checkBox;
        }
    }

    public void checkStartRunClicked() {

        Button startRunButton = findViewById(R.id.start_run_button);
        final Intent intent = new Intent(getBaseContext(), CountDownActivity.class);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        startRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                ArrayList<Genre> genreList = dataAdapter.genreList;
//                ArrayList<String> selectedGenres = new ArrayList<String>();
                for (int i = 0; i < genreList.size(); i++) {
                    Genre genre = genreList.get(i);
                    if (genre.getSelected()) {
                        count++;
                        editor.putString("genre" + String.valueOf(count), genre.getName());
//                        selectedGenres.add(genre.getName());
                        editor.putInt("num_selected", count);
                    }
                }
                editor.apply();
//                intent.putExtra("generes",selectedGenres);
                startActivity(intent);
            }
        });
    }
}