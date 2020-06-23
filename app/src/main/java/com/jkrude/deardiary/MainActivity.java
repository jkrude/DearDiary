package com.jkrude.deardiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.jkrude.deardiary.db.AppDatabase;
import com.jkrude.deardiary.db.DBAccess;
import com.jkrude.deardiary.db.Initiator;
import com.jkrude.deardiary.db.Repository;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

  public static final String SHARED_PREFS_TAG = "com.jkrude.dearDiary";
  public static final String LOGTAG = "Main";
  public static final String TODAY = "TODAY";

  private Repository repository;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d(LOGTAG, "onCreate");
    AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").build();
    SharedPreferences prefs = getApplicationContext()
        .getSharedPreferences(SHARED_PREFS_TAG, Context.MODE_PRIVATE);
    repository = new Repository(
        db.dbAccess(),
        prefs);
    //TODO
    setupUI(db.dbAccess(), prefs);
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
        repository.save());
  }

  private void setupUI(DBAccess dbAccess, SharedPreferences prefs) {
    Initiator initiator = new Initiator(
        prefs,
        dbAccess,
        repository);
    initiator.execute();
    //UI-setup
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    TextView dateDisplay = findViewById(R.id.date_display);
    try {
      // needs loading screen
      initiator.get();
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }

    String date = prefs.getString(TODAY, null);
    if (date == null) {
      throw new IllegalStateException("Date was not set");
    } else {
      date = Utility.DateConverter.toDate(date)
          .format(DateTimeFormatter.ofPattern(Utility.datePatternShort));
    }
    dateDisplay.setText(date);

    LinearLayout linearLayout = findViewById(R.id.linearLayout);
    for (String comment : repository.getComments()) {
      TextView textView = (TextView) getLayoutInflater()
          .inflate(R.layout.horizontal_textview, null);
      textView.setText(comment);
      linearLayout.addView(textView);
    }

    // RecyclerView config
    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    RecyclerAdapter adapter = new RecyclerAdapter(
            this,
            repository);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
  }

}
