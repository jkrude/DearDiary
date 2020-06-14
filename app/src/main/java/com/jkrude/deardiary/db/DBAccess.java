package com.jkrude.deardiary.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.jkrude.deardiary.db.enteties.DayEntity;

import java.util.List;

@Dao
public interface DBAccess {

    @Transaction
    @Query("SELECT * FROM DayEntity")
    public abstract List<DayEntity> getDayEntities();

    @Insert
    void insertAll(DayEntity dayEntity);

    @Delete
    void delete(DayEntity dayEntity);

}
