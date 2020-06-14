package com.jkrude.deardiary.db.enteties;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.jkrude.deardiary.db.enteties.DayEntity;

@Entity
@TypeConverters(DayEntity.DateConverter.class)
public class DayComment {

    @PrimaryKey
    @NonNull
    public String comment;

    public DayComment() {
        comment = "";
    }
}
