package com.example.commuteapp;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

public class ScheduledCommuteWorker extends Worker
{
    Context thisContext;
    GoogleMap thisMap;
    Bitmap thisSnapshot;

    public ScheduledCommuteWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
        thisContext = context;
    }

    @NonNull
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
                        //.origin(thisCommute.getFromAddr())
                        .origin("15 Lonsdale St Braddon ACT 2612")
                        .destination(thisCommute.getToAddr())
                        .departureTimeNow()
                        .await();
            } catch (ApiException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        // get stored route
        assert thisCommute != null;
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

        assert storedResults != null;
        DirectionsStep[] storedSteps = storedResults.routes[0].legs[0].steps;

        assert directionsResult != null;
        DirectionsStep[] tmpSteps = directionsResult.routes[0].legs[0].steps;

        boolean foundDetour = false;
        DirectionsStep detourStep = null;

        for(int i = 0; i < storedSteps.length; i++)
        {
            for(int j = 0; j < tmpSteps.length; j++)
            {
                if(storedSteps[i].startLocation.equals(tmpSteps[j].startLocation))
                {
                    if(storedSteps[i].endLocation.equals(tmpSteps[j].endLocation))
                    {
                        Log.d("SCHEDULER", "Got Step match");
                    }
                    else
                    {
                        if(foundDetour){continue;}
                        foundDetour = true;
                        detourStep = tmpSteps[j];
                        Log.d("SCHEDULER", "Detour found, next instruction: " + tmpSteps[j].htmlInstructions);
                    }
                }
                else if(i == 0 && j == 0) // Check first step for different start loc
                {
                    if(foundDetour){continue;}
                    foundDetour = true;
                    detourStep = tmpSteps[j];
                    Log.d("SCHEDULER", "Detour found, next instruction: " + tmpSteps[j].htmlInstructions);
                }
            }
        }

        Intent intent = new Intent(thisContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(thisContext, 0, intent, 0);


        if(foundDetour)
        {
            // generate notification
            // TODO: create dynamic notifications based on route comparison

            NotificationCompat.Builder builder = new NotificationCompat.Builder(thisContext, thisContext.getString(R.string.channel_id))
                    .setLargeIcon(thisSnapshot)
                    .setSmallIcon(R.drawable.commute_notification_cross)
                    .setColor(Color.RED)
                    .setContentTitle("Route from " + thisCommute.getFromAlias() + " to " + thisCommute.getToAlias() +" Delayed")
                    .setContentText(android.text.Html.fromHtml(detourStep.htmlInstructions, Html.FROM_HTML_MODE_COMPACT))
                    //.setStyle(new NotificationCompat.BigTextStyle()
                    //        .bigText("Expected commute time " + storedResults.routes[0].legs[0].duration.humanReadable + " via " + storedResults.routes[0].summary))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(thisContext);
            notificationManagerCompat.notify(0,builder.build());
        }
        else
        {
            // generate notification

            NotificationCompat.Builder builder = new NotificationCompat.Builder(thisContext, thisContext.getString(R.string.channel_id))
                    .setLargeIcon(thisSnapshot)
                    .setSmallIcon(R.drawable.commute_notification_greentick)
                    .setColor(Color.GREEN)
                    .setContentTitle("Usual route from " + thisCommute.getFromAlias() + " to " + thisCommute.getToAlias() + "CLEAR")
                    .setContentText("Expected commute time " + storedResults.routes[0].legs[0].duration.humanReadable + " via " + storedResults.routes[0].summary)
                    //.setStyle(new NotificationCompat.BigTextStyle()
                    //        .bigText("Expected commute time " + storedResults.routes[0].legs[0].duration.humanReadable + " via " + storedResults.routes[0].summary))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(thisContext);
            notificationManagerCompat.notify(0,builder.build());
        }

        if(directionsResult.routes[0].legs[0].startAddress.equals(storedResults.routes[0].legs[0].startAddress))
        {
            Log.d("SCHEDULER", "Found matching routes!");
        }
        else
        {
            Log.d("SCHEDULER", "Routes do not match!");
        }



        // schedule new task


        // finish
        return Result.success();
    }
}
