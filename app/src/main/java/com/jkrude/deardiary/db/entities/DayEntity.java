package com.jkrude.deardiary.db.entities;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.jkrude.deardiary.Utility;
import java.time.LocalDate;
import java.time.LocalTime;

@androidx.room.Entity
@TypeConverters({Utility.DateConverter.class, Utility.LocalTimeConverter.class})
public class DayEntity {

  @PrimaryKey
  @NonNull
  public LocalDate date_id;

  public LocalTime sleep;

  public DayEntity(@NonNull LocalDate date_id) {
    this.date_id = date_id;
  }
}
