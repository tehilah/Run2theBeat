package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
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
    String[] GENRE_NAMES = {"pop", "avant-garde", "electronic", "hip hop", "latin", "country",
            "jazz", "rock", "rap", "rbandsoul"};
    MyCustomAdapter dataAdapter = null;
    public static final String PREFS_NAME = "MyPref";
    private String TAG = "com.example.run2thebeat.MusicListActivity";
    private boolean allSelected = false;

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

        CheckBox selectAllCheckbox = findViewById(R.id.select_all_button);
        selectAllCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allSelected) {
                    for (int i = 0; i < genresList.size(); i++) {
                        genresList.get(i).setSelected(true);
                    }
                    allSelected =true;
                }
                else {
                    for (int i = 0; i < genresList.size(); i++) {
                        genresList.get(i).setSelected(false);
                    }
                    allSelected = false;
                }
                dataAdapter.updateRecords(genresList);

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

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Genre genre = (Genre) cb.getTag();
                        Toast.makeText(getApplicationContext(), "clicked on Checkbox: " + genre.getName(), Toast.LENGTH_SHORT).show();
                        genre.setSelected(cb.isChecked());
                    }
                });
            } else
                holder = (ViewHolder) view.getTag();

            Genre genre = genreList.get(i);
            holder.genreName.setText(genre.getName());
            holder.checkBox.setChecked(genre.getSelected());
            holder.checkBox.setTag(genre);

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
        final Intent intent = new Intent(this,RunActivity.class);
        startRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Genre> genreList = dataAdapter.genreList;
                ArrayList<String> selectedGenres = new ArrayList<String>();
                for (int i=0; i<genreList.size(); i++){
                    Genre genre = genreList.get(i);
                    Log.d(TAG, "onClick: "+genre.getSelected());
                    if(genre.getSelected()){
                        selectedGenres.add(genre.getName());
                    }
                }
                if(selectedGenres.size() ==0){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select a genre or select all ", Toast.LENGTH_LONG);
                    ViewGroup toastLayout = (ViewGroup) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTextSize(18);
                    toast.show();

                }
                else {
                    intent.putExtra("genres", selectedGenres);
                    startActivity(intent);
                    finish();
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
//    }
}