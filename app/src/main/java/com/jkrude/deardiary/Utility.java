package com.jkrude.deardiary;

import androidx.room.TypeConverter;
import java.time.LocalDate;
import java.time.LocalTime;

public class Utility {

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
