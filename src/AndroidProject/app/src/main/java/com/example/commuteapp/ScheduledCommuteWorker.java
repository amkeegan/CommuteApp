package com.example.commuteapp;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
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

    // When OS executes the task it fires this method.
    @NonNull
    @Override
    public Result doWork()
    {
        DirectionsResult directionsResult = null;
        DirectionsResult storedResults = null;
        CommuteDataClass thisCommute = null;

        // get reference to DB entity
        int tmpRoomId = this.getInputData().getInt("roomID",-1);

        if (tmpRoomId != -1)
        {
            // Retrieve entity from DB using ID from task data
            CommuteRepository tmpRepo = new CommuteRepository((Application)thisContext);
            thisCommute = tmpRepo.getSingleCommute(tmpRoomId);
            assert thisCommute != null;

            // Create new DirectionsAPI request
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey(thisContext.getString(R.string.APIKEY))
                    .build();
            try
            {
                // Calculate NEW route request (to compare with old)
                directionsResult = DirectionsApi.newRequest(geoApiContext)
                        .mode(TravelMode.DRIVING)
                        .origin(thisCommute.getFromAddr())
                        //.origin("15 Lonsdale St Braddon ACT 2612") // Used for demonstration of detour
                        .destination(thisCommute.getToAddr())
                        .departureTimeNow()
                        .await();
            } catch (ApiException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        // get stored route and deserialise
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

        // Assumes only one route, with only one leg
        assert storedResults != null;
        DirectionsStep[] storedSteps = storedResults.routes[0].legs[0].steps;

        // Assumes only one route, with only one leg
        assert directionsResult != null;
        DirectionsStep[] tmpSteps = directionsResult.routes[0].legs[0].steps;

        boolean foundDetour = false;
        DirectionsStep detourStep = null;

        // iterate through all steps
        for(int i = 0; i < storedSteps.length; i++)
        {
            // For each stored step, find the corresponding new step
            for(int j = 0; j < tmpSteps.length; j++)
            {
                if(storedSteps[i].startLocation.equals(tmpSteps[j].startLocation))
                {
                    // When corresponding step (from start location) does not match end location flag it
                    if(!storedSteps[i].endLocation.equals(tmpSteps[j].endLocation))
                    {
                        if(foundDetour){continue;}
                        foundDetour = true;
                        detourStep = tmpSteps[j];
                    }
                }
                else if(i == 0 && j == 0) // Check first step for different start loc
                {
                    if(foundDetour){continue;}
                    foundDetour = true;
                    detourStep = tmpSteps[j];
                }
            }
        }

        // Assign Intent to allow interaction from generated Notification
        // When user clicks notification the app is opened using MainActivity
        Intent intent = new Intent(thisContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(thisContext, 0, intent, 0);

        // If a detour is found then create a specific notification
        if(foundDetour)
        {
            // generate notification
            // TODO: create dynamic notifications based on route comparison

            NotificationCompat.Builder builder = new NotificationCompat.Builder(thisContext, thisContext.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.commute_notification_cross)
                    .setColor(Color.RED)
                    // Set dynamic text to indicate which route was calculated
                    .setContentTitle("Route from " + thisCommute.getFromAlias() + " to " + thisCommute.getToAlias() +" Delayed")
                    // Use the detour step's instruction for navigation guidance
                    .setContentText(android.text.Html.fromHtml(detourStep.htmlInstructions, Html.FROM_HTML_MODE_COMPACT))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(thisContext);
            notificationManagerCompat.notify(0,builder.build());
        }
        else // No detour, so assume usual route is clear. Display basic route information
        {
            // generate notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(thisContext, thisContext.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.commute_notification_greentick)
                    .setColor(Color.GREEN)
                    .setContentTitle("Usual route from " + thisCommute.getFromAlias() + " to " + thisCommute.getToAlias() + " Clear")
                    // Display contextual information about the route
                    .setContentText("Expected commute time " + storedResults.routes[0].legs[0].duration.humanReadable + " via " + storedResults.routes[0].summary)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(thisContext);
            notificationManagerCompat.notify(0,builder.build());
        }

        // schedule new task
        // TODO: lookup thisCommute to find next scheduled occurrence and create new task
        /*
        WorkRequest scheduleCommuteRequest =
                new OneTimeWorkRequest.Builder(ScheduledCommuteWorker.class)
                        .setInitialDelay(1, TimeUnit.MINUTES)
                        .setInputData(
                                new Data.Builder()
                                .putInt("roomID", thisCommute.getId())
                                .build()
                        )
                        .build();
         WorkManager.getInstance(thisContext).enqueue(scheduleCommuteRequest);
         */

        // finish, needed to flag to OS that task is complete.
        return Result.success();
    }
}
