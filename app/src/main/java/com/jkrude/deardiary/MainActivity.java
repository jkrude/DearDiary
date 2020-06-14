package com.jkrude.deardiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jkrude.deardiary.db.AppDatabase;
import com.jkrude.deardiary.db.enteties.DayEntity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import java.sql.Date;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
            DayEntity entity = new DayEntity();

            entity.id = new Date(1000000000);
            entity.sleep = LocalTime.of(10, 5);
            db.dbAccess().insertAll(entity);
            List<DayEntity> entities = db.dbAccess().getDayEntities();
        });
    }
}
