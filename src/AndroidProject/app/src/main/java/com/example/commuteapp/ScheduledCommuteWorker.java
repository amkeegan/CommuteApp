package com.example.commuteapp;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

public class ScheduledCommuteWorker extends Worker
{
    Context thisContext;

    public ScheduledCommuteWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
        thisContext = context;
    }

    @Override
    public Result doWork()
    {
        DirectionsResult directionsResult = null;
        DirectionsResult storedResults = null;
        CommuteDataClass thisCommute = null;

        // calculate current route
        int tmpRoomId = this.getInputData().getInt("roomID",-1);
        if (tmpRoomId != -1)
        {
            CommuteRepository tmpRepo = new CommuteRepository((Application)thisContext);
            thisCommute = tmpRepo.getSingleCommute(tmpRoomId);
            assert thisCommute != null;

            // Create new DirectionsAPI request
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey(thisContext.getString(R.string.APIKEY))
                    .build();
            try
            {
                directionsResult = DirectionsApi.newRequest(geoApiContext)
                        .mode(TravelMode.DRIVING)
                        .origin(thisCommute.getFromAddr())
                        .destination(thisCommute.getToAddr())
                        .departureTimeNow()
                        .await();
            } catch (ApiException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        // get stored route
        if(thisCommute.getRouteDirectionsString() != null)
        {
            // Load routeDirectionsString
            byte[] byteData = Base64.getDecoder().decode(thisCommute.getRouteDirectionsString());
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteData));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert objectInputStream != null;
                storedResults = (DirectionsResult)objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // compare new with stored route
        // TODO: create proper route comparison logic
        if(directionsResult.routes[0].legs[0].startAddress.equals(storedResults.routes[0].legs[0].startAddress))
        {
            Log.d("SCHEDULER", "Found matching routes!");
        }
        else
        {
            Log.d("SCHEDULER", "Routes do not match!");
        }

        // generate notification
        // TODO: create dynamic notifications based on route comparison
        Intent intent = new Intent(thisContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(thisContext, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(thisContext, thisContext.getString(R.string.channel_id))
                //.setLargeIcon(BitmapFactory.decodeResource(thisContext.getResources(), R.drawable.commute_notification_greentick))
                .setSmallIcon(R.drawable.commute_notification_greentick)
                .setColor(Color.GREEN)
                .setContentTitle("Usual route from Home to Work CLEAR")
                .setContentText("Expected commute time 12 minutes via Main St.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Expected commute time 12 minutes via Main St."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(thisContext);
        notificationManagerCompat.notify(0,builder.build());

        // schedule new task


        // finish
        return Result.success();
    }
}
