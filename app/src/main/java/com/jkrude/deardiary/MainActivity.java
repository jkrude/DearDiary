package com.jkrude.deardiary;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.jkrude.deardiary.db.Initiator;
import com.jkrude.deardiary.db.Repository;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

  public static final String sharedPrefsTag = "com.jkrude.dearDiary";
  public static final String LOGTAG = "Main";
  AppDatabase db;
  Repository repository;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d(LOGTAG, "onCreate");
    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").build();
    repository = new Repository(
        db.dbAccess(),
        getApplicationContext().getSharedPreferences(sharedPrefsTag, Context.MODE_PRIVATE));
    //TODO
    //testDB();
    Initiator initiator = new Initiator(
        getApplicationContext().getSharedPreferences(sharedPrefsTag, Context.MODE_PRIVATE),
        db.dbAccess(),
        repository);
    initiator.execute();
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
    try {
      initiator.get();
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    RecyclerAdapter adapter = new RecyclerAdapter(
        getApplicationContext(),
        repository);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
    AsyncTask.execute(() ->
    {
      repository.save();
    });
  }

}
