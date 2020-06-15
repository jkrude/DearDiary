package com.jkrude.deardiary.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Entity(primaryKeys = {"value", "catName"},
        foreignKeys = @ForeignKey(
                entity = DayEntity.class,
                parentColumns = "date_id",
                childColumns = "dayID",
                onDelete = ForeignKey.CASCADE))
@TypeConverters({DayEntity.DateConverter.class, DayEntity.LocalTimeConverter.class})
public class TimeEntry extends EntryForDay<LocalTime> {

    public TimeEntry(@NonNull LocalTime value, @NonNull String catName, @NonNull Date dayID) {
        super(value, catName, dayID);
    }

    public static Map<String, LocalTime> viewAsMap(@NonNull List<TimeEntry> list) {
        return EntryForDay.viewAsMapT(list);
    }

}
