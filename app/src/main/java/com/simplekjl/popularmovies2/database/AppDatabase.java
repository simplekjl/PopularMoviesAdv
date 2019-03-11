package com.simplekjl.popularmovies2.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.simplekjl.popularmovies2.network.models.Movie;

@Database(entities = {Movie.class},version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getName();
    private static final Object LOCK    = new Object();
    private static final String DATABASE_NAME = "moviesadv";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,
                        AppDatabase.DATABASE_NAME).build();
            }
        }
        Log.d(LOG_TAG,"Getting the database instance");
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
