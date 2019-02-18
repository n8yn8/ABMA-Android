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
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.ConvertUtil;
import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.view.adapter.PaperListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class EventActivity extends AppCompatActivity {

    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_PAPER_ID = "paper_id";

    private final String TAG = "EventActivity";
    Event event;
    Note note;
    Paper paper;
    List<Paper> eventPapers;

    TextView dayTextView;
    TextView dateTextView;
    TextView titleTextView;
    TextView subtitleTextView;
    TextView detailTextView;
    TextView timeTextView;
    TextView placeTextView;
    EditText noteEditText;
    Button saveButton;

    AppDatabase db;

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

        db = AppDatabase.getInstance(getApplicationContext());

        dayTextView = findViewById(R.id.dayTextView);
        dateTextView = findViewById(R.id.dateTextView);
        titleTextView = findViewById(R.id.titleTextView);
        subtitleTextView = findViewById(R.id.subtitleTextView);
        detailTextView = findViewById(R.id.detailTextView);
        timeTextView = findViewById(R.id.timeTextView);
        placeTextView = findViewById(R.id.placeTextView);
        noteEditText = findViewById(R.id.noteEditText);

        event = db.eventDao().getEventById(getIntent().getStringExtra(EXTRA_EVENT_ID));
        eventPapers = db.paperDao().getPapers(event.objectId);
        paper = db.paperDao().getPaperById(getIntent().getStringExtra(EXTRA_PAPER_ID));
        displayEvent();

        ImageButton backButton = findViewById(R.id.backEventButton);
        ImageButton nextButton = findViewById(R.id.nextEventButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paper != null) {
                    Paper prevPaper = getPrevPaper();
                    if (prevPaper != null) {
                        paper = prevPaper;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "First paper reached", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Event tempEvent = db.eventDao().getEventBefore(event.startDate);
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
                    Paper nextPaper = getNextPaper();
                    if (nextPaper != null) {
                        paper = nextPaper;
                        displayEvent();
                    } else {
                        Toast.makeText(getApplicationContext(), "Last paper reached", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Event tempEvent = db.eventDao().getEventAfter(event.startDate);
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
                    eventId = event.objectId;
                }
                String paperId = null;
                if (paper != null) {
                    paperId = paper.objectId;
                }
                String noteContent = noteEditText.getText().toString();

                if (!noteContent.equals("")) {
                    if (note == null) {
                        note = new Note();
                    }
                    note.content = noteContent;
                    note.eventId = eventId;
                    note.paperId = paperId;
                    db.noteDao().insert(note);
                    DbManager.getInstance().addNote(ConvertUtil.convert(note), new DbManager.OnNoteSavedCallback() {
                        @Override
                        public void noteSaved(@Nullable BNote note, String error) {
                            if (error == null && note != null) {
                                db.noteDao().insert(ConvertUtil.convert(note));
                                Toast.makeText(EventActivity.this, "This note has been saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    if (note != null) {
                        db.noteDao().delete(note);
                        //TODO delete from server
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
        Date date = new Date(event.startDate);
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
        dayFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        dayTextView.setText(dayFormatter.format(date).toUpperCase());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d");
        dateTextView.setText(dateFormatter.format(date));


        timeTextView.setText(Utils.getTimes(event));
        placeTextView.setText(event.place);

        List<Paper> papers = db.paperDao().getPapers(event.objectId);

        if (paper == null) {
            titleTextView.setText(event.title);
            subtitleTextView.setText(event.subtitle);
            detailTextView.setText(event.details);
            detailTextView.setMovementMethod(new ScrollingMovementMethod());
            detailTextView.scrollTo(0,0);
            final PaperListAdapter adapter = new PaperListAdapter(this, papers);
            ListView papersListView = findViewById(R.id.papersListView);
            papersListView.setAdapter(adapter);
            papersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Paper paper = adapter.getItem(position);
                    if (paper != null) {
                        EventActivity.start(EventActivity.this, event.objectId, paper.objectId);
                    }
                }
            });
            note = db.noteDao().getNote(event.objectId, null);
        } else {
            titleTextView.setText(paper.title);
            subtitleTextView.setText(paper.author);
            detailTextView.setText(paper.synopsis);
            detailTextView.setMovementMethod(new ScrollingMovementMethod());
            detailTextView.scrollTo(0,0);
            note = db.noteDao().getNote(event.objectId, paper.objectId);
        }
        if (note != null) {
            noteEditText.setText(note.content);
        } else {
            noteEditText.setText("");
        }
    }

    @Nullable
    private Paper getPrevPaper() {
        for (int i = 0; i < eventPapers.size(); i++) {
            Paper checkPaper = eventPapers.get(i);
            if (checkPaper.objectId.equals(paper.objectId)) {
                if (i > 0) {
                    return paper = eventPapers.get(i - 1);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Nullable
    private Paper getNextPaper() {
        for (int i = 0; i < eventPapers.size(); i++) {
            Paper checkPaper = eventPapers.get(i);
            if (checkPaper.objectId.equals(paper.objectId)) {
                if (i < eventPapers.size() - 1) {
                    return paper = eventPapers.get(i + 1);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
