package com.n8yn8.abma.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import java.util.TimeZone;


public class EventActivity extends AppCompatActivity {

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DatabaseHandler(getApplicationContext());

        dayTextView = findViewById(R.id.dayTextView);
        dateTextView = findViewById(R.id.dateTextView);
        titleTextView = findViewById(R.id.titleTextView);
        subtitleTextView = findViewById(R.id.subtitleTextView);
        detailTextView = findViewById(R.id.detailTextView);
        timeTextView = findViewById(R.id.timeTextView);
        placeTextView = findViewById(R.id.placeTextView);
        noteEditText = findViewById(R.id.noteEditText);

        event = db.getEventById(getIntent().getStringExtra(EXTRA_EVENT_ID));
        paper = db.getPaperById(getIntent().getStringExtra(EXTRA_PAPER_ID));
        displayEvent();

        ImageButton backButton = findViewById(R.id.backEventButton);
        ImageButton nextButton = findViewById(R.id.nextEventButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paper != null) {
                    BPaper prevPaper = getPrevPaper();
                    if (prevPaper != null) {
                        paper = prevPaper;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "First paper reached", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    BEvent tempEvent = db.getEventBefore(event.getStartDate().getTime());
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
                    BPaper nextPaper = getNextPaper();
                    if (nextPaper != null) {
                        paper = nextPaper;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "Last paper reached", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    BEvent tempEvent = db.getEventAfter(event.getStartDate().getTime());
                    if (tempEvent != null) {
                        event = tempEvent;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "Last event reached", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        saveButton = findViewById(R.id.saveNoteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0);
                }

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
                            if (error != null) {
                                db.addNote(EventActivity.this.note);
                            } else {
                                db.addNote(note);
                            }
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
        dayFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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
            ListView papersListView = findViewById(R.id.papersListView);
            papersListView.setAdapter(adapter);
            papersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BPaper paper = adapter.getItem(position);
                    if (paper != null) {
                        EventActivity.start(EventActivity.this, event.getObjectId(), paper.getObjectId());
                    }
                }
            });
            note = db.getNoteBy(event.getObjectId(), null);
        } else {
            titleTextView.setText(paper.getTitle());
            subtitleTextView.setText(paper.getAuthor());
            detailTextView.setText(paper.getSynopsis());
            detailTextView.setMovementMethod(new ScrollingMovementMethod());
            detailTextView.scrollTo(0,0);
            note = db.getNoteBy(event.getObjectId(), paper.getObjectId());
        }
        if (note != null) {
            noteEditText.setText(note.getContent());
        } else {
            noteEditText.setText("");
        }
    }

    @Nullable
    private BPaper getPrevPaper() {
        for (int i = 0; i < event.getPapers().size(); i++) {
            BPaper checkPaper = event.getPapers().get(i);
            if (checkPaper.getObjectId().equals(paper.getObjectId())) {
                if (i > 0) {
                    return paper = event.getPapers().get(i - 1);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Nullable
    private BPaper getNextPaper() {
        for (int i = 0; i < event.getPapers().size(); i++) {
            BPaper checkPaper = event.getPapers().get(i);
            if (checkPaper.getObjectId().equals(paper.getObjectId())) {
                if (i < event.getPapers().size() - 1) {
                    return paper = event.getPapers().get(i + 1);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
