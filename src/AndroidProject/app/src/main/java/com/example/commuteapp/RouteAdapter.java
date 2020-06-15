package com.example.commuteapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;
import static java.security.AccessController.getContext;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.DisplayViewHolder> implements OnMapReadyCallback {
    CommuteDataClass thisCommute;
    Context thisContext;
    Bundle thisSavedInstance;

    GoogleMap thisGoogleMap;

    static class DisplayViewHolder extends RecyclerView.ViewHolder {
        Button controlBack;
        Button controlDone;

        MapView routeMap;
        Fragment routeMapFragment;

        DisplayViewHolder(View v) {
            super(v);
            controlBack = v.findViewById(R.id.buttonBack);
            controlDone = v.findViewById(R.id.buttonSave);


            routeMap = v.findViewById(R.id.routeMap);
            if(routeMap == null)
            {
                Log.d("ROUTEADAPT","routeMap == null");
            }
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
        Log.d("ROUTEADAPTER", "Inflating route_fragment");
        return new DisplayViewHolder(displayView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        DirectionsResult directionsResult = null;
        Boolean loadedFromDB = false;

        // Check if routeDirectionsString exists
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
                directionsResult = (DirectionsResult)objectInputStream.readObject();
                loadedFromDB = true;
                Log.d("ROUTEADAPT","directionsResults loaded from bytes");
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(directionsResult == null)
        {
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
                Log.d("ROUTEADAPT","directions error: " + e.toString());
            }
            Log.d("ROUTEADAPT","directionsResults newly created");
        }

        if(directionsResult == null)
        {
            Log.d("ROUTEADAPT","directionsResults == null");
        }
        else
        {
            thisGoogleMap = googleMap;

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
                    Log.d("ROUTEATAPT","Serialised string: " + tmpResult);
                }
            }
            List<LatLng> decodedPath = PolyUtil.decode(directionsResult.routes[0].overviewPolyline.getEncodedPath());
            thisGoogleMap.addPolyline(new PolylineOptions().addAll(decodedPath));

            LatLng resultLatLng = new LatLng(
                    - abs((directionsResult.routes[0].bounds.northeast.lat
                            + directionsResult.routes[0].bounds.southwest.lat)
                            / 2),
                    abs((directionsResult.routes[0].bounds.northeast.lng
                            + directionsResult.routes[0].bounds.southwest.lng)
                            / 2));
            Log.d("ROUTEADAPT","onMapReady, new latlng: " + resultLatLng.toString());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(resultLatLng, 12));

            Log.d("ROUTEADAPT","onMapReady, marker added");
        }
    }

    @Override
    public void onBindViewHolder(final DisplayViewHolder holder, final int position)
    {
        if(holder.routeMap == null)
        {
            Log.d("ROUTEADAPT","holder.routeMap == null");
        }

        holder.routeMap.getMapAsync(this);
        holder.routeMap.onCreate(thisSavedInstance);
        holder.routeMap.onResume();

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

        holder.controlDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
