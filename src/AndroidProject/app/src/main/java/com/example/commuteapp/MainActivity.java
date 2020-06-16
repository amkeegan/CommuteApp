package com.example.commuteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
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

        createNotificationChannel();

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

    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            String CHANNEL_ID = getString(R.string.channel_id);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.cancelAll();
        }
    }
}
