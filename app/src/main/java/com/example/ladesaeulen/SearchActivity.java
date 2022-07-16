package com.example.ladesaeulen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static StationManager manager = new StationManager ();
    EditText searchInput;
    RecyclerView recyclerView;
    Switch aSwitch;
    Switch bSwitch;
    boolean click=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        BottomNavigationView  navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.search);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.search:
                        return true;
                }
                return false;

            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StationAdapter stationAdapter;
        ArrayList<ChargingStation> stations= new ArrayList<ChargingStation>();
        ArrayList<ChargingStation> st= new ArrayList<ChargingStation>();

         recyclerView = findViewById(R.id.recyclerview);
         searchInput=findViewById(R.id.search_bar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        aSwitch=findViewById(R.id.nearSwitch);
        bSwitch=findViewById(R.id.favoriteSwitch);

         stationAdapter = new StationAdapter(manager.getStation_list(),SearchActivity.this);
        recyclerView.setAdapter(stationAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stationAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    StationAdapter stationAdapter = new StationAdapter(manager.getNearby_station_storage(),SearchActivity.this);
                    recyclerView.setAdapter(stationAdapter);

                    searchInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            stationAdapter.getFilter().filter(s);

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }

                else
                {

                    StationAdapter stationAdapter = new StationAdapter(manager.getStation_list(),SearchActivity.this);
                    recyclerView.setAdapter(stationAdapter);

                    searchInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            stationAdapter.getFilter().filter(s);

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }



            }
        });

        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    StationAdapter stationAdapter = new StationAdapter(manager.getFavorit_station_storage(),SearchActivity.this);
                    recyclerView.setAdapter(stationAdapter);

                    searchInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            stationAdapter.getFilter().filter(s);

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }

                else
                {

                    StationAdapter stationAdapter = new StationAdapter(manager.getStation_list(),SearchActivity.this);
                    recyclerView.setAdapter(stationAdapter);

                    searchInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            stationAdapter.getFilter().filter(s);

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }



            }
        });








    }
}