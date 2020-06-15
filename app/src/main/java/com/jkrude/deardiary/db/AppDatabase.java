package com.jkrude.deardiary.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jkrude.deardiary.db.entities.BinaryEntry;
import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayCommCrossRef;
import com.jkrude.deardiary.db.entities.DayComment;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.TextEntry;
import com.jkrude.deardiary.db.entities.TimeEntry;

@Database(
        entities = {
                DayEntity.class,
                DayComment.class,
                DayCommCrossRef.class,
                BinaryEntry.class,
                CounterEntry.class,
                TextEntry.class,
                TimeEntry.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DBAccess dbAccess();
}
