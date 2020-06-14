package com.jkrude.deardiary.db.entities;

import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.LocalTime;
import java.sql.Date;

@androidx.room.Entity
@TypeConverters({DayEntity.DateConverter.class, DayEntity.LocalTimeConverter.class})
public class DayEntity {

    @PrimaryKey
    public Date date_id;

    public LocalTime sleep;

    /*@Relation(
            parentColumn = "id",
            entityColumn = "commentDate")
    public List<DayComment> dayComments;*/

    public static class DateConverter {

        @TypeConverter
        public static Date toDate(Long dateLong) {
            return dateLong == null ? null : new Date(dateLong);
        }

        @TypeConverter
        public static Long fromDate(Date date) {
            return date == null ? null : date.getTime();
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
