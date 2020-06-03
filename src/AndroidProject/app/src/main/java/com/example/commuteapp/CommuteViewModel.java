package com.example.commuteapp;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CommuteViewModel extends AndroidViewModel
{
    private CommuteRepository repository;

    private LiveData<List<CommuteDataClass>> allCommutes;

    public CommuteViewModel (Application application)
    {
        super(application);
        repository = new CommuteRepository(application);
        allCommutes = repository.getAllCommutes();
    }

    LiveData<List<CommuteDataClass>> getAllCommutes()
    {
        return allCommutes;
    }

    public void insertCommute(CommuteDataClass commute)
    {
        Log.d("VIEWMODEL","Inserting commute");
        repository.insertCommute(commute);
    }
}
