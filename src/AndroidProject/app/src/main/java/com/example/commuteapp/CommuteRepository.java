package com.example.commuteapp;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

class CommuteRepository
{
    private CommuteDAO commuteDAO;
    private LiveData<List<CommuteDataClass>> allCommutes;

    CommuteRepository(Application application)
    {
        CommuteDatabase db = CommuteDatabase.getDatabase(application);
        commuteDAO = db.commuteDAO();
        allCommutes = commuteDAO.getAllCommutes();
    }

    LiveData<List<CommuteDataClass>> getAllCommutes()
    {
        return allCommutes;
    }

    CommuteDataClass getSingleCommute(int id)
    {
        return (CommuteDataClass)commuteDAO.getSingleCommute(id);
    }

    void insertCommute(final CommuteDataClass commute)
    {
        CommuteDatabase.databaseWriteExecutor.execute(() -> commuteDAO.insertCommute(commute));
    }

    void updateCommute(final CommuteDataClass commute)
    {
        CommuteDatabase.databaseWriteExecutor.execute(() -> commuteDAO.updateCommute(commute));
    }

    void deleteCommute(final CommuteDataClass commute)
    {
        CommuteDatabase.databaseWriteExecutor.execute(() -> commuteDAO.deleteCommute(commute));
    }
}
