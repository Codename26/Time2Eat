package com.example.user.time2eat;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {FoursquareItem.class}, version = 1)
public abstract class FoursquareItemDatabase extends RoomDatabase {
    public abstract FoursquareItemDao foursquareItemDao();
    private static FoursquareItemDatabase INSTANCE;

    public static FoursquareItemDatabase getFoursquareItemDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FoursquareItemDatabase.class,
                    "database").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

}
