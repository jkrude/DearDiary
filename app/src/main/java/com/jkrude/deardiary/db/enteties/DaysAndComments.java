package com.jkrude.deardiary.db.enteties;

import androidx.room.Embedded;
import androidx.room.Relation;

public class DaysAndComments {
    @Embedded
    public DayEntity day;
    @Relation(
            parentColumn = "id",
            entityColumn = "date_id"
    )
    public DayComment comment;
}
