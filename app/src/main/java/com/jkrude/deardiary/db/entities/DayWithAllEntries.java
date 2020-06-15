package com.jkrude.deardiary.db.entities;


import java.sql.Date;
import java.util.List;
import java.util.Map;

public class DayWithAllEntries {

    public Date date;
    public List<String> comments;

    public Map<String, Integer> counterCategories;
    public Map<String, Boolean> binaryCategories;

}
