package com.example.commuteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_recycler_view);

        // Create RecyclerView
        RecyclerView recyclerView = findViewById(R.id.main_recycler_view);

        // Create LayoutManager
        RecyclerView.LayoutManager recyclerManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Create RV Adapter
        RecyclerView.Adapter recyclerAdapter = new MainCommuteAdapter(this);

        // setLayoutManager
        recyclerView.setLayoutManager(recyclerManager);

        // setAdapter
        recyclerView.setAdapter(recyclerAdapter);

        // setHasFixedSize
        recyclerView.setHasFixedSize(true);
    }
}
