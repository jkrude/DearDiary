package com.jkrude.deardiary.db.entities;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import java.time.LocalDate;
import java.time.LocalTime;

@androidx.room.Entity
@TypeConverters({DayEntity.DateConverter.class, DayEntity.LocalTimeConverter.class})
public class DayEntity {

  @PrimaryKey
  @NonNull
  public LocalDate date_id;

  public LocalTime sleep;

  public DayEntity(@NonNull LocalDate date_id) {
    this.date_id = date_id;
  }

  public static class DateConverter {

    @TypeConverter
    public static LocalDate toDate(String dateLong) {
      return dateLong == null ? null : LocalDate.parse(dateLong);
    }

    @TypeConverter
    public static String fromDate(LocalDate date) {
      return date == null ? null : date.toString();
    }
  }

    public static class LocalTimeConverter {

        @TypeConverter
        public static LocalTime toLocalTime(String timeString) {
            return timeString == null ? null : LocalTime.parse(timeString);
        }

        @TypeConverter
        public static String toLocalTime(LocalTime time) {
            return time == null ? null : time.toString();
        }
    }

}
