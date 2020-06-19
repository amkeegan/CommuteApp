package com.example.commuteapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        Button sunday;
        Button monday;
        Button tuesday;
        Button wednesday;
        Button thursday;
        Button friday;
        Button saturday;
        Button reminder30;
        Button reminder5;
        Button reminderBT;
        Button reminderAuto;

        MainCommuteViewHolder(View v)
        {
            super(v);
            fromTextView = v.findViewById(R.id.main_textview_from);
            toTextView = v.findViewById(R.id.main_textview_to);
            timeTextView = v.findViewById(R.id.main_textview_time);
            arriveDepartTextView = v.findViewById(R.id.main_textview_arrivedepart);
            routeImageView = v.findViewById(R.id.main_imageview_route);

            sunday = v.findViewById(R.id.btnSunday);
            monday = v.findViewById(R.id.btnMonday);
            tuesday = v.findViewById(R.id.btnTuesday);
            wednesday = v.findViewById(R.id.btnWednesday);
            thursday = v.findViewById(R.id.btnThursday);
            friday = v.findViewById(R.id.btnFriday);
            saturday = v.findViewById(R.id.btnSaturday);
            reminder30 = v.findViewById(R.id.button30);
            reminder5 = v.findViewById(R.id.button5);
            reminderBT = v.findViewById(R.id.buttonBT);
            reminderAuto = v.findViewById(R.id.buttonAuto);

        }
    }

    MainCommuteAdapter(Context context)
    {
        thisContext = context;
    }

    @NonNull
    @Override
    public MainCommuteAdapter.MainCommuteViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View mainCommuteView = inflater.inflate(R.layout.main_recycler_item, parent, false);
        return new MainCommuteViewHolder(mainCommuteView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainCommuteViewHolder holder, final int position)
    {
        if(thisCommutes != null) // Ensure data has loaded from the DB
        {
            // Each item in RecyclerView is represented by a instance from the DB
            CommuteDataClass currentCommute = thisCommutes.get(position);
            holder.fromTextView.setText(currentCommute.getFromAlias());
            holder.toTextView.setText(currentCommute.getToAlias());
            holder.timeTextView.setText(currentCommute.getRouteTime());
            holder.arriveDepartTextView.setText(currentCommute.getRouteArriveDepart());

            // Read the CommuteDataClass and set button views
            setScheduleButtons(holder, currentCommute);
            setReminderButtons(holder, currentCommute);

            // Action when user touches the item.
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {handleCardClick(currentCommute);}
            });
        }
    }

    void handleCardClick(CommuteDataClass commute)
    {
        // Replace this view with the new view with FragmentTransaction.
        FragmentManager fragmentManager = ((AppCompatActivity) thisContext).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, new CardFragment(thisContext, commute));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        RecyclerView recyclerView = ((AppCompatActivity) thisContext).findViewById(R.id.main_recycler_view);
        recyclerView.setVisibility(View.INVISIBLE); // Turn off visibility for MainActivity recyclerView

        // Disable/hide the FAB
        FloatingActionButton fab = ((AppCompatActivity) thisContext).findViewById(R.id.mainFAB);
        fab.setVisibility(View.INVISIBLE);
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
            return thisCommutes.size();
        }
        else
        {
            return 0;
        }
    }

    private void setReminderButtons(MainCommuteViewHolder holder, CommuteDataClass commute)
    {
        // Check each variable in the CommuteDataClass
        if(commute.getReminder30()) // If selected
        {
            // Display 'ON' scheme
            holder.reminder30.setBackgroundResource(R.drawable.commute_reminder_rectangle);
            holder.reminder30.setTextColor(Color.WHITE);
        }
        else // If not selected
        {
            // Display 'OFF' scheme
            holder.reminder30.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
            holder.reminder30.setTextColor(Color.BLACK);
        }
        if(commute.getReminder5())
        {
            holder.reminder5.setBackgroundResource(R.drawable.commute_reminder_rectangle);
            holder.reminder5.setTextColor(Color.WHITE);
        }
        else
        {
            holder.reminder5.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
            holder.reminder5.setTextColor(Color.BLACK);
        }
        if(commute.getReminderBT())
        {
            holder.reminderBT.setBackgroundResource(R.drawable.commute_reminder_rectangle);
            holder.reminderBT.setTextColor(Color.WHITE);
        }
        else
        {
            holder.reminderBT.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
            holder.reminderBT.setTextColor(Color.BLACK);
        }
        if(commute.getReminderAuto())
        {
            holder.reminderAuto.setBackgroundResource(R.drawable.commute_reminder_rectangle);
            holder.reminderAuto.setTextColor(Color.WHITE);
        }
        else
        {
            holder.reminderAuto.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
            holder.reminderAuto.setTextColor(Color.BLACK);
        }
    }

    private void setScheduleButtons(MainCommuteViewHolder holder, CommuteDataClass commute)
    {
        // Check each variable in the CommuteDataClass
        if(commute.getSunday()) // If selected
        {
            // Set 'ON' scheme
            holder.sunday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else // If not selected
        {
            // Set 'OFF' scheme
            holder.sunday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(commute.getMonday())
        {
            holder.monday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.monday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(commute.getTuesday())
        {
            holder.tuesday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.tuesday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(commute.getWednesday())
        {
            holder.wednesday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.wednesday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(commute.getThursday())
        {
            holder.thursday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.thursday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(commute.getFriday())
        {
            holder.friday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.friday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(commute.getSaturday())
        {
            holder.saturday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.saturday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
    }
}
