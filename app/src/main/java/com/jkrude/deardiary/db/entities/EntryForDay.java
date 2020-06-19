package com.jkrude.deardiary.db.entities;

import androidx.annotation.NonNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class EntryForDay<T> {

    @NonNull
    public T value;

    @NonNull
    public String catName;

    @NonNull
    public LocalDate dayID;

    public EntryForDay(@NonNull T value, @NonNull String catName, @NonNull LocalDate dayID) {
        this.value = value;
        this.catName = catName;
        this.dayID = dayID;
    }

    protected static <T, U extends EntryForDay<T>> Map<String, T> viewAsMapT(
        @NonNull final List<U> list) {
        HashMap<String, T> hashMap = new HashMap<>();
        list.forEach(
            entry -> hashMap.put(entry.catName, entry.value)
        );
        return hashMap;
    }

    @NonNull
    @Override
    public String toString() {
        return "EntryForDay{" +
            "value=" + value +
            ", catName='" + catName + '\'' +
            '}';
    }
}
