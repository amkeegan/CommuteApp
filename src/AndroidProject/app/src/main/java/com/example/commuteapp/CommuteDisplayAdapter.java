package com.example.commuteapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommuteDisplayAdapter extends RecyclerView.Adapter<CommuteDisplayAdapter.DisplayViewHolder>
{
    CommuteDataClass thisCommute;
    Context thisContext;

    boolean inEditMode = false; // Used to unlock all User input fields

    static class DisplayViewHolder extends  RecyclerView.ViewHolder
    {
        Spinner arriveDepart;
        Spinner mode;
        EditText origAddr;
        EditText endAlias;
        EditText destAddr;
        EditText startAlias;
        EditText time;
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
        Button controlBack;
        Button controlEdit;
        Button controlRoute;
        Button controlDelete;

        DisplayViewHolder(View v)
        {
            super(v);
            arriveDepart = v.findViewById(R.id.spinnerArriveDepart);
            mode = v.findViewById(R.id.spinnerMode);
            origAddr = v.findViewById(R.id.editTextOrigAddr);
            endAlias = v.findViewById(R.id.editTextEndAlias);
            destAddr = v.findViewById(R.id.editTextDestAddr);
            startAlias = v.findViewById(R.id.editTextStartAlias);
            time = v.findViewById(R.id.editTextTime);
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
            controlBack = v.findViewById(R.id.buttonBack);
            controlEdit = v.findViewById(R.id.buttonEdit);
            controlRoute = v.findViewById(R.id.buttonRoute);
            controlDelete = v.findViewById(R.id.buttonDelete);
        }
    }

    CommuteDisplayAdapter(CommuteDataClass commute, Context context)
    {
        thisCommute = commute;
        thisContext = context;
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
        // Update User input fields with data from the CommuteDataClass object (if present).

        // For some fields, if no data is present a hint can be displayed.
        String tmpAddr = thisCommute.getFromAddr();
        if(tmpAddr.equals(""))
        {
            holder.origAddr.setHint("Originating address");
        }
        else
        {
            holder.origAddr.setText(tmpAddr);
        }
        tmpAddr = thisCommute.getToAddr();
        if(tmpAddr.equals(""))
        {
            holder.destAddr.setHint("Destination address");
        }
        else
        {
            holder.destAddr.setText(tmpAddr);
        }
        String tmpAlias = thisCommute.getFromAlias();
        if(tmpAlias.equals(""))
        {
            holder.startAlias.setHint("Originating alias");
        }
        else
        {
            holder.startAlias.setText(tmpAlias);
        }
        tmpAlias = thisCommute.getToAlias();
        if(tmpAlias.equals(""))
        {
            holder.endAlias.setHint("Destination alias");
        }
        else
        {
            holder.endAlias.setText(tmpAlias);
        }

        // Create the options for the Spinner object.
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Car");
        arrayList.add("Public Transport");
        arrayList.add("Walking");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.mode.setAdapter(arrayAdapter);

        // If present, set the Transport Mode option
        String tmpMode = thisCommute.getTransportMode();
        if(tmpMode.equals(""))
        {
            holder.mode.setSelection(0); // Need to handle empty mode
        }
        else if(tmpMode.equals("Car"))
        {
            holder.mode.setSelection(0);
        }
        else if(tmpMode.equals("Public Transport"))
        {
            holder.mode.setSelection(1);
        }
        else if(tmpMode.equals("Walking"))
        {
            holder.mode.setSelection(2);
        }
        else
        {
            holder.mode.setSelection(0); // Need to handle incorrect type
        }

        // Same as previous Spinner object, create the options.
        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("Arrive By");
        arrayList2.add("Depart After");
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_item, arrayList2);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.arriveDepart.setAdapter(arrayAdapter2);

        // Set the values for the Spinner
        String tmpArriveDepart = thisCommute.getRouteArriveDepart();
        if(tmpArriveDepart.equals(""))
        {
            holder.arriveDepart.setSelection(0); // Need to handle empty mode
        }
        else if(tmpArriveDepart.equals("Arrive By"))
        {
            holder.arriveDepart.setSelection(1);
        }
        else if(tmpArriveDepart.equals("Depart After"))
        {
            holder.arriveDepart.setSelection(2);
        }
        else
        {
            holder.arriveDepart.setSelection(0); // Need to handle incorrect type
        }

        // Implement a more user friendly method of setting the time with this TimePickerDialog widget.
        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inEditMode)
                {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(thisContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            // TODO: Fix time display (does not handle minutes = 0, displays x:0, not x:00.
                            holder.time.setText( selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            }
        });

        String tmpTime = thisCommute.getRouteTime();
        if(tmpTime.equals(""))
        {
            holder.time.setHint("Time");
        }
        else
        {
            holder.time.setText(tmpTime);
        }

        // Read the CommuteDataClass and set button views
        setScheduleButtons(holder);
        setReminderButtons(holder);

        // Ensure User cannot initially change fields, until Edit is pressed.
        toggleEdit(holder, false); // Initially set to false

        // Edit button used to unlock editing for input fields
        holder.controlEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inEditMode)
                {
                    inEditMode = true;
                    toggleEdit(holder, true);
                    Log.d("COMMAD","Allowing editing");
                }
                else
                {
                    // Nothing, user must proceed to Route, or Back
                    // Back will save changes.
                    // TODO: improve Usability, it is not clear to User what each action achieves
                    // Route will attempt to process changes.
                }
            }
        });

        // Back button used to backtrack to main RecyclerView
        holder.controlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get a reference to the Database
                CommuteRepository tmpRepo = new CommuteRepository(((AppCompatActivity) thisContext).getApplication());

                // Extract any updated fields and update the CommuteDataClass object
                // TODO: implement extraction for all user input fields
                thisCommute.setFromAddr(holder.origAddr.getText().toString());
                thisCommute.setToAddr(holder.destAddr.getText().toString());

                // Update the database with this entity (to save any changes).
                tmpRepo.updateCommute(thisCommute);

                FragmentManager fragmentManager = ((AppCompatActivity) thisContext).getSupportFragmentManager();
                fragmentManager.popBackStack();

                ((AppCompatActivity) thisContext).findViewById(R.id.main_recycler_view).setVisibility(View.VISIBLE);

                ((AppCompatActivity) thisContext).findViewById(R.id.mainFAB).setVisibility(View.VISIBLE);
            }
        });

        // Delete button removes the current CommuteDataClass from the database and returns the user
        //  to the main RecyclerView
        holder.controlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommuteRepository tmpRepo = new CommuteRepository(((AppCompatActivity) thisContext).getApplication());

                tmpRepo.deleteCommute(thisCommute);
                FragmentManager fragmentManager = ((AppCompatActivity) thisContext).getSupportFragmentManager();
                fragmentManager.popBackStack();

                ((AppCompatActivity) thisContext).findViewById(R.id.main_recycler_view).setVisibility(View.VISIBLE);

                ((AppCompatActivity) thisContext).findViewById(R.id.mainFAB).setVisibility(View.VISIBLE);
            }
        });

        // Route button saves the user input and changes view to where the route is calculated / presented.
        holder.controlRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommuteRepository tmpRepo = new CommuteRepository(((AppCompatActivity) thisContext).getApplication());

                // TODO: implement field extraction for all fields
                thisCommute.setFromAddr(holder.origAddr.getText().toString());
                thisCommute.setToAddr(holder.destAddr.getText().toString());

                tmpRepo.updateCommute(thisCommute);

                FragmentManager fragmentManager = ((AppCompatActivity) thisContext).getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.commute_fragment, new RouteFragment(thisContext, thisCommute));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                RecyclerView recyclerView = ((AppCompatActivity) thisContext).findViewById(R.id.commute_recycler_view);
                recyclerView.setVisibility(View.INVISIBLE); // Turn off visibility for MainActivity recyclerView
            }
        });
    }

    private void setReminderButtons(DisplayViewHolder holder)
    {
        // Each field in the CommuteDataClass object is checked and used to toggle the displayed layout
        if(thisCommute.getReminder30()) // If selected
        {
            // Display the 'ON' scheme
            holder.reminder30.setBackgroundResource(R.drawable.commute_reminder_rectangle);
            holder.reminder30.setTextColor(Color.WHITE);
        }
        else // If not selected
        {
            // Display the 'OFF' scheme
            holder.reminder30.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
            holder.reminder30.setTextColor(Color.BLACK);
        }
        if(thisCommute.getReminder5())
        {
            holder.reminder5.setBackgroundResource(R.drawable.commute_reminder_rectangle);
            holder.reminder5.setTextColor(Color.WHITE);
        }
        else
        {
            holder.reminder5.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
            holder.reminder5.setTextColor(Color.BLACK);
        }
        if(thisCommute.getReminderBT())
        {
            holder.reminderBT.setBackgroundResource(R.drawable.commute_reminder_rectangle);
            holder.reminderBT.setTextColor(Color.WHITE);
        }
        else
        {
            holder.reminderBT.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
            holder.reminderBT.setTextColor(Color.BLACK);
        }
        if(thisCommute.getReminderAuto())
        {
            holder.reminderAuto.setBackgroundResource(R.drawable.commute_reminder_rectangle);
            holder.reminderAuto.setTextColor(Color.WHITE);
        }
        else
        {
            holder.reminderAuto.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
            holder.reminderAuto.setTextColor(Color.BLACK);
        }

        // Each button needs a listener to allow for toggling the layout and updating the CommuteDataClass entity.
        holder.reminder30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReminderButton(holder.reminder30, thisCommute.getReminder30());
                thisCommute.setReminder30(!thisCommute.getReminder30());
            }
        });
        holder.reminder5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReminderButton(holder.reminder5, thisCommute.getReminder5());
                thisCommute.setReminder5(!thisCommute.getReminder5());
            }
        });
        holder.reminderAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReminderButton(holder.reminderAuto, thisCommute.getReminderAuto());
                thisCommute.setReminderAuto(!thisCommute.getReminderAuto());
            }
        });
        holder.reminderBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReminderButton(holder.reminderBT, thisCommute.getReminderBT());
                thisCommute.setReminderBT(!thisCommute.getReminderBT());
            }
        });
    }

    private void toggleReminderButton(Button button, boolean turnOn)
    {
        if(inEditMode)
        {
            if(!turnOn)
            {
                button.setBackgroundResource(R.drawable.commute_reminder_rectangle);
                button.setTextColor(Color.WHITE);
            }
            else
            {
                button.setBackgroundResource(R.drawable.commute_reminder_rectangle_off);
                button.setTextColor(Color.BLACK);
            }
        }
    }

    private void setScheduleButtons(DisplayViewHolder holder)
    {
        if(thisCommute.getSunday())
        {
            holder.sunday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.sunday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(thisCommute.getMonday())
        {
            holder.monday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.monday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(thisCommute.getTuesday())
        {
            holder.tuesday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.tuesday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(thisCommute.getWednesday())
        {
            holder.wednesday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.wednesday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(thisCommute.getThursday())
        {
            holder.thursday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.thursday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(thisCommute.getFriday())
        {
            holder.friday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.friday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }
        if(thisCommute.getSaturday())
        {
            holder.saturday.setBackgroundResource(R.drawable.commute_schedule_circle);
        }
        else
        {
            holder.saturday.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
        }

        holder.sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inEditMode){return;}
                toggleScheduleButton(holder.sunday, thisCommute.getSunday());
                thisCommute.setSunday(!thisCommute.getSunday());
            }
        });
        holder.monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inEditMode){return;}
                toggleScheduleButton(holder.monday, thisCommute.getMonday());
                thisCommute.setMonday(!thisCommute.getMonday());
            }
        });
        holder.tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inEditMode){return;}
                toggleScheduleButton(holder.tuesday, thisCommute.getTuesday());
                thisCommute.setTuesday(!thisCommute.getTuesday());
            }
        });
        holder.wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inEditMode){return;}
                toggleScheduleButton(holder.wednesday, thisCommute.getWednesday());
                thisCommute.setWednesday(!thisCommute.getWednesday());
            }
        });
        holder.thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inEditMode){return;}
                toggleScheduleButton(holder.thursday, thisCommute.getThursday());
                thisCommute.setThursday(!thisCommute.getThursday());
            }
        });
        holder.friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inEditMode){return;}
                toggleScheduleButton(holder.friday, thisCommute.getFriday());
                thisCommute.setFriday(!thisCommute.getFriday());
            }
        });
        holder.saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inEditMode){return;}
                toggleScheduleButton(holder.saturday, thisCommute.getSaturday());
                thisCommute.setSaturday(!thisCommute.getSaturday());
            }
        });
    }

    private void toggleScheduleButton(Button button, boolean turnOn)
    {
        if(inEditMode)
        {
            if(!turnOn)
            {
                button.setBackgroundResource(R.drawable.commute_schedule_circle);
            }
            else
            {
                button.setBackgroundResource(R.drawable.commute_schedule_cirle_off);
            }
        }
    }

    private void toggleEdit(DisplayViewHolder holder, boolean allowEditing)
    {
        if(allowEditing)
        {
            holder.arriveDepart.setEnabled(true);
            holder.mode.setEnabled(true);
            holder.origAddr.setInputType(1);
            holder.endAlias.setInputType(1);
            holder.destAddr.setInputType(1);
            holder.startAlias.setInputType(1);
            holder.time.setInputType(1);
        }
        else // disable all editing
        {
            holder.arriveDepart.setEnabled(false);
            holder.mode.setEnabled(false);
            holder.origAddr.setInputType(0);
            holder.endAlias.setInputType(0);
            holder.destAddr.setInputType(0);
            holder.startAlias.setInputType(0);
            holder.time.setInputType(0);
        }
    }

    // Needed as it is shown in a RecyclerView however only one view is ever inflated.
    @Override
    public int getItemCount() {return 1;}
}
