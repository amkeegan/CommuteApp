package com.example.commuteapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class CardFragment extends Fragment
{
    OnBackPressedCallback callback;
    CommuteDataClass thisCommute;

    public CardFragment(CommuteDataClass commute)
    {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.commute_recycler_view, container, false);

        RecyclerView newsFragment_recycler = (RecyclerView) view.findViewById(R.id.commute_display_fragment);

        RecyclerView.LayoutManager recyclerManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);

        RecyclerView.Adapter recyclerAdapter = new CommuteDisplayAdapter(view.getContext());

        newsFragment_recycler.setLayoutManager(recyclerManager);
        newsFragment_recycler.setAdapter(recyclerAdapter);
        newsFragment_recycler.setHasFixedSize(true);

        return view;
    }
}
