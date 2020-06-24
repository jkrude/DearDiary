package com.jkrude.deardiary.db;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayComment;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.TextEntry;
import com.jkrude.deardiary.db.entities.TimeEntry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DBAccessTest {
    private DBAccess dbAccess;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        dbAccess = db.dbAccess();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertComment() throws IOException {
        String comment1 = "Good";
        String comment2 = "ok";
        dbAccess.insertComment(new DayComment(comment1), new DayComment(comment2));
        List<String> i = dbAccess.getAllComments();
        assertTrue(i.remove(comment1));
        assertTrue(i.remove(comment2));
        assertTrue(i.isEmpty());
    }

    @Test
    public void testInsertCommentForDay() throws IOException {
        String comment1 = "Good";
        String comment2 = "ok";
        LocalDate dateMin = LocalDate.MIN;
        LocalDate dateMax = LocalDate.MAX;
        dbAccess.insertComment(new DayComment(comment2));

        dbAccess.insertCommentForDay(dateMin, comment1);
        dbAccess.insertCommentForDay(dateMin, comment2);
        List<String> items = dbAccess.getCommentsForDate(dateMin);
        assertTrue(items.remove(comment1));
        assertTrue(items.remove(comment2));
        assertTrue(items.isEmpty());

        dbAccess.insertCommentForDay(dateMax, comment1);
        items = dbAccess.getCommentsForDate(dateMax);
        assertTrue(items.remove(comment1));
        assertTrue(items.isEmpty());
    }

    @Test
    public void testDeleteCommentForDay() {
        String comment1 = "Good";
        String comment2 = "ok";
        LocalDate dateMin = LocalDate.MIN;
        LocalDate dateMax = LocalDate.MAX;
        dbAccess.insertCommentForDay(dateMin, comment1);
        dbAccess.insertCommentForDay(dateMax, comment1);
        dbAccess.insertCommentForDay(dateMax, comment2);

        // dateMax  | dateMin
        // comment1 | comment1
        // comment2

        dbAccess.deleteCommentForDay(dateMin, comment1);
        dbAccess.deleteCommentForDay(dateMax, comment2);
        // dateMax  | dateMin
        // comment1 |
        assertTrue(dbAccess.getCommentsForDate(dateMin).isEmpty());
        assertTrue(dbAccess.getCommentsForDate(dateMax).contains(comment1));
        assertFalse(dbAccess.getCommentsForDate(dateMax).contains(comment2));
        List<String> allComments = dbAccess.getAllComments();
        assertTrue(allComments.remove(comment1)); // comment1 still exists
        assertFalse(allComments.remove(comment2)); // comment2 does not exist anymore

    }

    @Test
    public void testJSON() {
        DayComment[] comments = {new DayComment("Good"), new DayComment("ok"), new DayComment("well")};
        LocalDate[] dates = {
                LocalDate.of(2020, 6, 1),
                LocalDate.of(2020, 6, 2),
                LocalDate.of(2020, 6, 3),
                LocalDate.of(2020, 6, 5)
        };
        DayEntity[] days = {
                new DayEntity(dates[0]),
                new DayEntity(dates[1]),
                new DayEntity(dates[2]),
                new DayEntity(dates[3])
        };
        dbAccess.insertDay(days);
        TimeEntry[] timeEntry = {new TimeEntry(LocalTime.MIDNIGHT, "sleep", dates[0])};
        TextEntry[] textEntries = {new TextEntry("text", "Film", dates[0]),
                new TextEntry("SW", "Film", dates[1])};
        CounterEntry[] counterEntries = {
                new CounterEntry(1, "Gelesen", dates[0]),
                new CounterEntry(3, "Gelesen", dates[1]),
                new CounterEntry(5, "Gelesen", dates[2]),
                new CounterEntry(2, "Gelesen", dates[3])};
        dbAccess.insertCommentForDay(dates[0], comments[0].comment);
        dbAccess.insertCommentForDay(dates[0], comments[1].comment);
        dbAccess.insertCommentForDay(dates[0], comments[2].comment);
        dbAccess.insertCommentForDay(dates[1], comments[0].comment);
        dbAccess.insertCommentForDay(dates[2], comments[0].comment);
        dbAccess.insertTimeEntry(timeEntry);
        dbAccess.insertTextEntry(textEntries);
        dbAccess.insertCounterEntry(counterEntries);
        //System.out.println(dbAccess.exportAsJSON());
    }

}