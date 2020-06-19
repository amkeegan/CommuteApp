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

    // For testing / demonstration purposes.
    //  This object can be inserted into the database to populate the RecyclerView and provide
    //  quick access to modifying and testing features.
    CommuteDataClass tmpCommute = new CommuteDataClass(
            "25 Lonsdale St Braddon ACT 2612","Home","5 Isa St Fyshwick ACT 2609","Work",
            "Car","7:00","Depart",
            false,true,true,
            true,true,true,false,
            true,false,false,false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a new notification channel
        //  Although this is called regularly, even after config changes it is acceptable
        //  as the OS handles duplicate calls. i.e. will not overwrite/modify existing channel
        //  with same details.
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

        // Used for testing / demonstration purposes
        //  Inserts the temporary CommuteDataClass object into the database.
        //  Comment this line once desired number of objects are stored, otherwise a new object
        //  is stored during every onCreate
        commuteViewModel.insertCommute(tmpCommute); // Ensure there's always a valid route (for demo only)

        commuteViewModel.getAllCommutes().observe(this, new Observer<List<CommuteDataClass>>() {
            @Override
            public void onChanged(List<CommuteDataClass> commutes) {
                recyclerAdapter.setCommutes(commutes);
            }
        });
    }

    private void createNotificationChannel()
    {
        // Get some application specific values for this Channel
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        String CHANNEL_ID = getString(R.string.channel_id);

        // Set the importance. Different levels affects how the user will see the notification
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

        channel.setDescription(description);
        channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
        notificationManager.cancelAll();
    }
}
