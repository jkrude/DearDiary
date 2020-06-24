package com.jkrude.deardiary.db;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.jkrude.deardiary.MainActivity;
import com.jkrude.deardiary.Utility;
import com.jkrude.deardiary.db.entities.BinaryEntry;
import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.TimeEntry;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class Initiator extends AsyncTask<Void, Void, Void> {

    public static final String LOGTAG = "Initiator";
    SharedPreferences preferences;
    DBAccess dao;
    Repository repository;


    public Initiator(SharedPreferences preferences, DBAccess dao, Repository repository) {
        this.preferences = preferences;
        this.dao = dao;
        this.repository = repository;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        LocalDate today = LocalDate.now();
        String todaySt = preferences.getString(MainActivity.TODAY, null);
        if (todaySt != null) {
            today = Utility.DateConverter.toDate(todaySt);
        }
        if (dao.getDayEntitiesForDay(today).isEmpty()) {
            Log.d(LOGTAG, "Creating new DayEntity");
            DayEntity dayEntity = new DayEntity(today);
            dao.insertDay(dayEntity);
            BinaryEntry catEntry = new BinaryEntry(false, "ToDo done", dayEntity.date_id);
            dao.insertBinaryEntry(catEntry);

            CounterEntry counterEntry = new CounterEntry(1, "Article", dayEntity.date_id);
            dao.insertCounterEntry(counterEntry);

            TimeEntry timeEntry0 = new TimeEntry(LocalTime.of(10, 27), "Wakeup",
                    dayEntity.date_id);
            TimeEntry timeEntry1 = new TimeEntry(LocalTime.of(22, 10), "Bedtime",
                    dayEntity.date_id);
            dao.insertTimeEntry(timeEntry0, timeEntry1);
            dao.insertCommentForDay(today, "good");
            dao.insertCommentForDay(today, "Friends=great");
        } else {
            Log.d(LOGTAG, "DayEntity already available");
        }
        preferences.edit().putString("TODAY", today.toString()).apply();
        preferences.edit().putStringSet("BINARY", new HashSet<>(Arrays.asList("Reading", "Sport")))
                .apply();
        preferences.edit().putStringSet("COUNTER",
                new HashSet<>(Arrays.asList("Article", "ToDo created", "ToDo done"))).apply();
        preferences.edit()
                .putStringSet("TEXT", new HashSet<>(Collections.singletonList("Film/Series"))).apply();
        preferences.edit()
                .putStringSet("TIME", new HashSet<>(Arrays.asList("Bedtime", "Wakeup")))
                .apply();

        repository.populate();

        return null;
    }
}
