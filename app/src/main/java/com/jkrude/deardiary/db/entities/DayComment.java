package com.jkrude.deardiary.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class DayComment {

    @PrimaryKey
    @NonNull
    public String comment;

    public DayComment(@NonNull String comment) {
        this.comment = comment;
    }
}
