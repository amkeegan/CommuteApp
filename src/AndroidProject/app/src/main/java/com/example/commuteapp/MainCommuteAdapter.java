package com.example.commuteapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainCommuteAdapter extends RecyclerView.Adapter<MainCommuteAdapter.MainCommuteViewHolder>
{
    private Context thisContext;

    private List<CommuteDataClass> thisCommutes;

    static class MainCommuteViewHolder extends RecyclerView.ViewHolder
    {
        TextView fromTextView;
        TextView toTextView;
        TextView timeTextView;
        TextView arriveDepartTextView;
        ImageView routeImageView;

        MainCommuteViewHolder(View v)
        {
            super(v);
            fromTextView = v.findViewById(R.id.main_textview_from);
            toTextView = v.findViewById(R.id.main_textview_to);
            timeTextView = v.findViewById(R.id.main_textview_time);
            arriveDepartTextView = v.findViewById(R.id.main_textview_arrivedepart);
            routeImageView = v.findViewById(R.id.main_imageview_route);
            Log.d("COMMVIEW", "ViewHolder constructor");
        }
    }

    MainCommuteAdapter(Context context)
    {
        thisContext = context;
        Log.d("COMMADAPT", "Adapter constructor");
    }

    @NonNull
    @Override
    public MainCommuteAdapter.MainCommuteViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View mainCommuteView = inflater.inflate(R.layout.main_recycler_item, parent, false);
        Log.d("CREATEVIEW", "Adapter onCreateViewHolder");
        return new MainCommuteViewHolder(mainCommuteView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainCommuteViewHolder holder, final int position)
    {
        if(thisCommutes != null)
        {
            CommuteDataClass currentCommute = thisCommutes.get(position);
            holder.fromTextView.setText(currentCommute.getFromAddr());
            holder.toTextView.setText(currentCommute.getToAddr());
            holder.timeTextView.setText(currentCommute.getRouteTime());
            holder.arriveDepartTextView.setText(currentCommute.getRouteArriveDepart());
            Log.d("BINDVIEW", "thisCommutes != null");
        }
        else
        {
            Log.d("BINDVIEW", "thisCommutes == null");
        }
    }

    void setCommutes(List<CommuteDataClass> commutes)
    {
        thisCommutes = commutes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        if(thisCommutes != null)
        {
            Log.d("ITEMCOUNT", "thisCommutes size: " + thisCommutes.size());
            return thisCommutes.size();
        }
        else
        {
            Log.d("ITEMCOUNT", "thisCommutes == null; returning 0");
            return 0;
        }
    }
}
