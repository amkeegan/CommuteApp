package com.example.commuteapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class MainCommuteAdapter extends RecyclerView.Adapter<MainCommuteAdapter.MainCommuteViewHolder>
{
    private Context thisContext;

    static class MainCommuteViewHolder extends RecyclerView.ViewHolder
    {
        MainCommuteViewHolder(View v)
        {
            super(v);
        }
    }

    MainCommuteAdapter(Context context)
    {
        thisContext = context;
    }

    @Override
    public MainCommuteAdapter.MainCommuteViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View mainCommuteView = inflater.inflate(R.layout.commute_recycler_item_text_display, parent, false);
        return new MainCommuteViewHolder(mainCommuteView);
    }

    @Override
    public void onBindViewHolder(final MainCommuteViewHolder holder, final int position)
    {
        //
    }

    @Override
    public int getItemCount()
    {
        return 1;
    }
}
