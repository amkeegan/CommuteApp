package com.example.commuteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    CommuteViewModel commuteViewModel;

    CommuteDataClass tmpCommute = new CommuteDataClass(
            "25 Lonsdale St Braddon ACT 2612","Home","5 Isa St Fyshwick ACT 2609","Work",
            "Car","7:00","Depart",
            false,true,true,
            true,true,true,false,
            true,false,false,false);

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

        //tmpCommute.setRouteDirectionsString("");

        //commuteViewModel.insertCommute(tmpCommute);

        commuteViewModel.getAllCommutes().observe(this, new Observer<List<CommuteDataClass>>() {
            @Override
            public void onChanged(List<CommuteDataClass> commutes) {
                recyclerAdapter.setCommutes(commutes);
                Log.d("ROOMDB", "MAIN onChanged");
            }
        });
    }
}
