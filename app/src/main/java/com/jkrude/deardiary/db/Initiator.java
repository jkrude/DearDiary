package com.jkrude.deardiary.db;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.jkrude.deardiary.db.entities.BinaryEntry;
import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayCommCrossRef;
import com.jkrude.deardiary.db.entities.DayComment;
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
        if (dao.getDayEntitiesForDay(today).isEmpty()) {
            Log.d(LOGTAG, "Creating new DayEntity");
            DayEntity dayEntity = new DayEntity(today);
            dao.insertDay(dayEntity);
            BinaryEntry catEntry = new BinaryEntry(false, "ToDoErledigt", dayEntity.date_id);
            dao.insertBinaryEntry(catEntry);

            CounterEntry counterEntry = new CounterEntry(1, "Artikel", dayEntity.date_id);
            dao.insertCounterEntry(counterEntry);

            TimeEntry timeEntry0 = new TimeEntry(LocalTime.of(10, 27), "Aufgewacht",
                dayEntity.date_id);
            TimeEntry timeEntry1 = new TimeEntry(LocalTime.of(22, 10), "Eingeschlafen",
                dayEntity.date_id);
            dao.insertTimeEntry(timeEntry0, timeEntry1);
            DayComment dayComment = new DayComment("Gut");
            DayComment dayComment1 = new DayComment("Freunde=sehr schön");
            DayCommCrossRef commCrossRef = new DayCommCrossRef(today, dayComment.comment);
            DayCommCrossRef commCrossRef1 = new DayCommCrossRef(today, dayComment1.comment);
            dao.insertComment(dayComment);
            dao.insertComment(dayComment1);
            dao.insertAllRefs(commCrossRef, commCrossRef1);
        } else {
            Log.d(LOGTAG, "DayEntity already available");
        }
        preferences.edit().putString("TODAY", today.toString()).apply();
        preferences.edit().putStringSet("BINARY", new HashSet<>(Arrays.asList("Gelesen", "Sport")))
            .apply();
        preferences.edit().putStringSet("COUNTER",
            new HashSet<>(Arrays.asList("Artikel", "ToDo Erstellt", "ToDo Erledigt"))).apply();
        preferences.edit()
            .putStringSet("TEXT", new HashSet<>(Collections.singletonList("Film/Serie"))).apply();
        preferences.edit()
            .putStringSet("TIME", new HashSet<>(Arrays.asList("Eingeschlafen", "Aufgewacht")))
            .apply();

        repository.populate();

        return null;
    }
}
