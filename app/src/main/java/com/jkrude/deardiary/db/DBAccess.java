package com.jkrude.deardiary.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.jkrude.deardiary.db.entities.DayCommCrossRef;
import com.jkrude.deardiary.db.entities.DayComment;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.DayWithComments;

import java.util.List;

@Dao
public interface DBAccess {

    @Transaction
    @Query("SELECT * FROM DayEntity")
    public abstract List<DayEntity> getDayEntities();

    @Transaction
    @Query("SELECT * FROM DayEntity")
    public List<DayWithComments> getDaysWithComments();

    @Insert
    void insertComment(DayComment comment);

    @Delete
    void deleteComment(DayComment comment);

    @Insert
    void insertAllRefs(List<DayCommCrossRef> refs);

    @Insert
    void insertDay(DayEntity dayEntity);

    @Delete
    void delete(DayEntity dayEntity);

}
