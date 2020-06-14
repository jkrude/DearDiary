package com.jkrude.deardiary.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.sql.Date;

@Entity(primaryKeys = {"date_id", "comment"})
@TypeConverters(DayEntity.DateConverter.class)
public class DayCommCrossRef {

    @NonNull
    public Date date_id;
    @NonNull
    public String comment;


    public DayCommCrossRef(@NonNull Date date_id, @NonNull String comment) {
        this.date_id = date_id;
        this.comment = comment;
    }
}
