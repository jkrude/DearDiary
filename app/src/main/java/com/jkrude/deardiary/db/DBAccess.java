package com.jkrude.deardiary.db;

import androidx.annotation.NonNull;
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
    @Query("SELECT * FROM DayEntity")
    List<DayEntity> getDayEntities();

    @Query("SELECT comment FROM daycomment")
    List<String> getAllComments();

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

    default List<String> getCommentsForDate(LocalDate date) {
        return getCommentsForDate(Utility.DateConverter.fromDate(date));
    }

    @Query("SELECT COUNT(comment) FROM daycommcrossref WHERE comment LIKE :comment")
    int getCountCommDayJoin(String comment);

    /*
     * INSERT / DELETE / UPDATE
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertComment(DayComment... comments);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCommentDayJoin(DayCommCrossRef... dayCommCrossRefs);

    @Transaction
    default void insertCommentForDay(@NonNull LocalDate date, @NonNull String comment) {
        //String dateAsString = Utility.DateConverter.fromDate(date);
        insertComment(new DayComment(comment)); //try to insert -> else fine
        insertCommentDayJoin(new DayCommCrossRef(date, comment));
    }

    @Transaction
    default void deleteCommentForDay(@NonNull LocalDate date, @NonNull String comment) {
        deleteRef(new DayCommCrossRef(date, comment));
        if (getCountCommDayJoin(comment) == 0) {
            deleteComment(new DayComment(comment));
        }
    }

    @Delete
    void deleteComment(DayComment... comment);

    @Delete
    void deleteRef(DayCommCrossRef... refs);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
