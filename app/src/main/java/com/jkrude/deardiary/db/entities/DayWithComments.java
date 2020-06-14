package com.jkrude.deardiary.db.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class DayWithComments {
    @Embedded
    public DayEntity day;
    @Relation(
            parentColumn = "date_id",
            entityColumn = "comment",
            associateBy = @Junction(DayCommCrossRef.class)
    )
    public List<DayComment> comments;
}
