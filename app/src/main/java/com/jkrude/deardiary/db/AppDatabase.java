package com.jkrude.deardiary.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jkrude.deardiary.db.enteties.DayComment;
import com.jkrude.deardiary.db.enteties.DayEntity;

@Database(entities = {DayEntity.class, DayComment.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DBAccess dbAccess();
}
