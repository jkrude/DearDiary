package com.jkrude.deardiary.db;

import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import com.jkrude.deardiary.db.entities.BinaryEntry;
import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.DayWithAllEntries;
import com.jkrude.deardiary.db.entities.TextEntry;
import com.jkrude.deardiary.db.entities.TimeEntry;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Repository {

  public static final String LOGTAG = "Repository";

  private @NonNull
  DBAccess dbAccess;
  private @NonNull
  SharedPreferences prefs;
  private @NonNull
  Map<String, BinaryEntry> binaryEntries;
  private @NonNull
  Map<String, CounterEntry> counterEntries;
  private @NonNull
  Map<String, TextEntry> textEntries;
  private @NonNull
  Map<String, TimeEntry> timeEntries;


  public Repository(@NonNull DBAccess dbAccess, @NonNull SharedPreferences preferences) {
    binaryEntries = new HashMap<>();
    counterEntries = new HashMap<>();
    textEntries = new HashMap<>();
    timeEntries = new HashMap<>();
    this.dbAccess = dbAccess;
    this.prefs = preferences;
  }

  public void populate() {
    String today = prefs.getString("TODAY", null);
    if (today == null) {
      throw new IllegalStateException("Current Date not in SharedPreferences");
    }
    LocalDate date = DayEntity.DateConverter.toDate(today);
    DayWithAllEntries d = dbAccess.getEverythingForOneDay(date);
    if (d == null) {
      throw new IllegalStateException("Current Date not in DB");
    }

    for (String name : prefs.getStringSet("BINARY", new HashSet<>())) {
      binaryEntries.put(
          name,
          new BinaryEntry(
              d.binaryCategories.getOrDefault(name, false), name, date));
    }
    for (String name : prefs.getStringSet("COUNTER", new HashSet<>())) {
      counterEntries.put(
          name,
          new CounterEntry(
              Objects.requireNonNull(d.counterCategories.getOrDefault(name, 0)),
              name,
              date));
    }
    for (String name : prefs.getStringSet("TEXT", new HashSet<>())) {
      textEntries.put(
          name,
          new TextEntry(
              Objects.requireNonNull(d.textCategories.getOrDefault(name, "")),
              name,
              date));
    }
    for (String name : prefs.getStringSet("TIME", new HashSet<>())) {
      timeEntries.put(
          name,
          new TimeEntry(
              Objects.requireNonNull(d.timeCategories.getOrDefault(name, LocalTime.MIDNIGHT)),
              name,
              date));
    }
    Log.d(LOGTAG, "loading non default values:");
    Log.d(LOGTAG,
        "binary: " + binaryEntries.values().stream().filter(p -> p.value).toArray().length);
    Log.d(LOGTAG,
        "counter: " + counterEntries.values().stream().filter(p -> p.value != 1).toArray().length);
    Log.d(LOGTAG,
        "text: " + textEntries.values().stream().filter(p -> !p.value.isEmpty()).toArray().length);
    Log.d(LOGTAG,
        "time: " + timeEntries.values().stream().filter(p -> !p.value.equals(LocalTime.MIDNIGHT))
            .toArray().length);
  }

  public void save() {
    dbAccess.insertBinaryEntry(binaryEntries.values().toArray(new BinaryEntry[0]));
    dbAccess.insertCounterEntry(counterEntries.values().toArray(new CounterEntry[0]));
    dbAccess.insertTextEntry(textEntries.values().toArray(new TextEntry[0]));
    dbAccess.insertTimeEntry(timeEntries.values().toArray(new TimeEntry[0]));
  }

  @NonNull
  public Set<String> allBinaryCategories() {
    return binaryEntries.keySet();
  }

  @NonNull
  public Set<String> allCounterCategories() {
    return counterEntries.keySet();
  }

  @NonNull
  public Set<String> allTextCategories() {
    return textEntries.keySet();
  }

  @NonNull
  public Set<String> allTimeCategories() {
    return timeEntries.keySet();
  }


  @NonNull
  public BinaryEntry getBinaryEntry(@NonNull String name) {
    return Objects.requireNonNull(binaryEntries.get(name));
  }

  @NonNull
  public CounterEntry getCounterEntry(@NonNull String name) {
    return Objects.requireNonNull(counterEntries.get(name));
  }

  @NonNull
  public TextEntry getTextEntry(@NonNull String name) {
    return Objects.requireNonNull(textEntries.get(name));
  }

  @NonNull
  public TimeEntry getTimeEntry(@NonNull String name) {
    return Objects.requireNonNull(timeEntries.get(name));
  }
}
