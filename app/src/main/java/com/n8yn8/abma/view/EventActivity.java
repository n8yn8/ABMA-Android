package com.n8yn8.abma.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.n8yn8.abma.view.adapter.PaperListAdapter;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.Cache;
import com.n8yn8.abma.model.DatabaseHandler;
import com.n8yn8.abma.model.Event;
import com.n8yn8.abma.model.Note;
import com.n8yn8.abma.model.Paper;
import com.n8yn8.abma.model.Schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class EventActivity extends ActionBarActivity {

    private final String TAG = "EventActivity";
    Schedule schedule;
    Event event;
    Note note;
    Paper paper;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                if (paper != null) {
                    Paper tempPaper = schedule.getPrevPaper();
                    if (tempPaper != null) {
                        paper = tempPaper;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "First paper reached", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Event tempEvent = schedule.getPrevEvent();
                    if (tempEvent != null) {
                        event = tempEvent;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "First event reached", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paper != null) {
                    Paper tempPaper = schedule.getNextPaper();
                    if (tempPaper != null) {
                        paper = tempPaper;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "Last paper reached", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    event = schedule.getNextEvent();
                    if (event != null) {
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "Last event reached", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        saveButton = (Button) findViewById(R.id.saveNoteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0);

                int eventId = event.getIndex();
                int paperId = schedule.getPaperIndex();
                int dayId = schedule.getDayIndex();
                Log.d(TAG, "paperID = " + paperId);
                String noteContent = noteEditText.getText().toString();
                String title;
                if (paperId != -1) { //This is a paper.
                    title = paper.getTitle();
                } else { //This is an event
                    title = event.getTitle();
                }

                if (!noteContent.equals("")) {
                    if (note == null) {
                        note = new Note(dayId, eventId, paperId, noteContent, title);
                        db.addNote(note);
                    } else {
                        note.setContent(noteContent);
                        db.updateNote(note);
                    }
                    Toast.makeText(EventActivity.this, "This note has been saved", Toast.LENGTH_SHORT).show();
                } else {
                    if (note != null) {
                        db.deleteNote(note);
                        Toast.makeText(EventActivity.this, "This note has been deleted", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home) {
            schedule.setPaperIndex(-1);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        schedule.setPaperIndex(-1);
        super.onBackPressed();
    }

    public void displayEvent () {
        Date date = schedule.getCurrentDate();
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
        dayTextView.setText(dayFormatter.format(date).toUpperCase());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d");
        dateTextView.setText(dateFormatter.format(date));

        timeTextView.setText(event.getTime());
        placeTextView.setText(event.getPlace());

        List<Paper> papers = event.getPapers();

        int paperIndex = schedule.getPaperIndex();
        if (paperIndex == -1) {
            titleTextView.setText(event.getTitle());
            subtitleTextView.setText(event.getSubtitle());
            detailTextView.setText(event.getDetails());
            detailTextView.setMovementMethod(new ScrollingMovementMethod());
            detailTextView.scrollTo(0,0);
            PaperListAdapter adapter = new PaperListAdapter(this, papers);
            ListView papersListView = (ListView) findViewById(R.id.papersListView);
            papersListView.setAdapter(adapter);
            papersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    schedule.setPaperIndex(position);
                    Intent intent = new Intent(EventActivity.this, EventActivity.class);
                    startActivity(intent);
                }
            });
            note = db.getNote(event.getIndex());
            if (note != null) {
                noteEditText.setText(note.getContent());
            } else {
                noteEditText.setText("");
            }
        } else {
            paper = schedule.getCurrentPaper();
            titleTextView.setText(paper.getTitle());
            subtitleTextView.setText(paper.getAuthor());
            detailTextView.setText(paper.getSynopsis());
            detailTextView.setMovementMethod(new ScrollingMovementMethod());
            detailTextView.scrollTo(0,0);
            note = db.getNote(schedule.getDayIndex(), event.getIndex(), paper.getIndex());
            if (note != null) {
                noteEditText.setText(note.getContent());
            } else {
                noteEditText.setText("");
            }
        }
    }
}
