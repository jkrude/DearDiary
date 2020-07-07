package com.jkrude.deardiary.db.entities;


import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayWithAllEntries {

  @NonNull
  public transient LocalDate date;
  @NonNull
  public List<String> comments;
  @NonNull
  public Map<String, Integer> counterCategories;
  @NonNull
  public Map<String, Boolean> binaryCategories;
  @NonNull
  public Map<String, String> textCategories;
  @NonNull
  public Map<String, LocalTime> timeCategories;

  public DayWithAllEntries(@NonNull LocalDate date) {
    this.date = date;
    comments = new ArrayList<>();
    counterCategories = new HashMap<>();
    binaryCategories = new HashMap<>();
    textCategories = new HashMap<>();
    timeCategories = new HashMap<>();
  }

  @NonNull
  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append(date.toString())
        .append("\n")
        .append("{\n");
    for (Map.Entry<String, Boolean> i : binaryCategories.entrySet()) {
      out.append(i.getKey()).append(": ").append(i.getValue()).append(",\n");
        }
        return out.toString();
    }
}
