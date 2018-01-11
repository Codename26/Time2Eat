package com.example.user.time2eat;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by User on 10.01.2018.
 */

    @Dao
    public interface FoursquareItemDao{
        @android.arch.persistence.room.Query("SELECT * FROM items")
        List<FoursquareItem> getAll();

        @android.arch.persistence.room.Query("SELECT * FROM items WHERE id = :id")
        FoursquareItem getById(String id);

        @Insert
        void insert(FoursquareItem item);

        @Update
        void update(FoursquareItem item);

        @Delete
        void delete(FoursquareItem item);

        @Insert
        void insertAll(FoursquareItem... items);
    }

