package com.example.commuteapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database( entities = {CommuteDataClass.class}, version = 1, exportSchema = false)
public abstract class CommuteDatabase extends RoomDatabase
{
    public abstract  CommuteDAO commuteDAO();

    private static volatile CommuteDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static CommuteDatabase.Callback sCommuteDatabaseCallback = new CommuteDatabase.Callback()
    {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db)
        {
            super.onOpen(db);

            databaseWriteExecutor.execute(() ->
            {
                CommuteDAO dao = INSTANCE.commuteDAO();
                //dao.deleteAll();
            });
        }
    };

    static CommuteDatabase getDatabase(final Context context)
    {
        if(INSTANCE == null)
        {
            synchronized (CommuteDatabase.class)
            {
                if(INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CommuteDatabase.class, "commute_database")
                            .addCallback(sCommuteDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
