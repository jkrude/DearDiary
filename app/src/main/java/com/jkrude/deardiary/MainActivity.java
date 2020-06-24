package com.jkrude.deardiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
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
    private LinearLayout linearLayout;
    private RecyclerAdapter adapter;
    private AppDatabase db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(LOGTAG, "onCreate");
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").build();
        linearLayout = findViewById(R.id.linearLayout);
        prefs = getApplicationContext()
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
        // as you specify a.json parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_end_day:
                try {
                    new NextDayTask(repository).execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                refreshUI();
                break;

            case R.id.action_settings:
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("");
                EditText searchInput = findViewById(R.id.searchInput);
                ImageButton backButton = findViewById(R.id.backBtn);
                backButton.setVisibility(View.VISIBLE);
                backButton.setOnClickListener(l -> {
                    searchInput.setVisibility(View.INVISIBLE);
                    backButton.setVisibility(View.INVISIBLE);
                    toolbar.setTitle("DearDiary");
                    adapter.filter("");
                });
                searchInput.setVisibility(View.VISIBLE);
                searchInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        adapter.filter(s.toString());
                    }
                });
                return true;
            case R.id.action_export:
                AsyncTask.execute(() -> {
                    Intent shareIntent = ShareCompat.IntentBuilder.from(MainActivity.this)
                            .setType("text/plain")
                            .setText(repository.exportAsJSON())
                            .getIntent();
                    if (shareIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(shareIntent);
                    }
                });
                break;
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
        findViewById(R.id.searchInput).setVisibility(View.INVISIBLE);
        findViewById(R.id.backBtn).setVisibility(View.INVISIBLE);
        Initiator initiator = new Initiator(
                prefs,
                dbAccess,
                repository);
        initiator.execute();
        //UI-setup
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

        AutoCompleteTextView commentInput = findViewById(R.id.commentInput);
        //TODO wrong List + needs updated
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, repository.getAllComments());
        commentInput.setAdapter(arrayAdapter);
        commentInput.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String comment = commentInput.getText().toString();
                repository.addComment(comment);
                updateCommentList(comment);
                commentInput.setText("");
            }
            return false;
        });

        for (String comment : repository.getCommentsForToday()) {
            updateCommentList(comment);
        }

        // RecyclerView config
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapter(
                this,
                repository);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void refreshUI() {
        TextView dateDisplay = findViewById(R.id.date_display);
        dateDisplay.setText(repository.getToday()
                .format(DateTimeFormatter.ofPattern(Utility.datePatternShort)));
        linearLayout.removeAllViews();
        adapter.filter("");
    }

    private void updateCommentList(String comment) {
        TextView textView = (TextView) getLayoutInflater()
                .inflate(R.layout.horizontal_textview, null);
        textView.setText(comment);
        textView.setOnLongClickListener(v -> {
            //Option to delete a.json comment
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Should the comment be deleted?")
                    .setTitle("Delete")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        linearLayout.removeView(v);
                        repository.removeComment(comment);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        //Do nothing.
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });
        linearLayout.addView(textView);
    }


    public static class NextDayTask extends AsyncTask<Void, Void, Void> {
        private Repository repository;

        public NextDayTask(Repository repository) {
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            repository.nextDay();
            return null;
        }
    }
}
