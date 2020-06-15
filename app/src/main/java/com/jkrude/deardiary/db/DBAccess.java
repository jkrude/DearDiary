package com.jkrude.deardiary.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jkrude.deardiary.db.entities.BinaryEntry;
import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayCommCrossRef;
import com.jkrude.deardiary.db.entities.DayComment;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.DayWithAllEntries;
import com.jkrude.deardiary.db.entities.DayWithComments;
import com.jkrude.deardiary.db.entities.TextEntry;
import com.jkrude.deardiary.db.entities.TimeEntry;

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
    @Query("SELECT * FROM DayEntity")
    List<DayWithComments> getDaysWithComments();

    @Query("SELECT * FROM counterentry" +
            " WHERE counterentry.dayID LIKE :date")
    List<CounterEntry> getCounterEntriesForDate(long date);

    @Query("SELECT * FROM binaryentry" +
            " WHERE binaryentry.dayID LIKE :date")
    List<BinaryEntry> getBinaryEntriesForDate(long date);

    @Query("SELECT * FROM textentry" +
            " WHERE textentry.dayID LIKE :date")
    List<TextEntry> getTextEntriesForDate(long date);

    @Query("SELECT * FROM timeentry" +
            " WHERE timeentry.dayID LIKE :date")
    List<TimeEntry> getTimeEntriesForDate(long date);

    @Query("SELECT comment FROM DayCommCrossRef" +
            " WHERE date_id LIKE :date")
    List<String> getCommentsForDate(long date);

    /*
     * INSERT / DELETE / UPDATE
     */

    @Insert
    void insertComment(DayComment... comment);

    @Delete
    void deleteComment(DayComment... comment);

    @Insert
    void insertAllRefs(DayCommCrossRef... refs);

    @Insert
    void insertDay(DayEntity... dayEntity);

    @Delete
    void deleteDay(DayEntity dayEntity);

    @Insert
    void insertBinaryEntry(BinaryEntry... binaryEntries);

    @Update
    void updateBinaryEntry(BinaryEntry... binaryEntries);

    @Insert
    void insertCounterEntry(CounterEntry... counterEntries);

    @Update
    void updateCounterEntry(CounterEntry... counterEntries);

    @Insert
    void insertTextEntry(TextEntry... textEntries);

    @Insert
    void updateTextEntry(TextEntry... textEntries);

    @Insert
    void insertTimeEntry(TimeEntry... timeEntries);

    @Update
    void updateTimeEntry(TimeEntry... timeEntries);


    @Transaction
    default DayWithAllEntries getEverythingForOneDay(DayEntity dayEntity) {
        long dateAsLong = dayEntity.date_id.getTime();
        DayWithAllEntries d = new DayWithAllEntries();
        d.date = dayEntity.date_id;
        d.counterCategories = CounterEntry.viewAsMap(getCounterEntriesForDate(dateAsLong));
        d.binaryCategories = BinaryEntry.viewAsMap(getBinaryEntriesForDate(dateAsLong));
        d.textCategories = TextEntry.viewAsMap(getTextEntriesForDate(dateAsLong));
        d.timeCategories = TimeEntry.viewAsMap(getTimeEntriesForDate(dateAsLong));
        d.comments = getCommentsForDate(dateAsLong);
        return d;
    }

}
