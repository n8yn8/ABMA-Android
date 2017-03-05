package com.n8yn8.abma.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
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

import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.backendless.BPaper;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.old.DatabaseHandler;
import com.n8yn8.abma.view.adapter.PaperListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class EventActivity extends ActionBarActivity {

    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_PAPER_ID = "paper_id";

    private final String TAG = "EventActivity";
    BEvent event;
    BNote note;
    BPaper paper;

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

    public static void start(Context context, @Nullable String eventId, @Nullable String paperId) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        intent.putExtra(EXTRA_PAPER_ID, paperId);
        context.startActivity(intent);
    }

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

        event = db.getEventById(getIntent().getStringExtra(EXTRA_EVENT_ID));
        paper = db.getPaperById(getIntent().getStringExtra(EXTRA_PAPER_ID));
        displayEvent();

        ImageButton backButton = (ImageButton) findViewById(R.id.backEventButton);
        ImageButton nextButton = (ImageButton) findViewById(R.id.nextEventButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: get prev.
//                if (paper != null) {
//                    Paper tempPaper = schedule.getPrevPaper();
//                    if (tempPaper != null) {
//                        paper = tempPaper;
//                        displayEvent();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "First paper reached", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
                    BEvent tempEvent = db.getEventBefore(event.getStartDate().getTime());
                    if (tempEvent != null) {
                        event = tempEvent;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "First event reached", Toast.LENGTH_SHORT).show();
                    }
//                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: get next
//                if (paper != null) {
//                    Paper tempPaper = schedule.getNextPaper();
//                    if (tempPaper != null) {
//                        paper = tempPaper;
//                        displayEvent();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Last paper reached", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
                    BEvent tempEvent = db.getEventAfter(event.getStartDate().getTime());
                    if (tempEvent != null) {
                        event = tempEvent;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "Last event reached", Toast.LENGTH_SHORT).show();
                    }
//                }

            }
        });

        saveButton = (Button) findViewById(R.id.saveNoteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0);

                //TODO: save note
                String eventId = null;
                if (event != null) {
                    eventId = event.getObjectId();
                }
                String paperId = null;
                if (paper != null) {
                    paperId = paper.getObjectId();
                }
                String noteContent = noteEditText.getText().toString();

                if (!noteContent.equals("")) {
                    if (note == null) {
                        note = new BNote();
                    }
                    note.setContent(noteContent);
                    note.setEventId(eventId);
                    note.setPaperId(paperId);
                    DbManager.getInstance().addNote(note, new DbManager.OnNoteSavedCallback() {
                        @Override
                        public void noteSaved(BNote note, String error) {
                            db.addNote(note);
                            Toast.makeText(EventActivity.this, "This note has been saved", Toast.LENGTH_SHORT).show();
                        }
                    });
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void displayEvent () {
        Date date = event.getStartDate();
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
        dayTextView.setText(dayFormatter.format(date).toUpperCase());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d");
        dateTextView.setText(dateFormatter.format(date));


        timeTextView.setText(Utils.getTimes(event));
        placeTextView.setText(event.getLocation());

        List<BPaper> papers = event.getPapers();

        if (paper == null) {
            titleTextView.setText(event.getTitle());
            subtitleTextView.setText(event.getSubtitle());
            detailTextView.setText(event.getDetails());
            detailTextView.setMovementMethod(new ScrollingMovementMethod());
            detailTextView.scrollTo(0,0);
            final PaperListAdapter adapter = new PaperListAdapter(this, papers);
            ListView papersListView = (ListView) findViewById(R.id.papersListView);
            papersListView.setAdapter(adapter);
            papersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BPaper paper = adapter.getItem(position);
                    EventActivity.start(EventActivity.this, event.getObjectId(), paper.getObjectId());
                }
            });
            note = db.getNoteByEventId(event.getObjectId());
        } else {
            titleTextView.setText(paper.getTitle());
            subtitleTextView.setText(paper.getAuthor());
            detailTextView.setText(paper.getSynopsis());
            detailTextView.setMovementMethod(new ScrollingMovementMethod());
            detailTextView.scrollTo(0,0);
            note = db.getNoteByEventId(paper.getObjectId());
        }
        if (note != null) {
            noteEditText.setText(note.getContent());
        } else {
            noteEditText.setText("");
        }
    }
}
