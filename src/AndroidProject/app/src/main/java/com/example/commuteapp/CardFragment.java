package com.example.commuteapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class CardFragment extends Fragment
{
    OnBackPressedCallback callback;
    CommuteDataClass thisCommute;
    Context thisContext;

    public CardFragment(Context context, CommuteDataClass commute)
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

                getActivity().findViewById(R.id.main_recycler_view).setVisibility(View.VISIBLE);

                getActivity().findViewById(R.id.mainFAB).setVisibility(View.VISIBLE);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);

        FloatingActionButton mainFAB = getActivity().findViewById(R.id.mainFAB);
        mainFAB.bringToFront();
        mainFAB.setOnClickListener(new FloatingActionButton.OnClickListener()
        {
            public void onClick(View view)
            {
                Log.d("FAB","got onClick");
                FragmentManager fragmentManager = ((AppCompatActivity) thisContext).getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                CommuteDataClass tmpCommute = new CommuteDataClass(
                        "","","","",
                        "","","",
                        false,true,false,
                        false,false,false,false,
                        false,false,false,false);

                fragmentTransaction.replace(R.id.main_fragment, new CardFragment(thisContext, tmpCommute));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                RecyclerView recyclerView = ((AppCompatActivity) thisContext).findViewById(R.id.main_recycler_view);
                recyclerView.setVisibility(View.INVISIBLE); // Turn off visibility for MainActivity recyclerView

                FloatingActionButton fab = ((AppCompatActivity) thisContext).findViewById(R.id.mainFAB);
                fab.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.commute_recycler_view_layout, container, false);

        RecyclerView newsFragment_recycler = (RecyclerView) view.findViewById(R.id.commute_recycler_view);

        RecyclerView.LayoutManager recyclerManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);

        RecyclerView.Adapter recyclerAdapter = new CommuteDisplayAdapter(thisCommute, view.getContext());

        view.setFocusable(true);

        newsFragment_recycler.setLayoutManager(recyclerManager);
        newsFragment_recycler.setAdapter(recyclerAdapter);
        newsFragment_recycler.setHasFixedSize(true);

        return view;
    }
}
