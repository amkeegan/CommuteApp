package com.example.commuteapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.DisplayViewHolder> implements OnMapReadyCallback {
    CommuteDataClass thisCommute;
    Context thisContext;
    Bundle thisSavedInstance;

    GoogleMap thisGoogleMap;

    static class DisplayViewHolder extends RecyclerView.ViewHolder {
        Button controlBack;
        Button controlDone;

        MapView routeMap;

        DisplayViewHolder(View v) {
            super(v);
            controlBack = v.findViewById(R.id.buttonBack);
            controlDone = v.findViewById(R.id.buttonSave);
            routeMap = v.findViewById(R.id.routeMap);
        }
    }

    RouteAdapter(CommuteDataClass commute, Context context, Bundle savedInstanceState) {
        thisCommute = commute;
        thisContext = context;
        thisSavedInstance = savedInstanceState;
    }

    @NonNull
    @Override
    public RouteAdapter.DisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View displayView = inflater.inflate(R.layout.route_fragment, parent, false);
        return new DisplayViewHolder(displayView);
    }

    // Automatic callback method when GoogleMap object has been initialised using getMapAsync
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Store a reference to the newly created map object
        thisGoogleMap = googleMap;

        DirectionsResult directionsResult = null;
        boolean loadedFromDB = false; // Used to track if the DB was accessed to create route data

        // Check if routeDirectionsString exists
        if(thisCommute.getRouteDirectionsString() != null)
        {
            // Load routeDirectionsString by deserialising data from DB
            byte[] byteData = Base64.getDecoder().decode(thisCommute.getRouteDirectionsString());
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteData));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert objectInputStream != null;
                directionsResult = (DirectionsResult)objectInputStream.readObject();
                loadedFromDB = true;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If deserialising failed, or was not present (for a new route), generate a new request
        if(directionsResult == null)
        {
            // Create new DirectionsAPI request
            // TODO: point this to a Proxy service and not rely on API within APP.
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey(thisContext.getString(R.string.APIKEY))
                    .build();
            try
            {
                // Create the API request using data from CommuteDataClass object
                // TODO: Implement all user specified inputs, not just origin and destination
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

        // DEBUG information for API request
        Log.d("ROUTEATAPT","directions results.length: " + directionsResult.routes.length);
        Log.d("ROUTEATAPT","directions results: " + directionsResult.routes[0].toString());
        Log.d("ROUTEATAPT","directions results.legs.length: " + directionsResult.routes[0].legs.length);
        Log.d("ROUTEATAPT","directions results.legs.steps.length: " + directionsResult.routes[0].legs[0].steps.length);
        Log.d("ROUTEATAPT","directions waypoints.length: " + directionsResult.geocodedWaypoints.length);
        Log.d("ROUTEATAPT","directions waypoints: " + directionsResult.geocodedWaypoints[0].toString());

        if(!loadedFromDB)
        {
            // Serialise directionsResult into String
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert objectOutputStream != null;
                objectOutputStream.writeObject(directionsResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String tmpResult = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            if(tmpResult != null)
            {
                // Store in Room
                thisCommute.setRouteDirectionsString(tmpResult);
            }
        }

        // Get the polyline attributes from the results
        List<LatLng> decodedPath = PolyUtil.decode(directionsResult.routes[0].overviewPolyline.getEncodedPath());

        // Draw the polyline on the map object
        // TODO: implement polyline features/markers to better display the route.
        thisGoogleMap.addPolyline(new PolylineOptions().addAll(decodedPath));

        // Find the middle of the route, using the two extremes provided by the request.
        // GPS coords allow for a simple average calculation to determine mid-point.
        LatLng resultLatLng = new LatLng(
                - abs((directionsResult.routes[0].bounds.northeast.lat
                        + directionsResult.routes[0].bounds.southwest.lat)
                        / 2),
                abs((directionsResult.routes[0].bounds.northeast.lng
                        + directionsResult.routes[0].bounds.southwest.lng)
                        / 2));

        // Use the mid-point of the route to centre the camera
        // TODO: implement dynamic camera zoom based on route length/width
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(resultLatLng, 12));
    }

    @Override
    public void onBindViewHolder(final DisplayViewHolder holder, final int position)
    {
        // Immediate begin loading the map object with getMapAsync (a background task).
        holder.routeMap.getMapAsync(this);
        holder.routeMap.onCreate(thisSavedInstance);
        holder.routeMap.onResume();

        // Handle Back button, save the current state to the DB and return to edit view
        holder.controlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommuteRepository tmpRepo = new CommuteRepository(((AppCompatActivity) thisContext).getApplication());

                tmpRepo.updateCommute(thisCommute);

                FragmentManager fragmentManager = ((AppCompatActivity) thisContext).getSupportFragmentManager();
                fragmentManager.popBackStack();

                ((AppCompatActivity) thisContext).findViewById(R.id.commute_recycler_view).setVisibility(View.VISIBLE);
            }
        });

        // Handle Done button, schedule work, save to the DB and return to Main view
        holder.controlDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: read EditText values and update thisCommute

                // calculate initial delay to next commute
                // TODO: calculate delay for use in setInitialDelay

                // set scheduled work
                // TODO: change initialDelay to relative time between now and next reminder required
                WorkRequest scheduleCommuteRequest =
                        new OneTimeWorkRequest.Builder(ScheduledCommuteWorker.class)
                                .setInitialDelay(1, TimeUnit.MINUTES)
                                .setInputData(
                                        new Data.Builder()
                                        .putInt("roomID", thisCommute.getId()) // Store reference to DB entity
                                        .build()
                                )
                                .build();
                WorkManager.getInstance(thisContext).enqueue(scheduleCommuteRequest);

                CommuteRepository tmpRepo = new CommuteRepository(((AppCompatActivity) thisContext).getApplication());

                tmpRepo.insertCommute(thisCommute);

                FragmentManager fragmentManager = ((AppCompatActivity) thisContext).getSupportFragmentManager();
                fragmentManager.popBackStack();
                fragmentManager.popBackStack();

                ((AppCompatActivity) thisContext).findViewById(R.id.main_recycler_view).setVisibility(View.VISIBLE);
                ((AppCompatActivity) thisContext).findViewById(R.id.mainFAB).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {return 1;}

}
