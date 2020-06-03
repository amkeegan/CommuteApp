package com.example.commuteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    CommuteViewModel commuteViewModel;

    CommuteDataClass tmpCommute = new CommuteDataClass(
            "123","1234","456","1234",
            "1234","1234","1234",
            true,true,true,
            true,true,true,true,
            true,true,true,true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_recycler_view);

        // Create RecyclerView
        RecyclerView recyclerView = findViewById(R.id.main_recycler_view);

        // Create LayoutManager
        RecyclerView.LayoutManager recyclerManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Create RV Adapter
        final MainCommuteAdapter recyclerAdapter = new MainCommuteAdapter(this);

        // setLayoutManager
        recyclerView.setLayoutManager(recyclerManager);

        // setAdapter
        recyclerView.setAdapter(recyclerAdapter);

        // setHasFixedSize
        recyclerView.setHasFixedSize(true);

        commuteViewModel = new ViewModelProvider(this).get(CommuteViewModel.class);

        tmpCommute.setRouteQueryString("123.com");

        commuteViewModel.insertCommute(tmpCommute);


        commuteViewModel.getAllCommutes().observe(this, new Observer<List<CommuteDataClass>>() {
            @Override
            public void onChanged(List<CommuteDataClass> commutes) {
                recyclerAdapter.setCommutes(commutes);
            }
        });

        FloatingActionButton mainFAB = findViewById(R.id.mainFAB);
        mainFAB.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                // Switch to new view;
            }
        });
        Log.d("MAINACT", "MAIN onCreate");
    }
}
