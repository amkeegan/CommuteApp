package com.example.commuteapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommuteDAO
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCommute(CommuteDataClass commute);

    @Query("SELECT * from commute_data_table")
    LiveData<List<CommuteDataClass>> getAllCommutes();

    @Query("DELETE FROM commute_data_table")
    void deleteAll();
}
