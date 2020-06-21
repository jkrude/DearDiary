package com.jkrude.deardiary.db;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.jkrude.deardiary.Utility;
import com.jkrude.deardiary.Utility.DateConverter;
import com.jkrude.deardiary.db.entities.BinaryEntry;
import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayCommCrossRef;
import com.jkrude.deardiary.db.entities.DayComment;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.DayWithAllEntries;
import com.jkrude.deardiary.db.entities.DayWithComments;
import com.jkrude.deardiary.db.entities.TextEntry;
import com.jkrude.deardiary.db.entities.TimeEntry;
import java.time.LocalDate;
import java.util.List;

@Dao
public interface DBAccess {

    /*
     * Queries
     */

    @Transaction
    @Query("SELECT * FROM DayEntity")
    List<DayEntity> getDayEntities();

    @Transaction
    @Query("SELECT * FROM dayentity WHERE date_id LIKE :date")
    List<DayEntity> getDayEntitiesForDay(String date);

    default List<CounterEntry> getDayEntitiesForDay(LocalDate date) {
        return getCounterEntriesForDate(Utility.DateConverter.fromDate(date));
    }

    @Transaction
    @Query("SELECT * FROM DayEntity")
    List<DayWithComments> getDaysWithComments();

    @Query("SELECT * FROM counterentry" +
        " WHERE counterentry.dayID LIKE :date")
    List<CounterEntry> getCounterEntriesForDate(String date);

    @Query("SELECT * FROM binaryentry" +
        " WHERE binaryentry.dayID LIKE :date")
    List<BinaryEntry> getBinaryEntriesForDate(String date);

    @Query("SELECT * FROM textentry" +
        " WHERE textentry.dayID LIKE :date")
    List<TextEntry> getTextEntriesForDate(String date);

    @Query("SELECT * FROM timeentry" +
        " WHERE timeentry.dayID LIKE :date")
    List<TimeEntry> getTimeEntriesForDate(String date);

    @Query("SELECT comment FROM DayCommCrossRef" +
        " WHERE date_id LIKE :date")
    List<String> getCommentsForDate(String date);

    /*
     * INSERT / DELETE / UPDATE
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComment(DayComment... comment);

    @Delete
    void deleteComment(DayComment... comment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllRefs(DayCommCrossRef... refs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDay(DayEntity... dayEntity);

    @Delete
    void deleteDay(DayEntity dayEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBinaryEntry(BinaryEntry... binaryEntries);

    @Update
    void updateBinaryEntry(BinaryEntry... binaryEntries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCounterEntry(CounterEntry... counterEntries);

    @Update
    void updateCounterEntry(CounterEntry... counterEntries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTextEntry(TextEntry... textEntries);

    @Update
    void updateTextEntry(TextEntry... textEntries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTimeEntry(TimeEntry... timeEntries);

    @Update
    void updateTimeEntry(TimeEntry... timeEntries);


    @Transaction
    @Nullable
    default DayWithAllEntries getEverythingForOneDay(LocalDate date) {
        String dateAsString = DateConverter.fromDate(date);
        if (getDayEntitiesForDay(dateAsString).isEmpty()) {
            return null;
        }
        DayWithAllEntries d = new DayWithAllEntries(date);
        d.date = date;
        d.binaryCategories = BinaryEntry.viewAsMap(getBinaryEntriesForDate(dateAsString));
        d.counterCategories = CounterEntry.viewAsMap(getCounterEntriesForDate(dateAsString));
        d.textCategories = TextEntry.viewAsMap(getTextEntriesForDate(dateAsString));
        d.timeCategories = TimeEntry.viewAsMap(getTimeEntriesForDate(dateAsString));
        d.comments = getCommentsForDate(dateAsString);
        return d;
    }

}
