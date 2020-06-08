package com.example.commuteapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Objects;

public class RouteFragment extends Fragment
{
    OnBackPressedCallback callback;
    CommuteDataClass thisCommute;
    Context thisContext;



    public RouteFragment(Context context, CommuteDataClass commute)
    {
        thisContext = context;
        thisCommute = commute;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();

                getActivity().findViewById(R.id.commute_recycler_view).setVisibility(View.VISIBLE);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.route_recycler_view_layout, container, false);

        RecyclerView newsFragment_recycler = (RecyclerView) view.findViewById(R.id.route_recycler_view);

        RecyclerView.LayoutManager recyclerManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);

        RecyclerView.Adapter recyclerAdapter = new RouteAdapter(thisCommute, view.getContext(), savedInstanceState);

        newsFragment_recycler.setLayoutManager(recyclerManager);
        newsFragment_recycler.setAdapter(recyclerAdapter);
        newsFragment_recycler.setHasFixedSize(true);


        return view;
    }

}
