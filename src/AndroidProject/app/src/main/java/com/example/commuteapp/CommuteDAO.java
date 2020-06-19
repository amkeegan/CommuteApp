package com.example.commuteapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Data Access Object used to link Database and program logic, abstracts SQL queries
@Dao
public interface CommuteDAO
{
    // Insert the object and if a conflict is detected, overwrite it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCommute(CommuteDataClass commute);

    @Update
    void updateCommute(CommuteDataClass commute);

    // Simple select all statement
    @Query("SELECT * from commute_data_table")
    LiveData<List<CommuteDataClass>> getAllCommutes();

    // Select just the specified object
    @Query("SELECT * from commute_data_table WHERE id=:id")
    CommuteDataClass getSingleCommute(int id);

    // Delete all data. Only used to reset table for testing / demonstration
    @Query("DELETE FROM commute_data_table")
    void deleteAll();

    // Delete just this commute
    @Delete
    void deleteCommute(CommuteDataClass commute);
}
