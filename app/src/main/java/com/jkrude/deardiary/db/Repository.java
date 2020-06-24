package com.jkrude.deardiary.db;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jkrude.deardiary.Utility;
import com.jkrude.deardiary.db.entities.BinaryEntry;
import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.DayWithAllEntries;
import com.jkrude.deardiary.db.entities.TextEntry;
import com.jkrude.deardiary.db.entities.TimeEntry;

import org.json.JSONArray;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Repository {

  public static final String LOGTAG = "Repository";

  private @NonNull
  LocalDate today;

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
  private @NonNull
  Set<String> commentsForToday;
  private @NonNull
  List<String> allComments;


  public Repository(@NonNull DBAccess dbAccess, @NonNull SharedPreferences preferences) {
    binaryEntries = new HashMap<>();
    counterEntries = new HashMap<>();
    textEntries = new HashMap<>();
    timeEntries = new HashMap<>();
    commentsForToday = new HashSet<>();
    allComments = new ArrayList<>();
    this.dbAccess = dbAccess;
    this.prefs = preferences;
    this.today = LocalDate.MAX;
  }

  public void populate() {
    String todayAsSt = prefs.getString("TODAY", null);
    if (todayAsSt == null) {
      throw new IllegalStateException("Current Date not in SharedPreferences");
    }
    this.today = Utility.DateConverter.toDate(todayAsSt);
    DayWithAllEntries d = dbAccess.getEverythingForOneDay(today);
    if (d == null) {
      throw new IllegalStateException("Current Date not in DB");
    }
    allComments = dbAccess.getAllComments();
    //TODO refactor DayWithAllEntries
    commentsForToday = new HashSet<>(d.comments);

    for (String name : prefs.getStringSet("BINARY", new HashSet<>())) {
      binaryEntries.put(
              name,
              new BinaryEntry(
                      d.binaryCategories.getOrDefault(name, false), name, today));
    }
    for (String name : prefs.getStringSet("COUNTER", new HashSet<>())) {
      counterEntries.put(
              name,
              new CounterEntry(
                      Objects.requireNonNull(d.counterCategories.getOrDefault(name, 0)),
                      name,
                      today));
    }
    for (String name : prefs.getStringSet("TEXT", new HashSet<>())) {
      textEntries.put(
              name,
              new TextEntry(
                      Objects.requireNonNull(d.textCategories.getOrDefault(name, "")),
                      name,
                      today));
    }
    for (String name : prefs.getStringSet("TIME", new HashSet<>())) {
      timeEntries.put(
              name,
              new TimeEntry(
                      Objects.requireNonNull(d.timeCategories.getOrDefault(name, LocalTime.MIDNIGHT)),
                      name,
                      today));
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
    commentsForToday.forEach(item -> dbAccess.insertCommentForDay(today, item));
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

  @NonNull
  public Set<String> getCommentsForToday() {
    return commentsForToday;
  }

  public void addComment(@NonNull String comment) {
    commentsForToday.add(comment);
    AsyncTask.execute(() ->
            dbAccess.insertCommentForDay(today, comment));
  }

    public void removeComment(@NonNull String comment) {
        commentsForToday.remove(comment);
        AsyncTask.execute(() ->
                dbAccess.deleteCommentForDay(today, comment));
    }

    @NonNull
    public List<String> getAllComments() {
        return allComments;
    }

    public String exportAsJSON() {
        JSONArray jsonArray = new JSONArray();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<DayEntity> dayEntities = dbAccess.getDayEntities();
        for (DayEntity day : dayEntities) {
            jsonArray.put(day.date_id.toString() + ": " + gson.toJson(dbAccess.getEverythingForOneDay(day.date_id)));
        }
        return jsonArray.toString();
    }
}
