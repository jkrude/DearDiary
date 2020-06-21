package com.jkrude.deardiary.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;
import com.jkrude.deardiary.Utility;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity(primaryKeys = {"dayID", "catName"},
    foreignKeys = @ForeignKey(
        entity = DayEntity.class,
        parentColumns = "date_id",
        childColumns = "dayID",
        onDelete = ForeignKey.CASCADE))
@TypeConverters(Utility.DateConverter.class)
public class CounterEntry extends EntryForDay<Integer> {

    public CounterEntry(@NonNull Integer value, @NonNull String catName, @NonNull LocalDate dayID) {
        super(value, catName, dayID);
    }

    public static Map<String, Integer> viewAsMap(@NonNull List<CounterEntry> list) {
        return EntryForDay.viewAsMapT(list);
    }

}
