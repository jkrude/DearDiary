package com.jkrude.deardiary.db.entities;

import androidx.annotation.NonNull;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class EntryForDay<T> {

    @NonNull
    public T value;

    @NonNull
    public String catName;

    @NonNull
    public Date dayID;

    public EntryForDay(@NonNull T value, @NonNull String catName, @NonNull Date dayID) {
        this.value = value;
        this.catName = catName;
        this.dayID = dayID;
    }

    protected static <T, U extends EntryForDay<T>> Map<String, T> viewAsMapT(@NonNull final List<U> list) {
        HashMap<String, T> hashMap = new HashMap<>();
        list.forEach(
                entry -> hashMap.put(entry.catName, entry.value)
        );
        return hashMap;
    }
}
