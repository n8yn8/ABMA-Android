package com.n8yn8.abma;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class EventActivity extends ActionBarActivity {

    private final String TAG = "EventActivity";
    Schedule schedule;
    Event event;
    Note note;

    TextView dayTextView;
    TextView dateTextView;
    TextView titleTextView;
    TextView subtitleTextView;
    TextView detailTextView;
    TextView timeTextView;
    TextView placeTextView;
    EditText noteEditText;
    Button saveButton;

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        db = new DatabaseHandler(getApplicationContext());

        dayTextView = (TextView) findViewById(R.id.dayTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        subtitleTextView = (TextView) findViewById(R.id.subtitleTextView);
        detailTextView = (TextView) findViewById(R.id.detailTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        placeTextView = (TextView) findViewById(R.id.placeTextView);
        noteEditText = (EditText) findViewById(R.id.noteEditText);

        schedule = Cache.getInstance().getSchedule();
        event = schedule.getCurrentEvent();
        displayEvent();

        ImageButton backButton = (ImageButton) findViewById(R.id.backEventButton);
        ImageButton nextButton = (ImageButton) findViewById(R.id.nextEventButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = schedule.getPrevEvent();
                if (event != null) {
                    displayEvent();
                } else {
                    Toast.makeText(getApplicationContext(), "First event reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = schedule.getNextEvent();
                if (event != null) {
                    displayEvent();
                } else {
                    Toast.makeText(getApplicationContext(), "Last event reached", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton = (Button) findViewById(R.id.saveNoteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int eventId = event.getIndex();
                String noteContent = noteEditText.getText().toString();
                if (note == null) {
                    note = new Note(eventId, noteContent);
                    db.addNote(note);
                } else {
                    note.setContent(noteContent);
                    db.updateNote(note);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
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

    public void displayEvent () {
        Date date = schedule.getCurrentDate();
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
        dayTextView.setText(dayFormatter.format(date).toUpperCase());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d");
        dateTextView.setText(dateFormatter.format(date));
        titleTextView.setText(event.getTitle());
        subtitleTextView.setText(event.getSubtitle());
        timeTextView.setText(event.getTime());
        placeTextView.setText(event.getPlace());
        detailTextView.setText(event.getDetails());
        detailTextView.setMovementMethod(new ScrollingMovementMethod());

        note = db.getNote(event.getIndex());
        if (note != null) {
            noteEditText.setText(note.getContent());
        } else {
            noteEditText.setText("");
        }
    }
}
