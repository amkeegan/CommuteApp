package com.example.commuteapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommuteDisplayAdapter extends RecyclerView.Adapter<CommuteDisplayAdapter.DisplayViewHolder>
{
    static class DisplayViewHolder extends  RecyclerView.ViewHolder
    {

        DisplayViewHolder(View v)
        {
            super(v);
        }
    }

    CommuteDisplayAdapter(Context context)
    {

    }

    @NonNull
    @Override
    public CommuteDisplayAdapter.DisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View displayView = inflater.inflate(R.layout.commute_recycler_item_text_display, parent, false);
        return new DisplayViewHolder(displayView);
    }

    @Override
    public void onBindViewHolder(final DisplayViewHolder holder, final int position)
    {

    }

    @Override
    public int getItemCount() {return 1;}
}
