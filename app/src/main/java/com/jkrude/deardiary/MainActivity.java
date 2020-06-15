package com.jkrude.deardiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jkrude.deardiary.db.AppDatabase;
import com.jkrude.deardiary.db.entities.BinaryEntry;
import com.jkrude.deardiary.db.entities.CounterEntry;
import com.jkrude.deardiary.db.entities.DayCommCrossRef;
import com.jkrude.deardiary.db.entities.DayComment;
import com.jkrude.deardiary.db.entities.DayEntity;
import com.jkrude.deardiary.db.entities.DayWithAllEntries;

import java.sql.Date;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    public static final String sharedPrefsTag = "com.jkrude.dearDiary";
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testDB();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(
                view -> Snackbar.make(
                        view,
                        "Replace with your own action",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());

        // RecyclerView config
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        SharedPreferences prefs = this.getSharedPreferences(
                sharedPrefsTag, Context.MODE_PRIVATE);
        prefs.getStringSet("RecyclerViewEntries", new HashSet<>());
        // TODO
        HashMap<Integer, Pair<RecyclerAdapter.ViewType, String>> positions = new HashMap<>();
        positions.put(0, new Pair<>(RecyclerAdapter.ViewType.COUNTER, "Artikel"));
        positions.put(1, new Pair<>(RecyclerAdapter.ViewType.BINARY, "Lesen"));
        positions.put(2, new Pair<>(RecyclerAdapter.ViewType.TEXT_INPUT, "Film/Serie"));
        positions.put(3, new Pair<>(RecyclerAdapter.ViewType.BINARY, "Sport"));
        positions.put(4, new Pair<>(RecyclerAdapter.ViewType.COUNTER, "ToDo erstellt"));
        positions.put(5, new Pair<>(RecyclerAdapter.ViewType.COUNTER, "ToDo erledigt"));
        positions.put(6, new Pair<>(RecyclerAdapter.ViewType.TIME_INPUT, "Schlaf"));

        recyclerAdapter = new RecyclerAdapter(this, positions);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void testDB() {
        AsyncTask.execute(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").build();
            db.clearAllTables();

            DayEntity day0 = new DayEntity();
            day0.date_id = Date.valueOf("2020-01-01");
            day0.sleep = LocalTime.of(10, 5);
            DayEntity day1 = new DayEntity();
            day1.date_id = Date.valueOf("2020-02-02");
            db.dbAccess().insertDay(day0, day1);

            DayComment comment = new DayComment("Gut");
            DayComment comment1 = new DayComment("Töpi");
            DayComment comment2 = new DayComment("mäßig");
            db.dbAccess().insertComment(comment, comment1, comment2);

            DayCommCrossRef ref = new DayCommCrossRef(day0.date_id, comment.comment);
            DayCommCrossRef ref1 = new DayCommCrossRef(day0.date_id, comment1.comment);
            DayCommCrossRef ref2 = new DayCommCrossRef(day1.date_id, comment2.comment);
            db.dbAccess().insertAllRefs(ref, ref1, ref2);

            BinaryEntry catEntry = new BinaryEntry(false, "ToDoErledigt", day0.date_id);
            db.dbAccess().insertBinaryEntry(catEntry);

            CounterEntry counterEntry = new CounterEntry(1, "Artikel", day1.date_id);
            db.dbAccess().insertCounterEntry(counterEntry);

            /*List<DayEntity> entities = db.dbAccess().getDayEntities();
            List<DayWithComments> res = db.dbAccess().getDaysWithComments();

            List<CounterEntry> c = db.dbAccess().getCounterEntriesForDate(DayEntity.DateConverter.fromDate(day1.date_id));
            Map<String, Integer> cMap = CounterEntry.viewAsMap(c);
            List<BinaryEntry> b = db.dbAccess().getBinaryEntriesForDate(DayEntity.DateConverter.fromDate(day0.date_id));
            Map<String, Boolean> bMap = BinaryEntry.viewAsMap(b);*/
            DayWithAllEntries d = db.dbAccess().getEverythingForOneDay(day0);

        });
    }
}
