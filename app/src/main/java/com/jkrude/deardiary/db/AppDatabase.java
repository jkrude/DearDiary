package com.jkrude.deardiary.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jkrude.deardiary.db.entities.DayCommCrossRef;
import com.jkrude.deardiary.db.entities.DayComment;
import com.jkrude.deardiary.db.entities.DayEntity;

@Database(entities = {DayEntity.class, DayComment.class, DayCommCrossRef.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DBAccess dbAccess();
}
